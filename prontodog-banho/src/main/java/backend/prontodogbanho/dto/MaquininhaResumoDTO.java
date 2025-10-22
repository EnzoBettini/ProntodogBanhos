package backend.prontodogbanho.dto;

import backend.prontodogbanho.model.Maquininha;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaquininhaResumoDTO {

    private Long id;
    private String nome;
    private String adquirenteNome;
    private String contaBancariaNome;
    private Boolean ativo;
    private Boolean aceitaPix;
    private Integer totalTaxasConfiguradas;
    private Long totalBandeirasConfiguradas;

    // Construtor a partir da entidade
    public static MaquininhaResumoDTO fromEntity(Maquininha maquininha) {
        MaquininhaResumoDTO dto = new MaquininhaResumoDTO();

        dto.setId(maquininha.getId());
        dto.setNome(maquininha.getNome());
        dto.setAdquirenteNome(maquininha.getAdquirenteNome());
        dto.setContaBancariaNome(maquininha.getContaBancariaNome());
        dto.setAtivo(maquininha.getAtivo());
        dto.setAceitaPix(maquininha.getAceitaPix());
        dto.setTotalTaxasConfiguradas(maquininha.getTotalTaxasConfiguradas());
        dto.setTotalBandeirasConfiguradas(maquininha.getTotalBandeirasConfiguradas());

        return dto;
    }
}

