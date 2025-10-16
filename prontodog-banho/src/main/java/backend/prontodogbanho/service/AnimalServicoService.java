package backend.prontodogbanho.service;

import backend.prontodogbanho.dto.CriarAnimalServicoDTO;
import backend.prontodogbanho.dto.CriarServicoAdicionalDTO;
import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.model.BanhoIndividual;
import backend.prontodogbanho.model.Servico;
import backend.prontodogbanho.model.ServicoAdicional;
import backend.prontodogbanho.model.Usuario;
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
                        servicoAdicionalDTO.statusPagamento(),
                        servicoAdicionalDTO.dataPagamento(),
                        servicoAdicionalDTO.observacoes(),
                        servicoAdicionalDTO.usuarioId()
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
        servicoAdicionalEntity.setStatusPagamento(dto.statusPagamento() != null ? dto.statusPagamento() : "em_aberto");

        // Converter string para LocalDate se fornecida
        if (dto.dataPagamento() != null && !dto.dataPagamento().trim().isEmpty()) {
            try {
                servicoAdicionalEntity.setDataPagamento(java.time.LocalDate.parse(dto.dataPagamento()));
            } catch (Exception e) {
                System.err.println("❌ Erro ao parsear data de pagamento: " + dto.dataPagamento());
                // Data inválida, deixa null
            }
        }

        servicoAdicionalEntity.setObservacoes(dto.observacoes());
        servicoAdicionalEntity.setUsuario(usuario);
        servicoAdicionalEntity.setDataAdicao(java.time.LocalDateTime.now());

        return servicoAdicionalEntity;
    }

    public void deletar(Long id) {
        animalServicoRepository.deleteById(id);
    }

    @Transactional
    public AnimalServico atualizarTudo(Long id, AnimalServico novosDados) {
        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServicoExistente = animalServicoOptional.get();

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

            return animalServicoRepository.save(animalServicoExistente);
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
     * @param id ID do serviço
     * @param dataPagamento data do pagamento
     * @return serviço atualizado
     */
    @Transactional
    public AnimalServico marcarComoPago(Long id, LocalDate dataPagamento) {
        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServico = animalServicoOptional.get();
            animalServico.setStatusPagamento("pago");
            animalServico.setDataPagamento(dataPagamento != null ? dataPagamento : LocalDate.now());

            return animalServicoRepository.save(animalServico);
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }

    /**
     * Marca um serviço como cancelado
     * @param id ID do serviço
     * @return serviço atualizado
     */
    @Transactional
    public AnimalServico marcarComoCancelado(Long id) {
        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServico = animalServicoOptional.get();
            animalServico.setStatusPagamento("cancelado");
            animalServico.setDataPagamento(null);

            return animalServicoRepository.save(animalServico);
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }

    /**
     * Reativa um serviço (volta para em_aberto)
     * @param id ID do serviço
     * @return serviço atualizado
     */
    @Transactional
    public AnimalServico reativarServico(Long id) {
        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServico = animalServicoOptional.get();
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
