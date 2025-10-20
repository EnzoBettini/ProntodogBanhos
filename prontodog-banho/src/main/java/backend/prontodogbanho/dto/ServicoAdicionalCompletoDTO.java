package backend.prontodogbanho.dto;

import backend.prontodogbanho.model.ServicoAdicional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ServicoAdicionalCompletoDTO(
        Long id,
        Long animalServicoPrincipalId,
        Long servicoAdicionalId,
        String nomeServicoAdicional,
        Integer quantidade,
        BigDecimal valorUnitario,
        BigDecimal valorTotal,
        String statusPagamento,
        LocalDate dataPagamento,
        LocalDate dataRealizacao, // 🎯 Nova: data de realização do serviço adicional
        String observacoes,
        Long usuarioId,
        String nomeUsuario,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public ServicoAdicionalCompletoDTO(ServicoAdicional sa) {
        this(
                sa.getId(),
                sa.getAnimalServicoPrincipal() != null ? sa.getAnimalServicoPrincipal().getId() : null,
                sa.getServicoAdicional() != null ? sa.getServicoAdicional().getId() : null,
                sa.getServicoAdicional() != null ? sa.getServicoAdicional().getNome() : "Serviço Removido",
                sa.getQuantidadeAdicional(), // Corrected to use quantidadeAdicional
                sa.getValorUnitario(),
                sa.getValorTotal(),
                sa.getStatusPagamento(),
                sa.getDataPagamento(),
                sa.getDataRealizacao(), // 🎯 Nova: data de realização
                sa.getObservacoes(),
                sa.getUsuario() != null ? sa.getUsuario().getId() : null,
                sa.getUsuario() != null ? sa.getUsuario().getNome() : "Usuário Removido",
                sa.getDataAdicao(), // Using dataAdicao for createdAt
                sa.getDataAdicao() // Using dataAdicao for updatedAt (assuming no separate updatedAt for now)
        );
    }
}
