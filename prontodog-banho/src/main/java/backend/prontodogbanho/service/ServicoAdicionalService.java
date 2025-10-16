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
        servicoAdicional.setStatusPagamento(dto.statusPagamento() != null ? dto.statusPagamento() : "em_aberto");

        // Converter string para LocalDate se fornecida
        if (dto.dataPagamento() != null && !dto.dataPagamento().trim().isEmpty()) {
            try {
                servicoAdicional.setDataPagamento(java.time.LocalDate.parse(dto.dataPagamento()));
            } catch (Exception e) {
                System.err.println("❌ Erro ao parsear data de pagamento: " + dto.dataPagamento());
                // Data inválida, deixa null
            }
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
        ServicoAdicional servicoAdicional = servicoAdicionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço Adicional não encontrado com ID: " + id));

        servicoAdicional.setStatusPagamento(novoStatus);
        if ("pago".equals(novoStatus) && dataPagamento != null) {
            servicoAdicional.setDataPagamento(dataPagamento.toLocalDate());
        }

        ServicoAdicional salvo = servicoAdicionalRepository.save(servicoAdicional);
        return new ServicoAdicionalCompletoDTO(salvo);
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
