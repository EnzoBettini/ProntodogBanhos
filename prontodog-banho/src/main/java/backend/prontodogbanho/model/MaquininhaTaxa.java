package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(
    name="maquininha_taxas",
    schema="banhoetosa",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"maquininha_id", "bandeira_id", "tipo_transacao", "numero_parcelas"}
    )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaquininhaTaxa {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="maquininha_id", nullable=false)
    @JsonBackReference("maquininha-taxa")
    private Maquininha maquininha;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="bandeira_id", nullable=false)
    @JsonBackReference("bandeira-taxa")
    private Bandeira bandeira;

    @Column(name="tipo_transacao", nullable=false, length=50)
    private String tipoTransacao; // debito, credito_avista, credito_parcelado

    @Column(name="numero_parcelas")
    private Integer numeroParcelas;

    @Column(name="taxa_percentual", precision=5, scale=2, nullable=false)
    private BigDecimal taxaPercentual = BigDecimal.ZERO;

    @Column(name="taxa_fixa", precision=10, scale=2)
    private BigDecimal taxaFixa = BigDecimal.ZERO;

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Métodos helper para IDs
    public Long getMaquininhaId() {
        return maquininha != null ? maquininha.getId() : null;
    }

    public String getMaquininhaNome() {
        return maquininha != null ? maquininha.getNome() : null;
    }

    public Long getBandeiraId() {
        return bandeira != null ? bandeira.getId() : null;
    }

    public String getBandeiraNome() {
        return bandeira != null ? bandeira.getNome() : null;
    }

    // Validação e atualização antes de persistir
    @PrePersist
    @PreUpdate
    public void validar() {
        // Atualizar timestamp
        this.updatedAt = LocalDateTime.now();
        // Débito não pode ter número de parcelas
        if ("debito".equals(tipoTransacao) && numeroParcelas != null) {
            numeroParcelas = null;
        }

        // Crédito à vista pode ter 1 ou null
        if ("credito_avista".equals(tipoTransacao)) {
            if (numeroParcelas != null && numeroParcelas != 1) {
                numeroParcelas = 1;
            }
        }

        // Crédito parcelado deve ter número de parcelas >= 2
        if ("credito_parcelado".equals(tipoTransacao)) {
            if (numeroParcelas == null || numeroParcelas < 2) {
                throw new IllegalStateException("Crédito parcelado deve ter número de parcelas >= 2");
            }
        }

        // Taxas não podem ser negativas
        if (taxaPercentual != null && taxaPercentual.compareTo(BigDecimal.ZERO) < 0) {
            taxaPercentual = BigDecimal.ZERO;
        }

        if (taxaFixa != null && taxaFixa.compareTo(BigDecimal.ZERO) < 0) {
            taxaFixa = BigDecimal.ZERO;
        }
    }

    // Métodos de verificação
    public boolean isDebito() {
        return "debito".equals(tipoTransacao);
    }

    public boolean isCreditoAVista() {
        return "credito_avista".equals(tipoTransacao);
    }

    public boolean isCreditoParcelado() {
        return "credito_parcelado".equals(tipoTransacao);
    }

    public boolean isCredito() {
        return isCreditoAVista() || isCreditoParcelado();
    }

    // Calcular taxa de um valor
    public BigDecimal calcularTaxa(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxaPerc = BigDecimal.ZERO;
        if (taxaPercentual != null && taxaPercentual.compareTo(BigDecimal.ZERO) > 0) {
            taxaPerc = valor.multiply(taxaPercentual)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }

        BigDecimal taxaFix = taxaFixa != null ? taxaFixa : BigDecimal.ZERO;

        return taxaPerc.add(taxaFix);
    }

    // Calcular valor líquido
    public BigDecimal calcularValorLiquido(BigDecimal valorBruto) {
        if (valorBruto == null || valorBruto.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxa = calcularTaxa(valorBruto);
        return valorBruto.subtract(taxa);
    }

    // Descrição formatada
    public String getDescricaoTipoTransacao() {
        if ("debito".equals(tipoTransacao)) {
            return "Débito";
        } else if ("credito_avista".equals(tipoTransacao)) {
            return "Crédito à Vista";
        } else if ("credito_parcelado".equals(tipoTransacao)) {
            return "Crédito " + numeroParcelas + "x";
        }
        return tipoTransacao;
    }

    // Descrição completa
    public String getDescricaoCompleta() {
        StringBuilder sb = new StringBuilder();
        if (bandeira != null) {
            sb.append(bandeira.getNome()).append(" - ");
        }
        sb.append(getDescricaoTipoTransacao());
        sb.append(": ").append(taxaPercentual).append("%");
        if (taxaFixa != null && taxaFixa.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(" + R$ ").append(taxaFixa);
        }
        return sb.toString();
    }

    // Taxa efetiva (considerando taxa percentual + fixa)
    public BigDecimal getTaxaEfetiva(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return taxaPercentual;
        }

        BigDecimal taxaTotal = calcularTaxa(valor);
        return taxaTotal.multiply(new BigDecimal("100"))
                .divide(valor, 2, RoundingMode.HALF_UP);
    }
}

