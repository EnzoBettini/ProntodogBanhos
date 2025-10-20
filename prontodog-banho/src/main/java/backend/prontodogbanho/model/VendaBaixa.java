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
    private BigDecimal valorLiquido;

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

    // Método para calcular taxa e valor líquido automaticamente
    @PrePersist
    @PreUpdate
    public void calcularValores() {
        if (this.formaPagamento != null && this.valorBaixa != null) {
            this.valorTaxa = this.formaPagamento.calcularTaxa(this.valorBaixa);
            this.valorLiquido = this.valorBaixa.subtract(this.valorTaxa);
        }

        // Se não definiu data da primeira parcela, usar data da baixa
        if (this.dataPrimeiraParcela == null && this.dataBaixa != null) {
            this.dataPrimeiraParcela = this.dataBaixa.toLocalDate();
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
}

