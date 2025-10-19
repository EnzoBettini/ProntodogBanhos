package backend.prontodogbanho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarVendaDTO {

    private Long clienteId;
    private Long usuarioId;
    private String tipoVenda; // presencial, agendamento, delivery
    private BigDecimal desconto; // Desconto opcional na venda
    private String observacoes;

    // Lista de IDs de animal_servico que farão parte desta venda
    private List<Long> animalServicoIds;

    // OU Lista de itens com valores personalizados (mais flexível)
    private List<ItemVendaDTO> itens;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemVendaDTO {
        // Para serviços que já existem como AnimalServico
        private Long animalServicoId;

        // Para criar novos serviços na hora
        private Long animalId; // ID do animal
        private Long servicoId; // ID do serviço do catálogo

        // Valores personalizados
        private BigDecimal valorItem; // Permite sobrescrever o valor do serviço
        private BigDecimal descontoItem; // Desconto específico deste item
        private String observacoes;
    }
}

