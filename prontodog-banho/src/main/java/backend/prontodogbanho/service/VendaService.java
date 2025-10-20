package backend.prontodogbanho.service;

import backend.prontodogbanho.dto.*;
import backend.prontodogbanho.model.*;
import backend.prontodogbanho.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final VendaItemRepository vendaItemRepository;
    private final VendaBaixaRepository vendaBaixaRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AnimalServicoRepository animalServicoRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;
    private final AnimalRepository animalRepository;
    private final ServicoRepository servicoRepository;
    private final BanhoIndividualRepository banhoIndividualRepository;
    private final ServicoAdicionalRepository servicoAdicionalRepository;

    public VendaService(
            VendaRepository vendaRepository,
            VendaItemRepository vendaItemRepository,
            VendaBaixaRepository vendaBaixaRepository,
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            AnimalServicoRepository animalServicoRepository,
            FormaPagamentoRepository formaPagamentoRepository,
            AnimalRepository animalRepository,
            ServicoRepository servicoRepository,
            BanhoIndividualRepository banhoIndividualRepository,
            ServicoAdicionalRepository servicoAdicionalRepository) {
        this.vendaRepository = vendaRepository;
        this.vendaItemRepository = vendaItemRepository;
        this.vendaBaixaRepository = vendaBaixaRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.animalServicoRepository = animalServicoRepository;
        this.formaPagamentoRepository = formaPagamentoRepository;
        this.animalRepository = animalRepository;
        this.servicoRepository = servicoRepository;
        this.banhoIndividualRepository = banhoIndividualRepository;
        this.servicoAdicionalRepository = servicoAdicionalRepository;
    }

    // ============================================
    // MÉTODOS DE LISTAGEM
    // ============================================

    public List<VendaResumoDTO> listarTodas() {
        return vendaRepository.findAll().stream()
                .map(this::converterParaResumoDTO)
                .collect(Collectors.toList());
    }

    public List<VendaResumoDTO> listarPorCliente(Long clienteId) {
        return vendaRepository.findByCliente_IdOrderByDataVendaDesc(clienteId).stream()
                .map(this::converterParaResumoDTO)
                .collect(Collectors.toList());
    }

    public List<VendaResumoDTO> listarPorStatus(String status) {
        return vendaRepository.findByStatusVenda(status).stream()
                .map(this::converterParaResumoDTO)
                .collect(Collectors.toList());
    }

    public Optional<VendaCompletoDTO> buscarPorId(Long id) {
        return vendaRepository.findById(id)
                .map(this::converterParaCompletoDTO);
    }

    public Optional<VendaCompletoDTO> buscarPorCodigo(Long codigoVenda) {
        return vendaRepository.findByCodigoVenda(codigoVenda)
                .map(this::converterParaCompletoDTO);
    }

    // ============================================
    // CRIAR VENDA
    // ============================================

    @Transactional
    public VendaCompletoDTO criarVenda(CriarVendaDTO dto) {
        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        // Validar usuário
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + dto.getUsuarioId()));

        // Criar venda
        Venda venda = new Venda();
        venda.setCliente(cliente);
        venda.setUsuario(usuario);
        venda.setTipoVenda(dto.getTipoVenda() != null ? dto.getTipoVenda() : "presencial");
        venda.setDesconto(dto.getDesconto() != null ? dto.getDesconto() : BigDecimal.ZERO);
        venda.setObservacoes(dto.getObservacoes());
        venda.setDataVenda(LocalDateTime.now());
        venda.setStatusVenda("em_aberto");

        // Gerar código da venda usando a sequência
        Long codigoVenda = vendaRepository.getProximoCodigoVenda();
        venda.setCodigoVenda(codigoVenda);

        // Salvar venda para gerar ID
        venda = vendaRepository.save(venda);

        // Adicionar itens se houver
        BigDecimal valorBruto = BigDecimal.ZERO;

        if (dto.getItens() != null && !dto.getItens().isEmpty()) {
            for (CriarVendaDTO.ItemVendaDTO itemDTO : dto.getItens()) {
                VendaItem item = adicionarItemInterno(venda, itemDTO);
                valorBruto = valorBruto.add(item.getValorFinalItem());
            }
        } else if (dto.getAnimalServicoIds() != null && !dto.getAnimalServicoIds().isEmpty()) {
            for (Long animalServicoId : dto.getAnimalServicoIds()) {
                VendaItem item = adicionarItemSimples(venda, animalServicoId);
                valorBruto = valorBruto.add(item.getValorFinalItem());
            }
        }

        // Atualizar valores da venda
        venda.setValorBruto(valorBruto);
        venda.setValorTotal(valorBruto.subtract(venda.getDesconto()));
        venda.setValorPendente(venda.getValorTotal());
        venda.setValorPago(BigDecimal.ZERO);

        venda = vendaRepository.save(venda);

        // Buscar novamente para garantir que o codigoVenda foi populado
        venda = vendaRepository.findById(venda.getId()).orElse(venda);

        return converterParaCompletoDTO(venda);
    }

    // ============================================
    // ADICIONAR ITENS
    // ============================================

    @Transactional
    public VendaCompletoDTO adicionarItem(Long vendaId, Long animalServicoId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + vendaId));

        // Guardar o valor pago antes de adicionar o item
        BigDecimal valorPagoAntes = venda.getValorPago();

        // Adicionar o item
        adicionarItemSimples(venda, animalServicoId);

        // Recalcular valores da venda
        recalcularValoresVenda(venda);

        // Se a venda tinha pagamentos, redistribuir proporcionalmente aos itens
        if (valorPagoAntes.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("💸 Redistribuindo pagamentos existentes (R$ " + valorPagoAntes + ") aos itens...");
            redistribuirPagamentosExistentes(venda.getId());
        }

        return converterParaCompletoDTO(venda);
    }

    @Transactional
    public VendaCompletoDTO adicionarItemNovo(Long vendaId, CriarVendaDTO.ItemVendaDTO itemDTO) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + vendaId));

        // Guardar o valor pago antes de adicionar o item
        BigDecimal valorPagoAntes = venda.getValorPago();

        // Adicionar o item
        adicionarItemInterno(venda, itemDTO);

        // Recalcular valores da venda
        recalcularValoresVenda(venda);

        // Se a venda tinha pagamentos, redistribuir proporcionalmente aos itens
        if (valorPagoAntes.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println("💸 Redistribuindo pagamentos existentes (R$ " + valorPagoAntes + ") aos itens...");
            redistribuirPagamentosExistentes(venda.getId());
        }

        return converterParaCompletoDTO(venda);
    }

    private VendaItem adicionarItemSimples(Venda venda, Long animalServicoId) {
        // Verificar se animal_servico existe
        AnimalServico animalServico = animalServicoRepository.findById(animalServicoId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + animalServicoId));

        // Verificar se já não está em outra venda
        if (vendaItemRepository.existsByAnimalServico_Id(animalServicoId)) {
            throw new RuntimeException("Este serviço já está vinculado a outra venda");
        }

        // Criar item
        VendaItem item = new VendaItem();
        item.setVenda(venda);
        item.setAnimalServico(animalServico);

        // Calcular valor (serviço principal + adicionais)
        BigDecimal valorItem = animalServico.getValorTotalComAdicionais();
        item.setValorItem(valorItem);
        item.setDescontoItem(BigDecimal.ZERO);
        item.setValorFinalItem(valorItem);

        // Vincular animal_servico à venda e definir valor cobrado
        animalServico.setVenda(venda);
        animalServico.setValorCobrado(item.getValorFinalItem());
        animalServicoRepository.save(animalServico);

        return vendaItemRepository.save(item);
    }

    private VendaItem adicionarItemInterno(Venda venda, CriarVendaDTO.ItemVendaDTO itemDTO) {
        AnimalServico animalServico;

        // Cenário 1: AnimalServico já existe
        if (itemDTO.getAnimalServicoId() != null) {
            animalServico = animalServicoRepository.findById(itemDTO.getAnimalServicoId())
                    .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + itemDTO.getAnimalServicoId()));

            // Verificar se já não está em outra venda
            if (vendaItemRepository.existsByAnimalServico_Id(itemDTO.getAnimalServicoId())) {
                throw new RuntimeException("Este serviço já está vinculado a outra venda");
            }
        }
        // Cenário 2: Criar novo AnimalServico
        else if (itemDTO.getAnimalId() != null && itemDTO.getServicoId() != null) {
            animalServico = criarAnimalServicoParaVenda(itemDTO, venda.getUsuario().getId());
        } else {
            throw new RuntimeException("Deve fornecer animalServicoId OU (animalId + servicoId)");
        }

        // Criar item
        VendaItem item = new VendaItem();
        item.setVenda(venda);
        item.setAnimalServico(animalServico);

        // Valor customizado ou valor do serviço (já incluindo ajustes)
        BigDecimal valorItem = itemDTO.getValorItem() != null ?
                itemDTO.getValorItem() : animalServico.getValorTotalComAdicionais();

        BigDecimal descontoItem = itemDTO.getDescontoItem() != null ?
                itemDTO.getDescontoItem() : BigDecimal.ZERO;

        item.setValorItem(valorItem);
        item.setDescontoItem(descontoItem);
        item.setValorFinalItem(valorItem.subtract(descontoItem));
        item.setObservacoes(itemDTO.getObservacoes());

        // Vincular animal_servico à venda e definir valor cobrado
        animalServico.setVenda(venda);
        animalServico.setValorCobrado(item.getValorFinalItem());
        animalServicoRepository.save(animalServico);

        return vendaItemRepository.save(item);
    }

    /**
     * Cria um novo AnimalServico para ser usado na venda
     */
    private AnimalServico criarAnimalServicoParaVenda(CriarVendaDTO.ItemVendaDTO itemDTO, Long usuarioId) {
        // Buscar animal
        Animal animal = animalRepository.findById(itemDTO.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal não encontrado com ID: " + itemDTO.getAnimalId()));

        // Buscar serviço do catálogo
        Servico servico = servicoRepository.findById(itemDTO.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + itemDTO.getServicoId()));

        // Buscar usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + usuarioId));

        // Criar AnimalServico
        AnimalServico animalServico = new AnimalServico();
        animalServico.setAnimal(animal);
        animalServico.setServico(servico);
        animalServico.setUsuario(usuario);
        animalServico.setDataServico(LocalDate.now());

        // Configurações avançadas do DTO

        // Se é serviço único, marca todos os banhos como usados
        if (itemDTO.getServicoUnico() != null && itemDTO.getServicoUnico()) {
            animalServico.setBanhosUsados(servico.getQuantidade());
            System.out.println("✅ Serviço único: marcando " + servico.getQuantidade() + " banhos como usados");
        } else {
            animalServico.setBanhosUsados(itemDTO.getBanhosUsados() != null ? itemDTO.getBanhosUsados() : 0);
        }

        // Status de pagamento sempre inicia como "em_aberto" - será atualizado automaticamente quando houver baixas na venda
        animalServico.setStatusPagamento("em_aberto");

        // Data de expiração
        if (itemDTO.getDataExpiracao() != null && !itemDTO.getDataExpiracao().trim().isEmpty()) {
            try {
                animalServico.setDataExpiracao(LocalDate.parse(itemDTO.getDataExpiracao()));
            } catch (Exception e) {
                System.out.println("⚠️ Erro ao parsear dataExpiracao: " + itemDTO.getDataExpiracao());
            }
        }

        // Salvar o AnimalServico primeiro
        animalServico = animalServicoRepository.save(animalServico);
        System.out.println("✅ AnimalServico salvo na venda: ID=" + animalServico.getId() + ", banhosUsados=" + animalServico.getBanhosUsados());

        // Criar banhos individuais se datasBanhosRealizados fornecidas
        if (itemDTO.getDatasBanhosRealizados() != null && !itemDTO.getDatasBanhosRealizados().isEmpty()) {
            System.out.println("🛁 Criando " + itemDTO.getDatasBanhosRealizados().size() + " banhos individuais...");

            for (int i = 0; i < itemDTO.getDatasBanhosRealizados().size(); i++) {
                String dataString = itemDTO.getDatasBanhosRealizados().get(i);

                try {
                    LocalDate dataBanho = LocalDate.parse(dataString);

                    BanhoIndividual banho = new BanhoIndividual();
                    banho.setAnimalServico(animalServico);
                    banho.setDataBanho(dataBanho);
                    banho.setNumeroBanho(i + 1);
                    banho.setUsuario(usuario);

                    // Adicionar observação se fornecida
                    if (itemDTO.getObservacoesBanhos() != null &&
                        i < itemDTO.getObservacoesBanhos().size() &&
                        itemDTO.getObservacoesBanhos().get(i) != null) {
                        banho.setObservacoes(itemDTO.getObservacoesBanhos().get(i));
                    }

                    BanhoIndividual banhoSalvo = banhoIndividualRepository.save(banho);
                    System.out.println("  ✅ Banho " + (i + 1) + " salvo: data=" + banhoSalvo.getDataBanho() + ", ID=" + banhoSalvo.getId());

                } catch (Exception e) {
                    System.err.println("  ❌ Erro ao criar banho " + (i + 1) + " com data " + dataString + ": " + e.getMessage());
                }
            }
        }

        // Criar serviços adicionais se fornecidos
        if (itemDTO.getServicosAdicionais() != null && !itemDTO.getServicosAdicionais().isEmpty()) {
            System.out.println("🔧 Criando " + itemDTO.getServicosAdicionais().size() + " serviços adicionais...");

            for (CriarVendaDTO.ServicoAdicionalDTO adicionalDTO : itemDTO.getServicosAdicionais()) {
                try {
                    // Buscar o serviço adicional do catálogo
                    Servico servicoAdicional = servicoRepository.findById(adicionalDTO.getServicoId())
                            .orElseThrow(() -> new RuntimeException("Serviço adicional não encontrado com ID: " + adicionalDTO.getServicoId()));

                    // Criar ServicoAdicional
                    ServicoAdicional servicoAdicionalEntity = new ServicoAdicional();
                    servicoAdicionalEntity.setAnimalServicoPrincipal(animalServico);
                    servicoAdicionalEntity.setServicoAdicional(servicoAdicional);
                    servicoAdicionalEntity.setQuantidadeAdicional(adicionalDTO.getQuantidade() != null ? adicionalDTO.getQuantidade() : 1);
                    servicoAdicionalEntity.setValorUnitario(adicionalDTO.getValorUnitario());

                    // Herdar status e data de pagamento do pai
                    servicoAdicionalEntity.setStatusPagamento(animalServico.getStatusPagamento());
                    servicoAdicionalEntity.setDataPagamento(animalServico.getDataPagamento());

                    servicoAdicionalEntity.setObservacoes(adicionalDTO.getObservacoes());
                    servicoAdicionalEntity.setUsuario(usuario);
                    servicoAdicionalEntity.setDataAdicao(java.time.LocalDateTime.now());

                    ServicoAdicional servicoAdicionalSalvo = servicoAdicionalRepository.save(servicoAdicionalEntity);
                    System.out.println("  ✅ Serviço adicional salvo: " + servicoAdicional.getNome() +
                                     " (qtd=" + servicoAdicionalSalvo.getQuantidadeAdicional() +
                                     ", valor=" + servicoAdicionalSalvo.getValorTotal() + ")");

                } catch (Exception e) {
                    System.err.println("  ❌ Erro ao criar serviço adicional ID " + adicionalDTO.getServicoId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        return animalServico;
    }

    // ============================================
    // REMOVER ITEM
    // ============================================

    @Transactional
    public VendaCompletoDTO removerItem(Long vendaId, Long itemId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + vendaId));

        VendaItem item = vendaItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado com ID: " + itemId));

        System.out.println("🗑️ Removendo item #" + itemId + " da venda #" + vendaId);
        System.out.println("  💰 Valores antes:");
        System.out.println("    - Valor item: R$ " + item.getValorFinalItem());
        System.out.println("    - Valor pago item: R$ " + item.getValorPagoItem());
        System.out.println("    - Valor pago venda: R$ " + venda.getValorPago());
        System.out.println("    - Valor total venda: R$ " + venda.getValorTotal());

        // Guardar o valor pago total da venda ANTES de remover o item
        BigDecimal valorPagoTotal = venda.getValorPago();

        // Deletar o animal_servico completamente (e seus banhos em cascata)
        if (item.getAnimalServico() != null) {
            AnimalServico animalServico = item.getAnimalServico();
            // Primeiro deletar o item da venda
            vendaItemRepository.delete(item);
            // Depois deletar o animal_servico (isso deleta banhos em cascata)
            animalServicoRepository.delete(animalServico);
        } else {
            // Se não tem animal_servico, apenas deletar o item
            vendaItemRepository.delete(item);
        }

        // Recalcular valores da venda (atualiza valor bruto, total e pendente)
        recalcularValoresVenda(venda);

        // IMPORTANTE: Redistribuir o pagamento total entre os itens restantes
        // Não subtraímos do valor pago, apenas redistribuímos proporcionalmente
        System.out.println("  💸 Redistribuindo pagamento de R$ " + valorPagoTotal + " entre os itens restantes...");

        // Zerar valorPagoItem de todos os itens restantes
        List<VendaItem> itensRestantes = vendaItemRepository.findByVenda_Id(vendaId);
        for (VendaItem itemRestante : itensRestantes) {
            itemRestante.setValorPagoItem(BigDecimal.ZERO);
            vendaItemRepository.save(itemRestante);
        }

        // Agora redistribuir o pagamento proporcionalmente aos itens restantes
        if (!itensRestantes.isEmpty() && valorPagoTotal.compareTo(BigDecimal.ZERO) > 0) {
            // Garantir que o valor pago não exceda o novo total
            BigDecimal novoValorPago = valorPagoTotal;
            if (novoValorPago.compareTo(venda.getValorTotal()) > 0) {
                novoValorPago = venda.getValorTotal();
                System.out.println("  ⚠️  Valor pago (R$ " + valorPagoTotal + ") excede novo total (R$ " + venda.getValorTotal() + "), ajustando para R$ " + novoValorPago);
            }

            venda.setValorPago(novoValorPago);
            venda.recalcularValores();
            vendaRepository.save(venda);

            // Distribuir aos itens
            distribuirPagamentoAosItens(vendaId, novoValorPago);

            // Atualizar status dos AnimalServico
            atualizarStatusItensBaseadoEmPagamento(vendaId);
        } else {
            // Se não há mais itens, zerar o valor pago
            venda.setValorPago(BigDecimal.ZERO);
            venda.recalcularValores();
            vendaRepository.save(venda);
        }

        System.out.println("  ✅ Valores após remoção e redistribuição:");
        System.out.println("    - Valor total venda: R$ " + venda.getValorTotal());
        System.out.println("    - Valor pago venda: R$ " + venda.getValorPago());
        System.out.println("    - Valor pendente venda: R$ " + venda.getValorPendente());

        return converterParaCompletoDTO(venda);
    }

    // ============================================
    // REGISTRAR BAIXA (PAGAMENTO)
    // ============================================

    @Transactional
    public VendaCompletoDTO registrarBaixa(CriarVendaBaixaDTO dto) {
        // Validar venda
        Venda venda = vendaRepository.findById(dto.getVendaId())
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + dto.getVendaId()));

        // Validar forma de pagamento
        FormaPagamento formaPagamento = formaPagamentoRepository.findById(dto.getFormaPagamentoId())
                .orElseThrow(() -> new RuntimeException("Forma de pagamento não encontrada com ID: " + dto.getFormaPagamentoId()));

        // Validar usuário
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + dto.getUsuarioId()));

        // Validar valor
        if (dto.getValorBaixa().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor da baixa deve ser maior que zero");
        }

        // Criar baixa
        VendaBaixa baixa = new VendaBaixa();
        baixa.setVenda(venda);
        baixa.setFormaPagamento(formaPagamento);
        baixa.setUsuario(usuario);
        baixa.setValorBaixa(dto.getValorBaixa());
        baixa.setNumeroParcelas(dto.getNumeroParcelas() != null ? dto.getNumeroParcelas() : 1);
        baixa.setObservacoes(dto.getObservacoes());
        baixa.setDataBaixa(LocalDateTime.now());

        if (dto.getDataPrimeiraParcelaAsLocalDate() != null) {
            baixa.setDataPrimeiraParcela(dto.getDataPrimeiraParcelaAsLocalDate());
        }

        // Salvar (PrePersist calculará taxa e valor líquido)
        baixa = vendaBaixaRepository.save(baixa);
        System.out.println("💾 Baixa manual registrada: R$ " + baixa.getValorBaixa());

        // Atualizar valores da venda
        BigDecimal valorPagoAntes = venda.getValorPago();
        venda.setValorPago(venda.getValorPago().add(baixa.getValorBaixa()));
        venda.recalcularValores();

        System.out.println("💰 Valor pago: " + valorPagoAntes + " -> " + venda.getValorPago() + " | Total: " + venda.getValorTotal() + " | isPago: " + venda.isPago());

        // Distribuir pagamento proporcionalmente aos itens
        System.out.println("💸 Distribuindo R$ " + baixa.getValorBaixa() + " aos itens...");
        distribuirPagamentoAosItens(venda.getId(), baixa.getValorBaixa());

        // Atualizar status dos AnimalServico baseado no pagamento dos itens
        atualizarStatusItensBaseadoEmPagamento(venda.getId());

        vendaRepository.save(venda);

        return converterParaCompletoDTO(venda);
    }

    // ============================================
    // REMOVER BAIXA
    // ============================================

    @Transactional
    public VendaCompletoDTO removerBaixa(Long vendaId, Long baixaId) {
        System.out.println("🗑️ Removendo baixa #" + baixaId + " da venda #" + vendaId);

        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + vendaId));

        VendaBaixa baixa = vendaBaixaRepository.findById(baixaId)
                .orElseThrow(() -> new RuntimeException("Baixa não encontrada com ID: " + baixaId));

        BigDecimal valorBaixa = baixa.getValorBaixa();
        System.out.println("  💵 Valor da baixa: " + valorBaixa);

        vendaBaixaRepository.delete(baixa);
        vendaBaixaRepository.flush(); // Força commit imediato no banco
        System.out.println("  ✅ Baixa deletada do banco");

        // Atualizar valores da venda
        BigDecimal valorPagoAntes = venda.getValorPago();
        BigDecimal novoValorPago = venda.getValorPago().subtract(valorBaixa);

        // Garantir que o valor pago nunca fique negativo
        if (novoValorPago.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("  ⚠️ Valor pago ficaria negativo! Ajustando para zero.");
            novoValorPago = BigDecimal.ZERO;
        }

        venda.setValorPago(novoValorPago);
        venda.recalcularValores();
        vendaRepository.save(venda);
        vendaRepository.flush(); // Força commit imediato no banco

        System.out.println("  💰 Valor pago antes: " + valorPagoAntes + " | depois: " + venda.getValorPago());
        System.out.println("  📊 Status: " + venda.getStatusVenda());

        // Remover pagamento proporcional dos itens
        System.out.println("  ↩️ Removendo R$ " + valorBaixa + " dos itens proporcionalmente...");
        removerPagamentoDosItens(venda.getId(), valorBaixa);

        // Atualizar status dos AnimalServico
        atualizarStatusItensBaseadoEmPagamento(venda.getId());

        // Recarregar a venda do banco para pegar dados frescos
        Venda vendaAtualizada = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        VendaCompletoDTO dto = converterParaCompletoDTO(vendaAtualizada);
        System.out.println("  📦 DTO criado com " + dto.getBaixas().size() + " baixas");

        return dto;
    }

    // ============================================
    // ATUALIZAR VENDA
    // ============================================

    @Transactional
    public VendaCompletoDTO atualizarVenda(Long id, AtualizarVendaDTO dto) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + id));

        if (dto.getTipoVenda() != null) {
            venda.setTipoVenda(dto.getTipoVenda());
        }

        if (dto.getDesconto() != null) {
            venda.setDesconto(dto.getDesconto());
            recalcularValoresVenda(venda);
        }

        if (dto.getObservacoes() != null) {
            venda.setObservacoes(dto.getObservacoes());
        }

        if (dto.getStatusVenda() != null) {
            venda.setStatusVenda(dto.getStatusVenda());
        }

        venda = vendaRepository.save(venda);
        return converterParaCompletoDTO(venda);
    }

    // ============================================
    // CANCELAR VENDA
    // ============================================

    @Transactional
    public VendaCompletoDTO cancelarVenda(Long id, CancelarVendaDTO dto) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + id));

        if (venda.isCancelado()) {
            throw new RuntimeException("Venda já está cancelada");
        }

        venda.setStatusVenda("cancelado");
        venda.setCanceladoEm(LocalDateTime.now());
        venda.setMotivoCancelamento(dto.getMotivoCancelamento());

        venda = vendaRepository.save(venda);
        return converterParaCompletoDTO(venda);
    }

    // ============================================
    // EXCLUIR VENDA PERMANENTEMENTE
    // ============================================

    @Transactional
    public void excluirVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com ID: " + id));

        // 1. Desvincular todos os animal_servico desta venda ANTES de deletar os itens
        List<VendaItem> itens = vendaItemRepository.findByVenda_Id(id);
        for (VendaItem item : itens) {
            if (item.getAnimalServico() != null) {
                AnimalServico animalServico = item.getAnimalServico();
                animalServico.setVenda(null);
                animalServicoRepository.save(animalServico);
            }
        }

        // 2. Deletar manualmente os itens (não podemos deixar cascata por causa do NOT NULL)
        vendaItemRepository.deleteAll(itens);

        // 3. Deletar manualmente as baixas
        List<VendaBaixa> baixas = vendaBaixaRepository.findByVenda_IdOrderByDataBaixaDesc(id);
        vendaBaixaRepository.deleteAll(baixas);

        // 4. Agora podemos deletar a venda
        vendaRepository.delete(venda);
    }

    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================

    private void recalcularValoresVenda(Venda venda) {
        // Buscar todos os itens
        List<VendaItem> itens = vendaItemRepository.findByVenda_Id(venda.getId());

        // Calcular valor bruto (soma dos itens)
        BigDecimal valorBruto = itens.stream()
                .map(VendaItem::getValorFinalItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        venda.setValorBruto(valorBruto);
        venda.recalcularValores();
        vendaRepository.save(venda);
    }

    /**
     * Redistribui os pagamentos existentes da venda proporcionalmente a todos os itens
     * (usado quando um novo item é adicionado a uma venda com pagamentos)
     */
    private void redistribuirPagamentosExistentes(Long vendaId) {
        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        List<VendaItem> itens = vendaItemRepository.findByVenda_Id(vendaId);

        // 1. Zerar valorPagoItem de todos os itens
        System.out.println("  🔄 Zerando valorPagoItem de todos os itens...");
        for (VendaItem item : itens) {
            item.setValorPagoItem(BigDecimal.ZERO);
            vendaItemRepository.save(item);
        }

        // 2. Redistribuir o valorPago da venda proporcionalmente
        System.out.println("  📊 Redistribuindo R$ " + venda.getValorPago() + " proporcionalmente...");
        distribuirPagamentoAosItens(vendaId, venda.getValorPago());

        // 3. Atualizar status dos AnimalServico baseado nos novos valores
        System.out.println("  ✅ Atualizando status dos itens...");
        atualizarStatusItensBaseadoEmPagamento(vendaId);
    }

    /**
     * Distribui um pagamento proporcionalmente aos itens da venda
     */
    private void distribuirPagamentoAosItens(Long vendaId, BigDecimal valorPagamento) {
        List<VendaItem> itens = vendaItemRepository.findByVenda_Id(vendaId);
        BigDecimal valorTotalItens = itens.stream()
                .map(VendaItem::getValorFinalItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("  📊 Total dos itens: R$ " + valorTotalItens);

        if (valorTotalItens.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("  ⚠️ Valor total dos itens é zero, nada a distribuir");
            return;
        }

        for (VendaItem item : itens) {
            // Calcular proporção deste item no total
            BigDecimal proporcao = item.getValorFinalItem()
                    .divide(valorTotalItens, 6, java.math.RoundingMode.HALF_UP);

            // Calcular quanto deste pagamento vai para este item
            BigDecimal pagamentoItem = valorPagamento.multiply(proporcao)
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            // Atualizar valor pago do item (não pode exceder o valor final)
            BigDecimal novoValorPago = item.getValorPagoItem().add(pagamentoItem);
            if (novoValorPago.compareTo(item.getValorFinalItem()) > 0) {
                novoValorPago = item.getValorFinalItem();
            }

            System.out.println("  💸 Item #" + item.getId() + ": R$ " + item.getValorPagoItem() +
                             " -> R$ " + novoValorPago + " (+" + pagamentoItem + ")");

            item.setValorPagoItem(novoValorPago);
            vendaItemRepository.save(item);
        }
    }

    /**
     * Remove um pagamento proporcionalmente dos itens da venda
     */
    private void removerPagamentoDosItens(Long vendaId, BigDecimal valorRemocao) {
        List<VendaItem> itens = vendaItemRepository.findByVenda_Id(vendaId);
        BigDecimal valorTotalItens = itens.stream()
                .map(VendaItem::getValorFinalItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (valorTotalItens.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        for (VendaItem item : itens) {
            // Calcular proporção deste item no total
            BigDecimal proporcao = item.getValorFinalItem()
                    .divide(valorTotalItens, 6, java.math.RoundingMode.HALF_UP);

            // Calcular quanto remover deste item
            BigDecimal remocaoItem = valorRemocao.multiply(proporcao)
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            // Atualizar valor pago do item (não pode ficar negativo)
            BigDecimal novoValorPago = item.getValorPagoItem().subtract(remocaoItem);
            if (novoValorPago.compareTo(BigDecimal.ZERO) < 0) {
                novoValorPago = BigDecimal.ZERO;
            }

            System.out.println("  ↩️ Item #" + item.getId() + ": R$ " + item.getValorPagoItem() +
                             " -> R$ " + novoValorPago + " (-" + remocaoItem + ")");

            item.setValorPagoItem(novoValorPago);
            vendaItemRepository.save(item);
        }
    }

    /**
     * Atualiza o status dos AnimalServico baseado no pagamento dos itens
     */
    private void atualizarStatusItensBaseadoEmPagamento(Long vendaId) {
        List<VendaItem> itens = vendaItemRepository.findByVenda_Id(vendaId);

        for (VendaItem item : itens) {
            if (item.getAnimalServico() != null) {
                AnimalServico animalServico = item.getAnimalServico();
                String statusAnterior = animalServico.getStatusPagamento();

                // Determinar novo status baseado no percentual pago
                String novoStatus;
                if (item.isPago()) {
                    novoStatus = "pago";
                    if (animalServico.getDataPagamento() == null) {
                        animalServico.setDataPagamento(LocalDate.now());
                    }
                } else if (item.isParcial()) {
                    novoStatus = "parcial";
                    animalServico.setDataPagamento(null);
                } else {
                    novoStatus = "em_aberto";
                    animalServico.setDataPagamento(null);
                }

                if (!novoStatus.equals(statusAnterior)) {
                    System.out.println("  🔄 AnimalServico #" + animalServico.getId() +
                                     ": " + statusAnterior + " -> " + novoStatus +
                                     " (" + item.getPercentualPago() + "%)");
                    animalServico.setStatusPagamento(novoStatus);
                    animalServicoRepository.save(animalServico);
                }
            }
        }
    }

    /**
     * Verifica se um AnimalServico pode ser marcado como pago individualmente
     * NOVO COMPORTAMENTO: AnimalServicos que fazem parte de uma venda NÃO podem ser marcados como pagos individualmente
     * O pagamento deve ser feito pela venda
     */
    public boolean podeMarcarComoPagoIndividual(Long animalServicoId) {
        AnimalServico animalServico = animalServicoRepository.findById(animalServicoId).orElse(null);

        if (animalServico == null) {
            return false;
        }

        // Se está em uma venda, NÃO pode marcar como pago individualmente
        return animalServico.getVenda() == null;
    }

    // ============================================
    // CONVERSORES PARA DTO
    // ============================================

    private VendaResumoDTO converterParaResumoDTO(Venda venda) {
        VendaResumoDTO dto = new VendaResumoDTO();
        dto.setId(venda.getId());
        dto.setCodigoVenda(venda.getCodigoVenda());
        dto.setDataVenda(venda.getDataVenda());
        dto.setTipoVenda(venda.getTipoVenda());
        dto.setStatusVenda(venda.getStatusVenda());

        dto.setClienteId(venda.getClienteId());
        dto.setClienteNome(venda.getClienteNome());

        dto.setUsuarioId(venda.getUsuarioId());
        dto.setUsuarioNome(venda.getUsuarioNome());

        dto.setValorTotal(venda.getValorTotal());
        dto.setValorPago(venda.getValorPago());
        dto.setValorPendente(venda.getValorPendente());

        dto.setQuantidadeItens(venda.getQuantidadeItens());
        dto.setQuantidadeBaixas(venda.getQuantidadeBaixas());
        dto.setPercentualPago(venda.getPercentualPago());

        return dto;
    }

    private VendaCompletoDTO converterParaCompletoDTO(Venda venda) {
        VendaCompletoDTO dto = new VendaCompletoDTO();
        dto.setId(venda.getId());
        dto.setCodigoVenda(venda.getCodigoVenda());
        dto.setDataVenda(venda.getDataVenda());
        dto.setTipoVenda(venda.getTipoVenda());
        dto.setStatusVenda(venda.getStatusVenda());

        // Cliente
        dto.setClienteId(venda.getClienteId());
        dto.setClienteNome(venda.getClienteNome());
        if (venda.getCliente() != null) {
            dto.setClienteCpf(venda.getCliente().getCpf());
        }

        // Usuário
        dto.setUsuarioId(venda.getUsuarioId());
        dto.setUsuarioNome(venda.getUsuarioNome());

        // Valores
        dto.setValorBruto(venda.getValorBruto());
        dto.setDesconto(venda.getDesconto());
        dto.setValorTotal(venda.getValorTotal());
        dto.setValorPago(venda.getValorPago());
        dto.setValorPendente(venda.getValorPendente());
        dto.setPercentualPago(venda.getPercentualPago());

        dto.setQuantidadeItens(venda.getQuantidadeItens());
        dto.setQuantidadeBaixas(venda.getQuantidadeBaixas());
        dto.setObservacoes(venda.getObservacoes());

        // Cancelamento
        dto.setCanceladoEm(venda.getCanceladoEm());
        dto.setMotivoCancelamento(venda.getMotivoCancelamento());

        // Itens
        List<VendaItem> itens = vendaItemRepository.findByVenda_Id(venda.getId());
        dto.setItens(itens.stream().map(this::converterItemParaDTO).collect(Collectors.toList()));

        // Baixas
        List<VendaBaixa> baixas = vendaBaixaRepository.findByVenda_IdOrderByDataBaixaDesc(venda.getId());
        dto.setBaixas(baixas.stream().map(this::converterBaixaParaDTO).collect(Collectors.toList()));

        return dto;
    }

    private VendaCompletoDTO.ItemDetalhadoDTO converterItemParaDTO(VendaItem item) {
        VendaCompletoDTO.ItemDetalhadoDTO dto = new VendaCompletoDTO.ItemDetalhadoDTO();
        dto.setId(item.getId());
        dto.setAnimalServicoId(item.getAnimalServicoId());

        if (item.getAnimalServico() != null) {
            AnimalServico as = item.getAnimalServico();

            if (as.getAnimal() != null) {
                dto.setAnimalId(as.getAnimal().getId());
                dto.setAnimalNome(as.getAnimal().getNome());
                dto.setAnimalTipo(as.getAnimal().getTipo());
                dto.setAnimalRaca(as.getAnimal().getRaca());
            }

            if (as.getServico() != null) {
                dto.setServicoId(as.getServico().getId());
                dto.setServicoNome(as.getServico().getNome());
            }

            dto.setValorAdicionais(as.getServicosAdicionais() != null ?
                    as.getServicosAdicionais().stream()
                            .map(ServicoAdicional::getValorTotal)
                            .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO);
            dto.setQuantidadeAdicionais(as.getQuantidadeAdicionais());
        }

        dto.setValorItem(item.getValorItem());
        dto.setDescontoItem(item.getDescontoItem());
        dto.setValorFinalItem(item.getValorFinalItem());
        dto.setObservacoes(item.getObservacoes());

        return dto;
    }

    private VendaCompletoDTO.BaixaDetalhadaDTO converterBaixaParaDTO(VendaBaixa baixa) {
        VendaCompletoDTO.BaixaDetalhadaDTO dto = new VendaCompletoDTO.BaixaDetalhadaDTO();
        dto.setId(baixa.getId());
        dto.setDataBaixa(baixa.getDataBaixa());

        dto.setFormaPagamentoId(baixa.getFormaPagamentoId());
        dto.setFormaPagamentoNome(baixa.getFormaPagamentoNome());
        dto.setFormaPagamentoTipo(baixa.getFormaPagamentoTipo());

        dto.setValorBaixa(baixa.getValorBaixa());
        dto.setValorTaxa(baixa.getValorTaxa());
        dto.setValorLiquido(baixa.getValorLiquido());

        dto.setNumeroParcelas(baixa.getNumeroParcelas());
        if (baixa.getDataPrimeiraParcela() != null) {
            dto.setDataPrimeiraParcela(baixa.getDataPrimeiraParcela().toString());
        }
        dto.setValorParcela(baixa.getValorParcela());

        dto.setUsuarioId(baixa.getUsuarioId());
        dto.setUsuarioNome(baixa.getUsuarioNome());
        dto.setObservacoes(baixa.getObservacoes());

        return dto;
    }
}

