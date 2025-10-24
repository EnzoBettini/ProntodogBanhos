package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="maquininhas", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maquininha {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="nome", nullable=false, length=200)
    private String nome;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="adquirente_id", nullable=false)
    @JsonBackReference("adquirente-maquininha")
    private Adquirente adquirente;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="conta_bancaria_id", nullable=false)
    @JsonBackReference("conta-bancaria-maquininha")
    private ContaBancaria contaBancaria;

    @Column(name="prazo_recebimento_debito")
    private Integer prazoRecebimentoDebito = 1;

    @Column(name="prazo_recebimento_credito")
    private Integer prazoRecebimentoCredito = 30;

    @Column(name="aceita_antecipacao")
    private Boolean aceitaAntecipacao = false;

    @Column(name="antecipacao_automatica")
    private Boolean antecipacaoAutomatica = false;

    @Column(name="taxa_antecipacao_mensal", precision=5, scale=2)
    private BigDecimal taxaAntecipacaoMensal = BigDecimal.ZERO;

    @Column(name="aceita_pix")
    private Boolean aceitaPix = false;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="conta_pix_id")
    @JsonBackReference("conta-pix-maquininha")
    private ContaBancaria contaPix;

    @Column(name="taxa_pix", precision=5, scale=2)
    private BigDecimal taxaPix = BigDecimal.ZERO;

    @Column(name="prazo_recebimento_pix")
    private Integer prazoRecebimentoPix = 0;

    @Column(name="ativo")
    private Boolean ativo = true;

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy="maquininha", cascade=CascadeType.ALL, orphanRemoval=true)
    @JsonManagedReference("maquininha-taxa")
    private List<MaquininhaTaxa> taxas;

    @OneToMany(mappedBy="maquininha", cascade=CascadeType.ALL)
    @JsonManagedReference("maquininha-baixa")
    private List<VendaBaixa> baixas;

    // Atualizar timestamp antes de salvar
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Métodos helper para IDs
    public Long getAdquirenteId() {
        return adquirente != null ? adquirente.getId() : null;
    }

    public String getAdquirenteNome() {
        return adquirente != null ? adquirente.getNome() : null;
    }

    public Long getContaBancariaId() {
        return contaBancaria != null ? contaBancaria.getId() : null;
    }

    public String getContaBancariaNome() {
        return contaBancaria != null ? contaBancaria.getNome() : null;
    }

    public Long getContaPixId() {
        return contaPix != null ? contaPix.getId() : null;
    }

    public String getContaPixNome() {
        return contaPix != null ? contaPix.getNome() : null;
    }

    // Métodos de verificação
    public boolean isAtiva() {
        return ativo != null && ativo;
    }

    public boolean aceitaAntecipacao() {
        return aceitaAntecipacao != null && aceitaAntecipacao;
    }

    public boolean temAntecipacaoAutomatica() {
        return antecipacaoAutomatica != null && antecipacaoAutomatica;
    }

    public boolean aceitaPix() {
        return aceitaPix != null && aceitaPix;
    }

    // Calcular data de recebimento
    public LocalDate calcularDataRecebimento(String tipoTransacao, LocalDate dataTransacao) {
        if (dataTransacao == null) {
            dataTransacao = LocalDate.now();
        }

        int diasParaAdicionar;

        if ("pix".equalsIgnoreCase(tipoTransacao)) {
            diasParaAdicionar = prazoRecebimentoPix != null ? prazoRecebimentoPix : 0;
        } else if ("debito".equalsIgnoreCase(tipoTransacao)) {
            diasParaAdicionar = prazoRecebimentoDebito != null ? prazoRecebimentoDebito : 1;
        } else {
            // crédito ou crédito parcelado
            diasParaAdicionar = prazoRecebimentoCredito != null ? prazoRecebimentoCredito : 30;

            // Se tem antecipação automática, reduz para 1 dia
            if (temAntecipacaoAutomatica()) {
                diasParaAdicionar = 1;
            }
        }

        return dataTransacao.plusDays(diasParaAdicionar);
    }

    // Calcular taxa do PIX
    public BigDecimal calcularTaxaPix(BigDecimal valor) {
        if (!aceitaPix() || valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (taxaPix == null || taxaPix.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return valor.multiply(taxaPix).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
    }

    // Calcular valor líquido do PIX
    public BigDecimal calcularValorLiquidoPix(BigDecimal valorBruto) {
        if (valorBruto == null || valorBruto.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxa = calcularTaxaPix(valorBruto);
        return valorBruto.subtract(taxa);
    }

    // Buscar taxa específica por bandeira e tipo
    public MaquininhaTaxa buscarTaxa(Long bandeiraId, String tipoTransacao, Integer numeroParcelas) {
        if (taxas == null || taxas.isEmpty()) {
            return null;
        }

        return taxas.stream()
                .filter(t -> t.getBandeiraId().equals(bandeiraId))
                .filter(t -> t.getTipoTransacao().equals(tipoTransacao))
                .filter(t -> {
                    if ("debito".equals(tipoTransacao)) {
                        return t.getNumeroParcelas() == null;
                    } else if ("credito_avista".equals(tipoTransacao)) {
                        return t.getNumeroParcelas() == null || t.getNumeroParcelas() == 1;
                    } else {
                        return numeroParcelas.equals(t.getNumeroParcelas());
                    }
                })
                .findFirst()
                .orElse(null);
    }

    // Total de taxas configuradas
    public int getTotalTaxasConfiguradas() {
        return taxas != null ? taxas.size() : 0;
    }

    // Total de bandeiras configuradas
    public long getTotalBandeirasConfiguradas() {
        if (taxas == null) return 0;
        return taxas.stream()
                .map(MaquininhaTaxa::getBandeiraId)
                .distinct()
                .count();
    }

    // Descrição completa
    public String getDescricaoCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append(nome);
        if (adquirente != null) {
            sb.append(" (").append(adquirente.getNome()).append(")");
        }
        return sb.toString();
    }
}

