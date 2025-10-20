package backend.prontodogbanho.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CriarServicoAdicionalDTO(
        // Este campo será preenchido automaticamente pelo AnimalServicoService
        Long animalServicoPrincipalId,

        @NotNull(message = "O ID do serviço adicional é obrigatório.")
        Long servicoAdicionalId,

        @Min(value = 1, message = "A quantidade deve ser no mínimo 1.")
        Integer quantidade,

        @NotNull(message = "O valor do serviço adicional é obrigatório.")
        BigDecimal valorUnitario, // Mudado para coincidir com o frontend

        String observacoes,
        Long usuarioId, // Opcional, pode ser preenchido pelo sistema

        // 🎯 Data de realização do serviço adicional
        // Por padrão, herda do serviço pai (dataServico)
        // Mas pode ser editada manualmente pelo usuário
        LocalDate dataRealizacao

        // ❌ Removidos: statusPagamento e dataPagamento
        // 🎯 Sempre herdam automaticamente do serviço pai
) {}
