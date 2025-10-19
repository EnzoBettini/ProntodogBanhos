package backend.prontodogbanho.service;

import backend.prontodogbanho.dto.CriarAnimalServicoDTO;
import backend.prontodogbanho.dto.CriarServicoAdicionalDTO;
import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.model.BanhoIndividual;
import backend.prontodogbanho.model.Servico;
import backend.prontodogbanho.model.ServicoAdicional;
import backend.prontodogbanho.model.Usuario;
import backend.prontodogbanho.model.Venda;
import backend.prontodogbanho.model.VendaItem;
import backend.prontodogbanho.repository.AnimalRepository;
import backend.prontodogbanho.repository.AnimalServicoRepository;
import backend.prontodogbanho.repository.BanhoIndividualRepository;
import backend.prontodogbanho.repository.ServicoAdicionalRepository;
import backend.prontodogbanho.repository.ServicoRepository;
import backend.prontodogbanho.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AnimalServicoService {

    private final AnimalServicoRepository animalServicoRepository;
    private final BanhoIndividualRepository banhoIndividualRepository;
    private final AnimalRepository animalRepository;
    private final ServicoRepository servicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoAdicionalRepository servicoAdicionalRepository;
    private VendaService vendaService;

    public AnimalServicoService(AnimalServicoRepository animalServicoRepository,
                              BanhoIndividualRepository banhoIndividualRepository,
                              AnimalRepository animalRepository,
                              ServicoRepository servicoRepository,
                              UsuarioRepository usuarioRepository,
                              ServicoAdicionalRepository servicoAdicionalRepository) {
        this.animalServicoRepository = animalServicoRepository;
        this.banhoIndividualRepository = banhoIndividualRepository;
        this.animalRepository = animalRepository;
        this.servicoRepository = servicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicoAdicionalRepository = servicoAdicionalRepository;
    }

    // Setter para injeção lazy e evitar dependência circular
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    @org.springframework.context.annotation.Lazy
    public void setVendaService(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    public List<AnimalServico> listarTodos() {
        return animalServicoRepository.findAll();
    }

    public Optional<AnimalServico> buscarPorId(Long id) {
        return animalServicoRepository.findById(id);
    }

    @Transactional
    public AnimalServico salvar(AnimalServico animalServico) {
        return animalServicoRepository.save(animalServico);
    }

    @Transactional
    public AnimalServico criarComBanhosIndividuais(CriarAnimalServicoDTO dto) {
        System.out.println("🔄 Recebendo DTO: " + dto);

        // Buscar entidades relacionadas
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal não encontrado com ID: " + dto.getAnimalId()));

        Servico servico = servicoRepository.findById(dto.getServicoId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + dto.getServicoId()));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + dto.getUsuarioId()));

        // Validar banhos usados
        if (dto.getBanhosUsados() < 0 || dto.getBanhosUsados() > servico.getQuantidade()) {
            throw new RuntimeException("Banhos usados (" + dto.getBanhosUsados() +
                ") deve estar entre 0 e " + servico.getQuantidade());
        }

        // Converter datas usando métodos seguros
        LocalDate dataServicoLocal = dto.getDataServicoAsLocalDate();
        if (dataServicoLocal == null) {
            throw new RuntimeException("Data do serviço é obrigatória e deve estar no formato YYYY-MM-DD");
        }

        System.out.println("📅 Data do serviço convertida: " + dto.getDataServico() + " -> " + dataServicoLocal);

        // Validar datas dos banhos se banhos usados > 0
        List<LocalDate> datasBanhosLocal = null;
        if (dto.getBanhosUsados() > 0) {
            if (dto.getDatasBanhosRealizados() == null ||
                dto.getDatasBanhosRealizados().size() != dto.getBanhosUsados()) {
                throw new RuntimeException("Deve fornecer exatamente " + dto.getBanhosUsados() +
                    " datas para os banhos já realizados");
            }

            datasBanhosLocal = dto.getDatasBanhosRealizadosAsLocalDate();
            if (datasBanhosLocal.size() != dto.getBanhosUsados()) {
                throw new RuntimeException("Algumas datas dos banhos são inválidas. Formato esperado: YYYY-MM-DD");
            }

            System.out.println("📅 Datas dos banhos convertidas: " + datasBanhosLocal);
        }

        // Converter data de expiração se fornecida
        LocalDate dataExpiracaoLocal = dto.getDataExpiracaoAsLocalDate();
        if (dataExpiracaoLocal != null) {
            System.out.println("📅 Data de expiração convertida: " + dto.getDataExpiracao() + " -> " + dataExpiracaoLocal);
        }

        // Converter data de pagamento se fornecida
        LocalDate dataPagamentoLocal = dto.getDataPagamentoAsLocalDate();
        String statusPagamento = dto.getStatusPagamento();
        if (statusPagamento == null || statusPagamento.trim().isEmpty()) {
            statusPagamento = "em_aberto"; // Padrão
        }

        // Criar AnimalServico
        AnimalServico animalServico = new AnimalServico();
        animalServico.setDataServico(dataServicoLocal);
        animalServico.setBanhosUsados(dto.getBanhosUsados());
        animalServico.setDataExpiracao(dataExpiracaoLocal);
        animalServico.setStatusPagamento(statusPagamento);
        animalServico.setDataPagamento(dataPagamentoLocal);
        animalServico.setAnimal(animal);
        animalServico.setServico(servico);
        animalServico.setUsuario(usuario);

        // Salvar AnimalServico primeiro
        AnimalServico animalServicoSalvo = animalServicoRepository.save(animalServico);
        System.out.println("✅ AnimalServico salvo com data: " + animalServicoSalvo.getDataServico());

        // Criar banhos individuais se banhos usados > 0
        if (dto.getBanhosUsados() > 0 && datasBanhosLocal != null) {
            for (int i = 0; i < dto.getBanhosUsados(); i++) {
                BanhoIndividual banho = new BanhoIndividual();
                banho.setAnimalServico(animalServicoSalvo);
                banho.setDataBanho(datasBanhosLocal.get(i));
                banho.setNumeroBanho(i + 1);

                // Adicionar observação se fornecida
                if (dto.getObservacoesBanhos() != null &&
                    i < dto.getObservacoesBanhos().size() &&
                    dto.getObservacoesBanhos().get(i) != null) {
                    banho.setObservacoes(dto.getObservacoesBanhos().get(i));
                }

                banho.setUsuario(usuario);

                BanhoIndividual banhoSalvo = banhoIndividualRepository.save(banho);
                System.out.println("✅ Banho " + (i + 1) + " salvo com data: " + banhoSalvo.getDataBanho());
            }
        }

        // Criar serviços adicionais se fornecidos
        if (dto.getServicosAdicionais() != null && !dto.getServicosAdicionais().isEmpty()) {
            System.out.println("🔧 Criando " + dto.getServicosAdicionais().size() + " serviços adicionais...");

            for (CriarServicoAdicionalDTO servicoAdicionalDTO : dto.getServicosAdicionais()) {
                try {
                    // Criar um novo DTO com o animalServicoPrincipalId correto
                    CriarServicoAdicionalDTO dtoCompleto = new CriarServicoAdicionalDTO(
                        animalServicoSalvo.getId(), // ← adicionar o ID que faltava
                        servicoAdicionalDTO.servicoAdicionalId(),
                        servicoAdicionalDTO.quantidade(),
                        servicoAdicionalDTO.valorUnitario(), // ← usar valorUnitario
                        servicoAdicionalDTO.observacoes(),
                        servicoAdicionalDTO.usuarioId()
                        // ❌ Removidos: statusPagamento e dataPagamento (herdam do pai automaticamente)
                    );

                    // Usar o service para criar o serviço adicional
                    ServicoAdicional servicoAdicionalSalvo = servicoAdicionalRepository.save(criarServicoAdicionalEntity(dtoCompleto, animalServicoSalvo, usuario));

                    System.out.println("✅ Serviço adicional salvo: " + servicoAdicionalSalvo.getServicoAdicional().getNome() +
                                     " - Valor: R$ " + servicoAdicionalSalvo.getValorTotal());
                } catch (Exception e) {
                    System.err.println("❌ Erro ao criar serviço adicional ID " + servicoAdicionalDTO.servicoAdicionalId() + ": " + e.getMessage());
                    e.printStackTrace();
                    // Não falha a transação inteira, apenas loga o erro
                }
            }
        }

        return animalServicoSalvo;
    }

    /**
     * Método helper para criar entidade ServicoAdicional a partir do DTO
     */
    private ServicoAdicional criarServicoAdicionalEntity(CriarServicoAdicionalDTO dto, AnimalServico animalServico, Usuario usuario) {
        // Buscar o serviço adicional
        Servico servicoAdicional = servicoRepository.findById(dto.servicoAdicionalId())
                .orElseThrow(() -> new RuntimeException("Serviço adicional não encontrado com ID: " + dto.servicoAdicionalId()));

        // Criar ServicoAdicional
        ServicoAdicional servicoAdicionalEntity = new ServicoAdicional();
        servicoAdicionalEntity.setAnimalServicoPrincipal(animalServico);
        servicoAdicionalEntity.setServicoAdicional(servicoAdicional);
        servicoAdicionalEntity.setQuantidadeAdicional(dto.quantidade() != null ? dto.quantidade() : 1);
        servicoAdicionalEntity.setValorUnitario(dto.valorUnitario());

        // 🎯 HERDAR STATUS E DATA DE PAGAMENTO DO PAI AUTOMATICAMENTE
        System.out.println("💡 Herdando status e data de pagamento do pai (AnimalServicoService):");
        System.out.println("  - Status do pai: " + animalServico.getStatusPagamento());
        System.out.println("  - Data do pai: " + animalServico.getDataPagamento());

        servicoAdicionalEntity.setStatusPagamento(animalServico.getStatusPagamento());
        servicoAdicionalEntity.setDataPagamento(animalServico.getDataPagamento());

        System.out.println("✅ Status e data herdados do pai automaticamente!");

        servicoAdicionalEntity.setObservacoes(dto.observacoes());
        servicoAdicionalEntity.setUsuario(usuario);
        servicoAdicionalEntity.setDataAdicao(java.time.LocalDateTime.now());

        return servicoAdicionalEntity;
    }

    @Transactional
    public void deletar(Long id) {
        // Busca o serviço para verificar se está em uma venda
        AnimalServico animalServico = animalServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        // Se o serviço faz parte de uma venda, precisamos remover através da venda
        if (animalServico.getVenda() != null) {
            System.out.println("🔗 Serviço faz parte da venda #" + animalServico.getVenda().getCodigoVenda());
            System.out.println("🗑️ Removendo através do VendaService para manter consistência...");

            // Buscar o VendaItem correspondente
            Venda venda = animalServico.getVenda();
            Long vendaId = venda.getId();

            // Precisamos encontrar o item da venda que corresponde a este animalServico
            // Para isso, vamos usar o repository
            if (vendaService != null) {
                // Buscar o item manualmente
                java.util.List<backend.prontodogbanho.model.VendaItem> itens =
                    venda.getItens();

                backend.prontodogbanho.model.VendaItem itemParaRemover = null;
                for (backend.prontodogbanho.model.VendaItem item : itens) {
                    if (item.getAnimalServico() != null &&
                        item.getAnimalServico().getId().equals(id)) {
                        itemParaRemover = item;
                        break;
                    }
                }

                if (itemParaRemover != null) {
                    System.out.println("✅ Item encontrado: #" + itemParaRemover.getId());
                    vendaService.removerItem(vendaId, itemParaRemover.getId());
                    System.out.println("✅ Serviço removido da venda com sucesso!");
                    return;
                } else {
                    System.err.println("❌ Item não encontrado na venda!");
                    throw new RuntimeException("Item não encontrado na venda");
                }
            } else {
                // Se vendaService não está disponível, apenas desvincular e deletar
                System.out.println("⚠️ VendaService não disponível, desvinculando e deletando...");
                animalServico.setVenda(null);
                animalServicoRepository.save(animalServico);
                animalServicoRepository.deleteById(id);
            }
        } else {
            // Serviço não faz parte de uma venda, pode deletar diretamente
            animalServicoRepository.deleteById(id);
        }
    }

    @Transactional
    public AnimalServico atualizarTudo(Long id, AnimalServico novosDados) {
        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServicoExistente = animalServicoOptional.get();

            System.out.println("🔍 DADOS RECEBIDOS do frontend:");
            System.out.println("  - novosDados.getServicoId(): " + novosDados.getServicoId());
            System.out.println("  - novosDados.getUsuarioId(): " + novosDados.getUsuarioId());
            System.out.println("  - novosDados completo: " + novosDados);

            System.out.println("🔍 ANTES da atualização:");
            System.out.println("  - ID: " + animalServicoExistente.getId());
            System.out.println("  - Animal ID: " + (animalServicoExistente.getAnimal() != null ? animalServicoExistente.getAnimal().getId() : "null"));
            System.out.println("  - Servico ID: " + (animalServicoExistente.getServico() != null ? animalServicoExistente.getServico().getId() : "null"));
            System.out.println("  - Usuario ID: " + (animalServicoExistente.getUsuario() != null ? animalServicoExistente.getUsuario().getId() : "null"));

            // ✅ Atualizar apenas campos escalares (não relacionamentos)
            if (novosDados.getDataServico() != null) {
                animalServicoExistente.setDataServico(novosDados.getDataServico());
                System.out.println("📅 Atualizando data do serviço: " + novosDados.getDataServico());
            }
            if (novosDados.getBanhosUsados() != null) {
                animalServicoExistente.setBanhosUsados(novosDados.getBanhosUsados());
                System.out.println("🛁 Atualizando banhos usados: " + novosDados.getBanhosUsados());
            }
            // dataExpiracao pode ser null (remoção da expiração)
            animalServicoExistente.setDataExpiracao(novosDados.getDataExpiracao());
            System.out.println("⏰ Atualizando data expiração: " + novosDados.getDataExpiracao());

            if (novosDados.getStatusPagamento() != null) {
                animalServicoExistente.setStatusPagamento(novosDados.getStatusPagamento());
                System.out.println("💳 Atualizando status pagamento: " + novosDados.getStatusPagamento());
            }
            // dataPagamento pode ser null (remoção da data)
            animalServicoExistente.setDataPagamento(novosDados.getDataPagamento());
            System.out.println("📆 Atualizando data pagamento: " + novosDados.getDataPagamento());

            // ✅ Atualizar relacionamentos se fornecidos
            if (novosDados.getServicoId() != null) {
                System.out.println("🔄 Tentando atualizar serviço para ID: " + novosDados.getServicoId());
                Optional<Servico> novoServico = servicoRepository.findById(novosDados.getServicoId());
                if (novoServico.isPresent()) {
                    Servico servicoAnterior = animalServicoExistente.getServico();
                    animalServicoExistente.setServico(novoServico.get());
                    System.out.println("✅ Serviço atualizado de ID " + (servicoAnterior != null ? servicoAnterior.getId() : "null") + " para ID: " + novosDados.getServicoId());
                } else {
                    System.err.println("❌ Serviço não encontrado com ID: " + novosDados.getServicoId());
                    throw new RuntimeException("Serviço não encontrado com ID: " + novosDados.getServicoId());
                }
            } else {
                System.out.println("⚠️  novosDados.getServicoId() é null - não atualizando serviço");
            }

            if (novosDados.getUsuarioId() != null) {
                System.out.println("👤 Tentando atualizar usuário para ID: " + novosDados.getUsuarioId());
                Optional<Usuario> novoUsuario = usuarioRepository.findById(novosDados.getUsuarioId());
                if (novoUsuario.isPresent()) {
                    Usuario usuarioAnterior = animalServicoExistente.getUsuario();
                    animalServicoExistente.setUsuario(novoUsuario.get());
                    System.out.println("✅ Usuário atualizado de ID " + (usuarioAnterior != null ? usuarioAnterior.getId() : "null") + " para ID: " + novosDados.getUsuarioId());
                } else {
                    System.err.println("❌ Usuário não encontrado com ID: " + novosDados.getUsuarioId());
                    throw new RuntimeException("Usuário não encontrado com ID: " + novosDados.getUsuarioId());
                }
            } else {
                System.out.println("⚠️  novosDados.getUsuarioId() é null - não atualizando usuário");
            }

            // ✅ VERIFICAÇÃO CRÍTICA: Garantir que relacionamentos não sejam null
            if (animalServicoExistente.getAnimal() == null ||
                animalServicoExistente.getServico() == null ||
                animalServicoExistente.getUsuario() == null) {

                System.err.println("❌ ERRO CRÍTICO: Relacionamentos estão null após atualização!");
                System.err.println("  - Animal: " + animalServicoExistente.getAnimal());
                System.err.println("  - Servico: " + animalServicoExistente.getServico());
                System.err.println("  - Usuario: " + animalServicoExistente.getUsuario());
                throw new RuntimeException("Relacionamentos críticos estão null - operação cancelada");
            }

            System.out.println("🔍 DEPOIS da atualização (antes do save):");
            System.out.println("  - Animal ID: " + animalServicoExistente.getAnimal().getId());
            System.out.println("  - Servico ID: " + animalServicoExistente.getServico().getId());
            System.out.println("  - Usuario ID: " + animalServicoExistente.getUsuario().getId());

            AnimalServico salvo = animalServicoRepository.save(animalServicoExistente);

            System.out.println("✅ SALVO NO BANCO:");
            System.out.println("  - ID salvo: " + salvo.getId());
            System.out.println("  - Servico ID salvo: " + (salvo.getServico() != null ? salvo.getServico().getId() : "null"));
            System.out.println("  - Usuario ID salvo: " + (salvo.getUsuario() != null ? salvo.getUsuario().getId() : "null"));
            System.out.println("✅ AnimalServicoService.atualizarTudo - Atualização concluída com sucesso!");

            return salvo;
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }

    @Transactional
    public AnimalServico atualizarComIDs(Long id, AnimalServico novosDados, Long servicoId, Long usuarioId) {
        System.out.println("🔍 SERVICE - atualizarComIDs chamado:");
        System.out.println("  - ID: " + id);
        System.out.println("  - servicoId: " + servicoId);
        System.out.println("  - usuarioId: " + usuarioId);
        System.out.println("  - novosDados: " + novosDados);

        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServicoExistente = animalServicoOptional.get();

            System.out.println("🔍 ANTES da atualização:");
            System.out.println("  - Servico ID atual: " + (animalServicoExistente.getServico() != null ? animalServicoExistente.getServico().getId() : "null"));
            System.out.println("  - Usuario ID atual: " + (animalServicoExistente.getUsuario() != null ? animalServicoExistente.getUsuario().getId() : "null"));

            // Atualizar campos básicos se fornecidos
            if (novosDados.getDataServico() != null) {
                animalServicoExistente.setDataServico(novosDados.getDataServico());
                System.out.println("📅 Atualizando data do serviço: " + novosDados.getDataServico());
            }
            if (novosDados.getDataExpiracao() != null) {
                animalServicoExistente.setDataExpiracao(novosDados.getDataExpiracao());
                System.out.println("⏰ Atualizando data expiração: " + novosDados.getDataExpiracao());
            }
            if (novosDados.getStatusPagamento() != null) {
                animalServicoExistente.setStatusPagamento(novosDados.getStatusPagamento());
                System.out.println("💳 Atualizando status pagamento: " + novosDados.getStatusPagamento());
            }
            if (novosDados.getDataPagamento() != null) {
                animalServicoExistente.setDataPagamento(novosDados.getDataPagamento());
                System.out.println("📆 Atualizando data pagamento: " + novosDados.getDataPagamento());
            }

            // Atualizar serviço se fornecido
            if (servicoId != null) {
                System.out.println("🔄 Tentando atualizar serviço para ID: " + servicoId);
                Optional<Servico> novoServico = servicoRepository.findById(servicoId);
                if (novoServico.isPresent()) {
                    Servico servicoAnterior = animalServicoExistente.getServico();
                    Servico servicoNovo = novoServico.get();

                    System.out.println("🔍 ANÁLISE DE BANHOS:");
                    System.out.println("  - Serviço anterior: " + (servicoAnterior != null ? servicoAnterior.getNome() + " (qtd: " + servicoAnterior.getQuantidade() + ")" : "null"));
                    System.out.println("  - Serviço novo: " + servicoNovo.getNome() + " (qtd: " + servicoNovo.getQuantidade() + ")");

                    // Verificar se precisa resetar banhos usados
                    Integer banhosUsadosAtuais = animalServicoExistente.getBanhosUsados();
                    Integer quantidadeNovoServico = servicoNovo.getQuantidade();

                    System.out.println("  - Banhos usados atuais: " + banhosUsadosAtuais + " (tipo: " + (banhosUsadosAtuais != null ? banhosUsadosAtuais.getClass().getSimpleName() : "null") + ")");
                    System.out.println("  - Quantidade novo serviço: " + quantidadeNovoServico + " (tipo: " + (quantidadeNovoServico != null ? quantidadeNovoServico.getClass().getSimpleName() : "null") + ")");

                    // DEBUG: Testando todas as condições
                    System.out.println("🔍 CONDIÇÕES DETALHADAS:");
                    System.out.println("  - banhosUsadosAtuais != null: " + (banhosUsadosAtuais != null));
                    System.out.println("  - quantidadeNovoServico != null: " + (quantidadeNovoServico != null));
                    if (banhosUsadosAtuais != null && quantidadeNovoServico != null) {
                        System.out.println("  - banhosUsadosAtuais > quantidadeNovoServico: " + (banhosUsadosAtuais > quantidadeNovoServico) + " (" + banhosUsadosAtuais + " > " + quantidadeNovoServico + ")");
                    }
                    System.out.println("  - CONDIÇÃO COMPLETA: " + (banhosUsadosAtuais != null && quantidadeNovoServico != null && banhosUsadosAtuais > quantidadeNovoServico));

                    if (banhosUsadosAtuais != null && quantidadeNovoServico != null && banhosUsadosAtuais > quantidadeNovoServico) {
                        System.out.println("⚠️  CONFLITO DETECTADO: " + banhosUsadosAtuais + " banhos usados > " + quantidadeNovoServico + " banhos permitidos");

                        // Verificar quantos banhos individuais existem no banco
                        Long banhosRegistrados = banhoIndividualRepository.countByAnimalServicoId(animalServicoExistente.getId());
                        System.out.println("🔍 Banhos registrados no BD: " + banhosRegistrados);

                        if (banhosRegistrados > 0) {
                            System.out.println("🗑️  DELETANDO TODOS OS BANHOS para dar flexibilidade total ao usuário");

                            // Buscar todos os banhos e deletar TODOS
                            List<BanhoIndividual> todosOsBanhos = banhoIndividualRepository.findByAnimalServicoIdOrderByNumeroBanho(animalServicoExistente.getId());

                            for (BanhoIndividual banho : todosOsBanhos) {
                                System.out.println("🗑️  Deletando banho #" + banho.getNumeroBanho() + " ID " + banho.getId() + " (data: " + banho.getDataBanho() + ")");
                                banhoIndividualRepository.delete(banho);
                            }

                            System.out.println("✅ Todos os " + banhosRegistrados + " banhos foram deletados");
                        }

                        System.out.println("🔄 EXECUTANDO RESET: setBanhosUsados(0) - usuário pode registrar novos banhos");
                        animalServicoExistente.setBanhosUsados(0);
                        System.out.println("✅ RESET CONCLUÍDO - Banhos agora: " + animalServicoExistente.getBanhosUsados() + "/" + quantidadeNovoServico);
                    } else {
                        System.out.println("✅ RESET NÃO NECESSÁRIO - Banhos cabem no novo serviço");

                        // ALTERNATIVA: Verificar se mudou de um tipo muito diferente de serviço
                        if (servicoAnterior != null && !servicoAnterior.getId().equals(servicoNovo.getId())) {
                            System.out.println("🤔 MAS... houve mudança de serviço. Vamos verificar se deve limpar mesmo assim:");

                            // Verificar quantos banhos individuais existem no banco
                            Long banhosRegistrados = banhoIndividualRepository.countByAnimalServicoId(animalServicoExistente.getId());
                            System.out.println("🔍 Banhos registrados no BD: " + banhosRegistrados);

                            // Se mudou de pacote para avulso ou vice-versa, pode ser interessante limpar
                            boolean servicoAnteriorEhPacote = servicoAnterior.getQuantidade() > 1;
                            boolean servicoNovoEhPacote = servicoNovo.getQuantidade() > 1;

                            System.out.println("🔍 Análise de tipos:");
                            System.out.println("  - Serviço anterior é pacote: " + servicoAnteriorEhPacote);
                            System.out.println("  - Serviço novo é pacote: " + servicoNovoEhPacote);
                            System.out.println("  - Mudança de tipo: " + (servicoAnteriorEhPacote != servicoNovoEhPacote));

                            if (banhosRegistrados > 0 && servicoAnteriorEhPacote != servicoNovoEhPacote) {
                                System.out.println("🧹 LIMPEZA POR MUDANÇA DE TIPO DE SERVIÇO!");

                                // Buscar todos os banhos e deletar TODOS
                                List<BanhoIndividual> todosOsBanhos = banhoIndividualRepository.findByAnimalServicoIdOrderByNumeroBanho(animalServicoExistente.getId());

                                for (BanhoIndividual banho : todosOsBanhos) {
                                    System.out.println("🗑️  Deletando banho #" + banho.getNumeroBanho() + " ID " + banho.getId() + " (mudança de tipo)");
                                    banhoIndividualRepository.delete(banho);
                                }

                                System.out.println("✅ Todos os " + banhosRegistrados + " banhos foram deletados por mudança de tipo");
                                System.out.println("🔄 Resetando contador para 0");
                                animalServicoExistente.setBanhosUsados(0);
                            }
                        }
                    }

                    animalServicoExistente.setServico(servicoNovo);
                    System.out.println("✅ Serviço atualizado de ID " + (servicoAnterior != null ? servicoAnterior.getId() : "null") + " para ID: " + servicoId);
                    System.out.println("📊 Estado final - Banhos: " + animalServicoExistente.getBanhosUsados() + "/" + quantidadeNovoServico);
                } else {
                    System.err.println("❌ Serviço não encontrado com ID: " + servicoId);
                    throw new RuntimeException("Serviço não encontrado com ID: " + servicoId);
                }
            }

            // Atualizar usuário se fornecido
            if (usuarioId != null) {
                System.out.println("👤 Tentando atualizar usuário para ID: " + usuarioId);
                Optional<Usuario> novoUsuario = usuarioRepository.findById(usuarioId);
                if (novoUsuario.isPresent()) {
                    Usuario usuarioAnterior = animalServicoExistente.getUsuario();
                    animalServicoExistente.setUsuario(novoUsuario.get());
                    System.out.println("✅ Usuário atualizado de ID " + (usuarioAnterior != null ? usuarioAnterior.getId() : "null") + " para ID: " + usuarioId);
                } else {
                    System.err.println("❌ Usuário não encontrado com ID: " + usuarioId);
                    throw new RuntimeException("Usuário não encontrado com ID: " + usuarioId);
                }
            }

            // Verificação de integridade
            if (animalServicoExistente.getAnimal() == null ||
                animalServicoExistente.getServico() == null ||
                animalServicoExistente.getUsuario() == null) {

                System.err.println("❌ ERRO CRÍTICO: Relacionamentos estão null após atualização!");
                throw new RuntimeException("Relacionamentos críticos estão null - operação cancelada");
            }

            AnimalServico salvo = animalServicoRepository.save(animalServicoExistente);

            System.out.println("✅ SALVO NO BANCO:");
            System.out.println("  - ID salvo: " + salvo.getId());
            System.out.println("  - Servico ID salvo: " + (salvo.getServico() != null ? salvo.getServico().getId() : "null"));
            System.out.println("  - Usuario ID salvo: " + (salvo.getUsuario() != null ? salvo.getUsuario().getId() : "null"));
            System.out.println("✅ AnimalServicoService.atualizarComIDs - Atualização concluída com sucesso!");

            return salvo;
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }

    // Métodos para controle de expiração de pacotes

    /**
     * Busca pacotes que vão vencer em até X dias
     * @param dias número de dias para considerar como "vai vencer"
     * @return lista de pacotes que vão vencer
     */
    public List<AnimalServico> buscarPacotesQueVaoVencer(int dias) {
        LocalDate dataLimite = LocalDate.now().plusDays(dias);
        return animalServicoRepository.findAll().stream()
                .filter(as -> as.getDataExpiracao() != null)
                .filter(as -> as.getDataExpiracao().isAfter(LocalDate.now()))
                .filter(as -> as.getDataExpiracao().isBefore(dataLimite) || as.getDataExpiracao().isEqual(dataLimite))
                .toList();
    }

    /**
     * Busca pacotes já vencidos
     * @return lista de pacotes vencidos
     */
    public List<AnimalServico> buscarPacotesVencidos() {
        LocalDate hoje = LocalDate.now();
        return animalServicoRepository.findAll().stream()
                .filter(as -> as.getDataExpiracao() != null)
                .filter(as -> as.getDataExpiracao().isBefore(hoje))
                .toList();
    }

    /**
     * Busca pacotes válidos (não vencidos ou sem data de expiração)
     * @return lista de pacotes válidos
     */
    public List<AnimalServico> buscarPacotesValidos() {
        LocalDate hoje = LocalDate.now();
        return animalServicoRepository.findAll().stream()
                .filter(as -> as.getDataExpiracao() == null ||
                            as.getDataExpiracao().isAfter(hoje) ||
                            as.getDataExpiracao().isEqual(hoje))
                .toList();
    }

    /**
     * Busca pacotes que expiram exatamente hoje
     * @return lista de pacotes que expiram hoje
     */
    public List<AnimalServico> buscarPacotesQueExpiramHoje() {
        LocalDate hoje = LocalDate.now();
        return animalServicoRepository.findAll().stream()
                .filter(as -> as.getDataExpiracao() != null)
                .filter(as -> as.getDataExpiracao().isEqual(hoje))
                .toList();
    }

    // Métodos para controle de pagamento

    /**
     * Busca serviços por status de pagamento
     * @param status "pago", "em_aberto" ou "cancelado"
     * @return lista de serviços com o status especificado
     */
    public List<AnimalServico> buscarPorStatusPagamento(String status) {
        return animalServicoRepository.findAll().stream()
                .filter(as -> status.equals(as.getStatusPagamento()))
                .toList();
    }

    /**
     * Busca serviços em aberto (não pagos)
     * @return lista de serviços em aberto
     */
    public List<AnimalServico> buscarServicosEmAberto() {
        return buscarPorStatusPagamento("em_aberto");
    }

    /**
     * Busca serviços pagos
     * @return lista de serviços pagos
     */
    public List<AnimalServico> buscarServicosPagos() {
        return buscarPorStatusPagamento("pago");
    }

    /**
     * Busca serviços cancelados
     * @return lista de serviços cancelados
     */
    public List<AnimalServico> buscarServicosCancelados() {
        return buscarPorStatusPagamento("cancelado");
    }

    /**
     * Marca um serviço como pago
     * IMPORTANTE: Serviços que fazem parte de uma venda NÃO podem ser marcados como pagos individualmente
     * O pagamento deve ser feito através da venda
     *
     * @param id ID do serviço
     * @param dataPagamento data do pagamento
     * @return serviço atualizado
     */
    @Transactional
    public AnimalServico marcarComoPago(Long id, LocalDate dataPagamento) {
        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServico = animalServicoOptional.get();

            // NOVA REGRA: Bloquear se estiver em uma venda
            if (animalServico.getVenda() != null) {
                throw new RuntimeException("❌ Este serviço faz parte da venda #" + animalServico.getVenda().getCodigoVenda() +
                    ". O pagamento deve ser registrado através da venda, não individualmente.");
            }

            animalServico.setStatusPagamento("pago");
            animalServico.setDataPagamento(dataPagamento != null ? dataPagamento : LocalDate.now());

            return animalServicoRepository.save(animalServico);
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }

    /**
     * Marca um serviço como cancelado
     * IMPORTANTE: Serviços que fazem parte de uma venda NÃO podem ter status alterado individualmente
     *
     * @param id ID do serviço
     * @return serviço atualizado
     */
    @Transactional
    public AnimalServico marcarComoCancelado(Long id) {
        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServico = animalServicoOptional.get();

            // NOVA REGRA: Bloquear se estiver em uma venda
            if (animalServico.getVenda() != null) {
                throw new RuntimeException("❌ Este serviço faz parte da venda #" + animalServico.getVenda().getCodigoVenda() +
                    ". O status de pagamento é controlado pela venda.");
            }

            animalServico.setStatusPagamento("cancelado");
            animalServico.setDataPagamento(null);

            return animalServicoRepository.save(animalServico);
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }

    /**
     * Reativa um serviço (volta para em_aberto)
     * IMPORTANTE: Serviços que fazem parte de uma venda NÃO podem ter status alterado individualmente
     *
     * @param id ID do serviço
     * @return serviço atualizado
     */
    @Transactional
    public AnimalServico reativarServico(Long id) {
        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServico = animalServicoOptional.get();

            // NOVA REGRA: Bloquear se estiver em uma venda
            if (animalServico.getVenda() != null) {
                throw new RuntimeException("❌ Este serviço faz parte da venda #" + animalServico.getVenda().getCodigoVenda() +
                    ". O status de pagamento é controlado pela venda.");
            }

            animalServico.setStatusPagamento("em_aberto");
            animalServico.setDataPagamento(null);

            return animalServicoRepository.save(animalServico);
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }

    /**
     * Busca serviços pagos em um período específico
     * @param dataInicio data de início do período
     * @param dataFim data de fim do período
     * @return lista de serviços pagos no período
     */
    public List<AnimalServico> buscarServicosPagosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return animalServicoRepository.findAll().stream()
                .filter(as -> "pago".equals(as.getStatusPagamento()))
                .filter(as -> as.getDataPagamento() != null)
                .filter(as -> !as.getDataPagamento().isBefore(dataInicio))
                .filter(as -> !as.getDataPagamento().isAfter(dataFim))
                .toList();
    }
}
