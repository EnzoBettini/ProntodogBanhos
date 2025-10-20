package backend.prontodogbanho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarVendaBaixaDTO {

    private Long vendaId;
    private Long formaPagamentoId;
    private BigDecimal valorBaixa;
    private Integer numeroParcelas; // Opcional, padrão 1
    private String dataPrimeiraParcela; // Opcional, formato ISO (YYYY-MM-DD)
    private String observacoes;
    private Long usuarioId;

    // Método auxiliar para converter string em LocalDate
    public LocalDate getDataPrimeiraParcelaAsLocalDate() {
        if (dataPrimeiraParcela == null || dataPrimeiraParcela.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dataPrimeiraParcela, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            System.err.println("❌ Erro ao parsear dataPrimeiraParcela: " + dataPrimeiraParcela + " - " + e.getMessage());
            return null;
        }
    }
}

