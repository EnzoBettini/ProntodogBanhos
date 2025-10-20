package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="vendas", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="codigo_venda", nullable=false, unique=true)
    private Long codigoVenda;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cliente_id", nullable=false)
    @JsonBackReference("cliente-venda")
    private Cliente cliente;

    @Column(name="tipo_venda", length=50)
    private String tipoVenda = "presencial"; // presencial, agendamento, busca_entrega

    @Column(name="status_venda", nullable=false, length=20)
    private String statusVenda = "em_aberto"; // em_aberto, pago, parcial, cancelado

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable=false)
    @JsonBackReference("usuario-venda")
    private Usuario usuario;

    @Column(name="data_venda")
    private LocalDateTime dataVenda = LocalDateTime.now();

    // Valores financeiros
    @Column(name="valor_bruto", precision=10, scale=2, nullable=false)
    private BigDecimal valorBruto = BigDecimal.ZERO;

    @Column(name="desconto", precision=10, scale=2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(name="valor_total", precision=10, scale=2, nullable=false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name="valor_pago", precision=10, scale=2, nullable=false)
    private BigDecimal valorPago = BigDecimal.ZERO;

    @Column(name="valor_pendente", precision=10, scale=2, nullable=false)
    private BigDecimal valorPendente = BigDecimal.ZERO;

    @Column(name="observacoes", columnDefinition="TEXT")
    private String observacoes;

    @Column(name="cancelado_em")
    private LocalDateTime canceladoEm;

    @Column(name="motivo_cancelamento", columnDefinition="TEXT")
    private String motivoCancelamento;

    // Relacionamentos
    @OneToMany(mappedBy="venda", cascade=CascadeType.ALL)
    @JsonManagedReference("venda-item")
    private List<VendaItem> itens;

    @OneToMany(mappedBy="venda", cascade=CascadeType.ALL)
    @JsonManagedReference("venda-baixa")
    private List<VendaBaixa> baixas;

    @OneToMany(mappedBy="venda", cascade=CascadeType.ALL)
    @JsonManagedReference("venda-animal-servico")
    private List<AnimalServico> animalServicos;

    // Métodos helper para expor IDs
    public Long getClienteId() {
        return cliente != null ? cliente.getId() : null;
    }

    public String getClienteNome() {
        return cliente != null ? cliente.getNomeCompleto() : null;
    }

    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    public String getUsuarioNome() {
        return usuario != null ? usuario.getNome() : null;
    }

    // Métodos helper para cálculos financeiros
    public void recalcularValores() {
        // Calcular valor total (bruto - desconto)
        this.valorTotal = this.valorBruto.subtract(this.desconto);

        // Calcular valor pendente
        this.valorPendente = this.valorTotal.subtract(this.valorPago);

        // Atualizar status baseado nos valores
        atualizarStatus();
    }

    public void atualizarStatus() {
        if (this.valorPago.compareTo(BigDecimal.ZERO) == 0) {
            this.statusVenda = "em_aberto";
        } else if (this.valorPago.compareTo(this.valorTotal) >= 0) {
            this.statusVenda = "pago";
        } else {
            this.statusVenda = "parcial";
        }
    }

    public void adicionarBaixa(VendaBaixa baixa) {
        this.valorPago = this.valorPago.add(baixa.getValorBaixa());
        recalcularValores();
    }

    public void removerBaixa(VendaBaixa baixa) {
        this.valorPago = this.valorPago.subtract(baixa.getValorBaixa());
        recalcularValores();
    }

    public boolean isPago() {
        return "pago".equals(this.statusVenda);
    }

    public boolean isEmAberto() {
        return "em_aberto".equals(this.statusVenda);
    }

    public boolean isParcial() {
        return "parcial".equals(this.statusVenda);
    }

    public boolean isCancelado() {
        return "cancelado".equals(this.statusVenda);
    }

    public int getQuantidadeItens() {
        return itens != null ? itens.size() : 0;
    }

    public int getQuantidadeBaixas() {
        return baixas != null ? baixas.size() : 0;
    }

    // Calcular percentual pago
    public BigDecimal getPercentualPago() {
        if (valorTotal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return valorPago.multiply(new BigDecimal("100"))
                .divide(valorTotal, 2, BigDecimal.ROUND_HALF_UP);
    }
}

