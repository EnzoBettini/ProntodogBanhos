package backend.prontodogbanho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarMaquininhaDTO {

    private String nome;
    private Long adquirenteId;
    private Long contaBancariaId;

    // Prazos
    private Integer prazoRecebimentoDebito = 1;
    private Integer prazoRecebimentoCredito = 30;

    // Antecipação
    private Boolean aceitaAntecipacao = false;
    private Boolean antecipacaoAutomatica = false;
    private BigDecimal taxaAntecipacaoMensal = BigDecimal.ZERO;

    // PIX
    private Boolean aceitaPix = false;
    private Long contaPixId; // Opcional, se null usa a conta principal
    private BigDecimal taxaPix = BigDecimal.ZERO;
    private Integer prazoRecebimentoPix = 0;

    // Taxas (opcional no momento da criação, pode adicionar depois)
    private List<TaxaMaquininhaDTO> taxas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxaMaquininhaDTO {
        private Long bandeiraId;
        private String tipoTransacao; // debito, credito_avista, credito_parcelado
        private Integer numeroParcelas; // NULL para débito, 1 ou NULL para à vista, 2+ para parcelado
        private BigDecimal taxaPercentual;
        private BigDecimal taxaFixa = BigDecimal.ZERO;
    }
}

