package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="formas_pagamento", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormaPagamento {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="nome", nullable=false, unique=true, length=100)
    private String nome;

    @Column(name="tipo", nullable=false, length=50)
    private String tipo; // dinheiro, debito, credito, pix, boleto, outro

    @Column(name="taxa_percentual", precision=5, scale=2)
    private BigDecimal taxaPercentual = BigDecimal.ZERO;

    @Column(name="taxa_fixa", precision=10, scale=2)
    private BigDecimal taxaFixa = BigDecimal.ZERO;

    @Column(name="parcelas_max")
    private Integer parcelasMax = 1;

    @Column(name="dias_recebimento")
    private Integer diasRecebimento = 0;

    @Column(name="ativo")
    private Boolean ativo = true;

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relacionamento opcional com maquininha
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="maquininha_id")
    @JsonIgnore
    private Maquininha maquininha;

    @OneToMany(mappedBy="formaPagamento", cascade=CascadeType.ALL)
    @JsonManagedReference("forma-pagamento-baixa")
    private List<VendaBaixa> baixas;

    // Método helper para calcular taxa de um valor
    public BigDecimal calcularTaxa(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // Garantir que taxas nunca sejam null
        BigDecimal taxaPerc = taxaPercentual != null ? taxaPercentual : BigDecimal.ZERO;
        BigDecimal taxaFix = taxaFixa != null ? taxaFixa : BigDecimal.ZERO;

        BigDecimal taxaCalculadaPerc = valor.multiply(taxaPerc).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
        return taxaCalculadaPerc.add(taxaFix);
    }

    // Método helper para calcular valor líquido
    public BigDecimal calcularValorLiquido(BigDecimal valorBruto) {
        if (valorBruto == null || valorBruto.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal taxa = calcularTaxa(valorBruto);
        // calcularTaxa() sempre retorna um valor válido (nunca null)
        return valorBruto.subtract(taxa);
    }

    // Verificar se forma de pagamento é à vista
    public boolean isAVista() {
        return parcelasMax == 1;
    }

    // Verificar se permite parcelamento
    public boolean permiteParcelamento() {
        return parcelasMax > 1;
    }

    // Helper para obter ID da maquininha (evita problemas de serialização)
    public Long getMaquininhaId() {
        return maquininha != null ? maquininha.getId() : null;
    }

    // Verificar se é uma forma de pagamento vinculada a maquininha
    public boolean isMaquininha() {
        return maquininha != null;
    }
}

