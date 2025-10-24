package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="venda_baixas", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaBaixa {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="venda_id", nullable=false)
    @JsonBackReference("venda-baixa")
    private Venda venda;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="forma_pagamento_id", nullable=false)
    @JsonBackReference("forma-pagamento-baixa")
    private FormaPagamento formaPagamento;

    @Column(name="valor_baixa", precision=10, scale=2, nullable=false)
    private BigDecimal valorBaixa;

    @Column(name="valor_taxa", precision=10, scale=2)
    private BigDecimal valorTaxa = BigDecimal.ZERO;

    @Column(name="valor_liquido", precision=10, scale=2, nullable=false)
    private BigDecimal valorLiquido = BigDecimal.ZERO;

    @Column(name="data_baixa")
    private LocalDateTime dataBaixa = LocalDateTime.now();

    @Column(name="numero_parcelas")
    private Integer numeroParcelas = 1;

    @Column(name="data_primeira_parcela")
    private LocalDate dataPrimeiraParcela;

    @Column(name="observacoes", columnDefinition="TEXT")
    private String observacoes;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable=false)
    @JsonBackReference("usuario-baixa")
    private Usuario usuario;

    // Novos campos para suporte a maquininhas
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="maquininha_id")
    @JsonBackReference("maquininha-baixa")
    private Maquininha maquininha;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="bandeira_id")
    @JsonBackReference("bandeira-baixa")
    private Bandeira bandeira;

    @Column(name="tipo_transacao", length=50)
    private String tipoTransacao; // debito, credito_avista, credito_parcelado, pix

    @Column(name="data_prevista_recebimento")
    private LocalDate dataPrevistaRecebimento;

    @Column(name="data_efetiva_recebimento")
    private LocalDate dataEfetivaRecebimento;

    @Column(name="status_recebimento", length=50)
    private String statusRecebimento = "pendente"; // pendente, recebido, antecipado, estornado

    // Métodos helper para expor IDs
    public Long getVendaId() {
        return venda != null ? venda.getId() : null;
    }

    public Long getFormaPagamentoId() {
        return formaPagamento != null ? formaPagamento.getId() : null;
    }

    public String getFormaPagamentoNome() {
        return formaPagamento != null ? formaPagamento.getNome() : null;
    }

    public String getFormaPagamentoTipo() {
        return formaPagamento != null ? formaPagamento.getTipo() : null;
    }

    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    public String getUsuarioNome() {
        return usuario != null ? usuario.getNome() : null;
    }

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

    // Método para calcular taxa e valor líquido automaticamente
    @PrePersist
    @PreUpdate
    public void calcularValores() {
        // Garantir que valorBaixa nunca seja null
        if (this.valorBaixa == null) {
            this.valorBaixa = BigDecimal.ZERO;
        }

        // Calcular taxa
        if (this.formaPagamento != null) {
            BigDecimal taxaCalculada = this.formaPagamento.calcularTaxa(this.valorBaixa);
            this.valorTaxa = taxaCalculada != null ? taxaCalculada : BigDecimal.ZERO;
        } else {
            this.valorTaxa = BigDecimal.ZERO;
        }

        // Garantir que valorTaxa nunca seja null antes de calcular valorLiquido
        if (this.valorTaxa == null) {
            this.valorTaxa = BigDecimal.ZERO;
        }

        // Calcular valor líquido (sempre seguro agora)
        this.valorLiquido = this.valorBaixa.subtract(this.valorTaxa);

        // Se não definiu data da primeira parcela, usar data da baixa
        if (this.dataPrimeiraParcela == null && this.dataBaixa != null) {
            this.dataPrimeiraParcela = this.dataBaixa.toLocalDate();
        }

        // Inicializar statusRecebimento se null
        if (this.statusRecebimento == null) {
            this.statusRecebimento = "pendente";
        }
    }

    // Verificar se é pagamento parcelado
    public boolean isParcelado() {
        return numeroParcelas != null && numeroParcelas > 1;
    }

    // Verificar se é à vista
    public boolean isAVista() {
        return numeroParcelas == null || numeroParcelas == 1;
    }

    // Calcular valor de cada parcela
    public BigDecimal getValorParcela() {
        if (numeroParcelas == null || numeroParcelas <= 0) {
            return valorBaixa;
        }
        return valorBaixa.divide(new BigDecimal(numeroParcelas), 2, BigDecimal.ROUND_HALF_UP);
    }

    // Calcular percentual da taxa
    public BigDecimal getPercentualTaxa() {
        if (valorBaixa.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return valorTaxa.multiply(new BigDecimal("100"))
                .divide(valorBaixa, 2, BigDecimal.ROUND_HALF_UP);
    }

    // Verificar se é forma de pagamento em dinheiro
    public boolean isDinheiro() {
        return formaPagamento != null && "dinheiro".equals(formaPagamento.getTipo());
    }

    // Verificar se é PIX
    public boolean isPix() {
        return formaPagamento != null && "pix".equals(formaPagamento.getTipo());
    }

    // Verificar se é cartão (débito ou crédito)
    public boolean isCartao() {
        if (formaPagamento == null) return false;
        String tipo = formaPagamento.getTipo();
        return "debito".equals(tipo) || "credito".equals(tipo);
    }

    // Novos métodos para maquininhas
    public boolean temMaquininha() {
        return maquininha != null;
    }

    public boolean isDebito() {
        return "debito".equalsIgnoreCase(tipoTransacao);
    }

    public boolean isCreditoAVista() {
        return "credito_avista".equalsIgnoreCase(tipoTransacao);
    }

    public boolean isCreditoParcelado() {
        return "credito_parcelado".equalsIgnoreCase(tipoTransacao);
    }

    public boolean isPixMaquininha() {
        return "pix".equalsIgnoreCase(tipoTransacao);
    }

    public boolean isRecebimentoPendente() {
        return "pendente".equalsIgnoreCase(statusRecebimento);
    }

    public boolean isRecebido() {
        return "recebido".equalsIgnoreCase(statusRecebimento);
    }

    public boolean isAntecipado() {
        return "antecipado".equalsIgnoreCase(statusRecebimento);
    }

    public boolean isEstornado() {
        return "estornado".equalsIgnoreCase(statusRecebimento);
    }

    public boolean jaRecebeu() {
        return dataEfetivaRecebimento != null;
    }

    // Calcular dias até recebimento
    public Integer getDiasAteRecebimento() {
        if (jaRecebeu() || dataPrevistaRecebimento == null) {
            return 0;
        }

        LocalDate hoje = LocalDate.now();
        if (dataPrevistaRecebimento.isBefore(hoje)) {
            return 0; // Já passou da data
        }

        return (int) java.time.temporal.ChronoUnit.DAYS.between(hoje, dataPrevistaRecebimento);
    }

    // Verificar se está atrasado
    public boolean isAtrasado() {
        if (jaRecebeu()) return false;
        if (dataPrevistaRecebimento == null) return false;
        return dataPrevistaRecebimento.isBefore(LocalDate.now());
    }

    // Descrição do tipo de transação
    public String getDescricaoTipoTransacao() {
        if (tipoTransacao == null) return "-";

        switch (tipoTransacao.toLowerCase()) {
            case "debito":
                return "Débito";
            case "credito_avista":
                return "Crédito à Vista";
            case "credito_parcelado":
                return "Crédito " + numeroParcelas + "x";
            case "pix":
                return "PIX";
            default:
                return tipoTransacao;
        }
    }
}

