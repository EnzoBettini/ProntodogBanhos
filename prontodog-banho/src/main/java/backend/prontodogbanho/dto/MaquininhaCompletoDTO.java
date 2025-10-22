package backend.prontodogbanho.dto;

import backend.prontodogbanho.model.Maquininha;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaquininhaCompletoDTO {

    private Long id;
    private String nome;
    private Boolean ativo;

    // Adquirente
    private Long adquirenteId;
    private String adquirenteNome;

    // Conta principal
    private Long contaBancariaId;
    private String contaBancariaNome;

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
    private String contaPixNome;
    private BigDecimal taxaPix;
    private Integer prazoRecebimentoPix;

    // Taxas configuradas
    private List<TaxaDTO> taxas;

    // Estatísticas
    private Integer totalTaxasConfiguradas;
    private Long totalBandeirasConfiguradas;

    // Datas
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxaDTO {
        private Long id;
        private Long bandeiraId;
        private String bandeiraNome;
        private String tipoTransacao;
        private String tipoTransacaoDescricao;
        private Integer numeroParcelas;
        private BigDecimal taxaPercentual;
        private BigDecimal taxaFixa;
    }

    // Construtor a partir da entidade
    public static MaquininhaCompletoDTO fromEntity(Maquininha maquininha) {
        MaquininhaCompletoDTO dto = new MaquininhaCompletoDTO();

        dto.setId(maquininha.getId());
        dto.setNome(maquininha.getNome());
        dto.setAtivo(maquininha.getAtivo());

        // Adquirente
        dto.setAdquirenteId(maquininha.getAdquirenteId());
        dto.setAdquirenteNome(maquininha.getAdquirenteNome());

        // Conta bancária
        dto.setContaBancariaId(maquininha.getContaBancariaId());
        dto.setContaBancariaNome(maquininha.getContaBancariaNome());

        // Prazos
        dto.setPrazoRecebimentoDebito(maquininha.getPrazoRecebimentoDebito());
        dto.setPrazoRecebimentoCredito(maquininha.getPrazoRecebimentoCredito());

        // Antecipação
        dto.setAceitaAntecipacao(maquininha.getAceitaAntecipacao());
        dto.setAntecipacaoAutomatica(maquininha.getAntecipacaoAutomatica());
        dto.setTaxaAntecipacaoMensal(maquininha.getTaxaAntecipacaoMensal());

        // PIX
        dto.setAceitaPix(maquininha.getAceitaPix());
        dto.setContaPixId(maquininha.getContaPixId());
        dto.setContaPixNome(maquininha.getContaPixNome());
        dto.setTaxaPix(maquininha.getTaxaPix());
        dto.setPrazoRecebimentoPix(maquininha.getPrazoRecebimentoPix());

        // Taxas
        if (maquininha.getTaxas() != null) {
            dto.setTaxas(
                maquininha.getTaxas().stream()
                    .map(taxa -> {
                        TaxaDTO taxaDto = new TaxaDTO();
                        taxaDto.setId(taxa.getId());
                        taxaDto.setBandeiraId(taxa.getBandeiraId());
                        taxaDto.setBandeiraNome(taxa.getBandeiraNome());
                        taxaDto.setTipoTransacao(taxa.getTipoTransacao());
                        taxaDto.setTipoTransacaoDescricao(taxa.getDescricaoTipoTransacao());
                        taxaDto.setNumeroParcelas(taxa.getNumeroParcelas());
                        taxaDto.setTaxaPercentual(taxa.getTaxaPercentual());
                        taxaDto.setTaxaFixa(taxa.getTaxaFixa());
                        return taxaDto;
                    })
                    .collect(Collectors.toList())
            );
        }

        // Estatísticas
        dto.setTotalTaxasConfiguradas(maquininha.getTotalTaxasConfiguradas());
        dto.setTotalBandeirasConfiguradas(maquininha.getTotalBandeirasConfiguradas());

        // Datas
        dto.setCreatedAt(maquininha.getCreatedAt());
        dto.setUpdatedAt(maquininha.getUpdatedAt());

        return dto;
    }
}

