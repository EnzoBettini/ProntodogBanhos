package backend.prontodogbanho.service;

import backend.prontodogbanho.dto.CriarServicoAdicionalDTO;
import backend.prontodogbanho.dto.ServicoAdicionalCompletoDTO;
import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.model.Servico;
import backend.prontodogbanho.model.ServicoAdicional;
import backend.prontodogbanho.model.Usuario;
import backend.prontodogbanho.repository.AnimalServicoRepository;
import backend.prontodogbanho.repository.ServicoAdicionalRepository;
import backend.prontodogbanho.repository.ServicoRepository;
import backend.prontodogbanho.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicoAdicionalService {

    @Autowired
    private ServicoAdicionalRepository servicoAdicionalRepository;

    @Autowired
    private AnimalServicoRepository animalServicoRepository;

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Criar um novo serviço adicional
     */
    @Transactional
    public ServicoAdicionalCompletoDTO criarServicoAdicional(CriarServicoAdicionalDTO dto) {
        // Buscar entidades relacionadas
        AnimalServico animalServico = animalServicoRepository.findById(dto.animalServicoPrincipalId())
                .orElseThrow(() -> new RuntimeException("Animal Serviço não encontrado com ID: " + dto.animalServicoPrincipalId()));

        Servico servico = servicoRepository.findById(dto.servicoAdicionalId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + dto.servicoAdicionalId()));

        Usuario usuario = null;
        if (dto.usuarioId() != null) {
            usuario = usuarioRepository.findById(dto.usuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + dto.usuarioId()));
        }

        // Criar nova instância
        ServicoAdicional servicoAdicional = new ServicoAdicional();
        servicoAdicional.setAnimalServicoPrincipal(animalServico);
        servicoAdicional.setServicoAdicional(servico);
        servicoAdicional.setQuantidadeAdicional(dto.quantidade() != null ? dto.quantidade() : 1);
        servicoAdicional.setValorUnitario(dto.valorUnitario());

        // 🎯 HERDAR STATUS E DATA DE PAGAMENTO DO PAI AUTOMATICAMENTE
        System.out.println("💡 Herdando status e data de pagamento do pai:");
        System.out.println("  - Status do pai: " + animalServico.getStatusPagamento());
        System.out.println("  - Data do pai: " + animalServico.getDataPagamento());

        servicoAdicional.setStatusPagamento(animalServico.getStatusPagamento());
        servicoAdicional.setDataPagamento(animalServico.getDataPagamento());

        System.out.println("✅ Status e data herdados com sucesso!");

        // 🎯 HERDAR OU DEFINIR DATA DE REALIZAÇÃO
        // Se o DTO tiver dataRealizacao, usa ela; senão, herda do pai (dataServico)
        if (dto.dataRealizacao() != null) {
            servicoAdicional.setDataRealizacao(dto.dataRealizacao());
            System.out.println("📅 Data de realização customizada: " + dto.dataRealizacao());
        } else {
            servicoAdicional.setDataRealizacao(animalServico.getDataServico());
            System.out.println("📅 Data de realização herdada do pai: " + animalServico.getDataServico());
        }

        servicoAdicional.setObservacoes(dto.observacoes());
        servicoAdicional.setUsuario(usuario);
        servicoAdicional.setDataAdicao(LocalDateTime.now());

        // Calcular valor total (isso será feito automaticamente no @PrePersist)
        servicoAdicional.calcularValorTotal();

        // Salvar
        ServicoAdicional salvo = servicoAdicionalRepository.save(servicoAdicional);

        // Converter para DTO
        return new ServicoAdicionalCompletoDTO(salvo);
    }

    /**
     * Listar todos os serviços adicionais de um animal serviço
     */
    @Transactional(readOnly = true)
    public List<ServicoAdicionalCompletoDTO> listarPorAnimalServico(Long animalServicoId) {
        List<ServicoAdicional> servicosAdicionais = servicoAdicionalRepository
                .findByAnimalServicoPrincipalIdWithFetch(animalServicoId);

        return servicosAdicionais.stream()
                .map(ServicoAdicionalCompletoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Buscar serviço adicional por ID
     */
    @Transactional(readOnly = true)
    public ServicoAdicionalCompletoDTO buscarPorId(Long id) {
        ServicoAdicional servicoAdicional = servicoAdicionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço Adicional não encontrado com ID: " + id));

        return new ServicoAdicionalCompletoDTO(servicoAdicional);
    }

    /**
     * Atualizar status de pagamento
     */
    @Transactional
    public ServicoAdicionalCompletoDTO atualizarStatusPagamento(Long id, String novoStatus, LocalDateTime dataPagamento) {
        System.out.println("🔄 SERVICE: Atualizando status de pagamento");
        System.out.println("  - ID: " + id);
        System.out.println("  - Novo Status: " + novoStatus);
        System.out.println("  - Data Pagamento: " + dataPagamento);

        ServicoAdicional servicoAdicional = servicoAdicionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço Adicional não encontrado com ID: " + id));

        System.out.println("📋 Serviço encontrado:");
        System.out.println("  - Nome: " + (servicoAdicional.getServicoAdicional() != null ? servicoAdicional.getServicoAdicional().getNome() : "N/A"));
        System.out.println("  - Status atual: " + servicoAdicional.getStatusPagamento());
        System.out.println("  - Data pagamento atual: " + servicoAdicional.getDataPagamento());

        // Atualizar status
        servicoAdicional.setStatusPagamento(novoStatus);
        System.out.println("✏️ Status atualizado para: " + novoStatus);

        // Atualizar data se necessário
        if ("pago".equals(novoStatus) && dataPagamento != null) {
            servicoAdicional.setDataPagamento(dataPagamento.toLocalDate());
            System.out.println("📅 Data de pagamento atualizada para: " + dataPagamento.toLocalDate());
        } else if (!"pago".equals(novoStatus)) {
            servicoAdicional.setDataPagamento(null);
            System.out.println("🗑️ Data de pagamento removida (status não é 'pago')");
        }

        System.out.println("💾 Salvando alterações...");
        ServicoAdicional salvo = servicoAdicionalRepository.save(servicoAdicional);

        System.out.println("✅ Alterações salvas:");
        System.out.println("  - Status final: " + salvo.getStatusPagamento());
        System.out.println("  - Data final: " + salvo.getDataPagamento());

        ServicoAdicionalCompletoDTO resultado = new ServicoAdicionalCompletoDTO(salvo);
        System.out.println("🎯 Retornando DTO: " + resultado);

        return resultado;
    }

    /**
     * Atualizar serviço adicional completo
     */
    @Transactional
    public ServicoAdicionalCompletoDTO atualizarServicoAdicional(Long id, CriarServicoAdicionalDTO dto) {
        System.out.println("🔄 SERVICE: Atualizando serviço adicional completo");
        System.out.println("  - ID: " + id);
        System.out.println("  - DTO: " + dto);

        // Buscar o serviço adicional existente
        ServicoAdicional servicoAdicional = servicoAdicionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço Adicional não encontrado com ID: " + id));

        System.out.println("📋 Serviço adicional encontrado:");
        System.out.println("  - Nome atual: " + (servicoAdicional.getServicoAdicional() != null ? servicoAdicional.getServicoAdicional().getNome() : "N/A"));
        System.out.println("  - Quantidade atual: " + servicoAdicional.getQuantidadeAdicional());
        System.out.println("  - Valor atual: " + servicoAdicional.getValorUnitario());

        // Buscar entidades relacionadas
        Servico novoServicoAdicional = servicoRepository.findById(dto.servicoAdicionalId())
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com ID: " + dto.servicoAdicionalId()));

        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + dto.usuarioId()));

        System.out.println("✅ Entidades relacionadas encontradas:");
        System.out.println("  - Novo serviço: " + novoServicoAdicional.getNome());
        System.out.println("  - Usuário: " + usuario.getNome());

        // Atualizar dados do serviço adicional
        servicoAdicional.setServicoAdicional(novoServicoAdicional);
        servicoAdicional.setQuantidadeAdicional(dto.quantidade());
        servicoAdicional.setValorUnitario(dto.valorUnitario());
        servicoAdicional.setUsuario(usuario);
        servicoAdicional.setObservacoes(dto.observacoes());

        // 🎯 HERDAR STATUS E DATA DE PAGAMENTO DO PAI AUTOMATICAMENTE
        AnimalServico animalServicoPai = servicoAdicional.getAnimalServicoPrincipal();
        System.out.println("💡 Herdando status e data de pagamento do pai (atualização):");
        System.out.println("  - Status do pai: " + animalServicoPai.getStatusPagamento());
        System.out.println("  - Data do pai: " + animalServicoPai.getDataPagamento());

        servicoAdicional.setStatusPagamento(animalServicoPai.getStatusPagamento());
        servicoAdicional.setDataPagamento(animalServicoPai.getDataPagamento());

        System.out.println("✅ Status e data herdados do pai na atualização!");

        // 🎯 ATUALIZAR OU HERDAR DATA DE REALIZAÇÃO
        // Se o DTO tiver dataRealizacao, usa ela; senão, herda do pai (dataServico)
        if (dto.dataRealizacao() != null) {
            servicoAdicional.setDataRealizacao(dto.dataRealizacao());
            System.out.println("📅 Data de realização atualizada: " + dto.dataRealizacao());
        } else {
            servicoAdicional.setDataRealizacao(animalServicoPai.getDataServico());
            System.out.println("📅 Data de realização herdada do pai: " + animalServicoPai.getDataServico());
        }

        // Recalcular valor total
        servicoAdicional.calcularValorTotal();

        System.out.println("💾 Salvando alterações...");
        ServicoAdicional salvo = servicoAdicionalRepository.save(servicoAdicional);

        System.out.println("✅ Serviço adicional atualizado:");
        System.out.println("  - Nome final: " + salvo.getServicoAdicional().getNome());
        System.out.println("  - Quantidade final: " + salvo.getQuantidadeAdicional());
        System.out.println("  - Valor unitário final: " + salvo.getValorUnitario());
        System.out.println("  - Valor total final: " + salvo.getValorTotal());
        System.out.println("  - Status final: " + salvo.getStatusPagamento());
        System.out.println("  - Data pagamento final: " + salvo.getDataPagamento());

        // Converter para DTO
        ServicoAdicionalCompletoDTO resultado = new ServicoAdicionalCompletoDTO(salvo);
        System.out.println("🎯 Retornando DTO atualizado");

        return resultado;
    }

    /**
     * Remover serviço adicional
     */
    @Transactional
    public void removerServicoAdicional(Long id) {
        if (!servicoAdicionalRepository.existsById(id)) {
            throw new RuntimeException("Serviço Adicional não encontrado com ID: " + id);
        }

        servicoAdicionalRepository.deleteById(id);
    }

    /**
     * Listar todos os serviços adicionais
     */
    @Transactional(readOnly = true)
    public List<ServicoAdicionalCompletoDTO> listarTodos() {
        List<ServicoAdicional> servicosAdicionais = servicoAdicionalRepository.findAll();

        return servicosAdicionais.stream()
                .map(ServicoAdicionalCompletoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Calcular valor total dos serviços adicionais de um animal serviço
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularValorTotalAdicionais(Long animalServicoId) {
        List<ServicoAdicional> servicosAdicionais = servicoAdicionalRepository
                .findByAnimalServicoPrincipalId(animalServicoId);

        return servicosAdicionais.stream()
                .map(ServicoAdicional::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Contar serviços adicionais por status
     */
    @Transactional(readOnly = true)
    public Long contarPorStatus(String status) {
        return servicoAdicionalRepository.findByStatusPagamento(status).stream().count();
    }
}
