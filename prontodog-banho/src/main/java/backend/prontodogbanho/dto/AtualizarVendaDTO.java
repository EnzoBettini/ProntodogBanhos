package backend.prontodogbanho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarVendaDTO {

    private String tipoVenda;
    private BigDecimal desconto;
    private String observacoes;
    private String statusVenda; // Permite alterar status manualmente se necessário
}

