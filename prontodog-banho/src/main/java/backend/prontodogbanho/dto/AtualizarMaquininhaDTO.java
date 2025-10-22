package backend.prontodogbanho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarMaquininhaDTO {

    private String nome;
    private Long adquirenteId;
    private Long contaBancariaId;

    // Prazos
    private Integer prazoRecebimentoDebito;
    private Integer prazoRecebimentoCredito;

    // Antecipação
    private Boolean aceitaAntecipacao;
    private Boolean antecipacaoAutomatica;
    private BigDecimal taxaAntecipacaoMensal;

    // PIX
    private Boolean aceitaPix;
    private Long contaPixId;
    private BigDecimal taxaPix;
    private Integer prazoRecebimentoPix;

    // Status
    private Boolean ativo;
}

