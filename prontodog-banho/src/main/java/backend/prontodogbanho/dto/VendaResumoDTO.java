package backend.prontodogbanho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaResumoDTO {

    private Long id;
    private Long codigoVenda;
    private LocalDateTime dataVenda;
    private String tipoVenda;
    private String statusVenda;

    // Cliente
    private Long clienteId;
    private String clienteNome;

    // Usuário
    private Long usuarioId;
    private String usuarioNome;

    // Valores principais
    private BigDecimal valorTotal;
    private BigDecimal valorPago;
    private BigDecimal valorPendente;

    // Quantidades
    private Integer quantidadeItens;
    private Integer quantidadeBaixas;

    // Percentual pago
    private BigDecimal percentualPago;
}

