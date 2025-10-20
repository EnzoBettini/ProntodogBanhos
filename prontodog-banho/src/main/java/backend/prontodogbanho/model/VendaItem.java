package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="venda_itens", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="venda_id", nullable=false)
    @JsonBackReference("venda-item")
    private Venda venda;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="animal_servico_id", nullable=false)
    @JsonBackReference("item-animal-servico")
    private AnimalServico animalServico;

    @Column(name="valor_item", precision=10, scale=2, nullable=false)
    private BigDecimal valorItem = BigDecimal.ZERO;

    @Column(name="desconto_item", precision=10, scale=2)
    private BigDecimal descontoItem = BigDecimal.ZERO;

    @Column(name="valor_final_item", precision=10, scale=2, nullable=false)
    private BigDecimal valorFinalItem = BigDecimal.ZERO;

    @Column(name="valor_pago_item", precision=10, scale=2, nullable=false)
    private BigDecimal valorPagoItem = BigDecimal.ZERO;

    @Column(name="observacoes", columnDefinition="TEXT")
    private String observacoes;

    // Métodos helper para expor IDs
    public Long getVendaId() {
        return venda != null ? venda.getId() : null;
    }

    public Long getAnimalServicoId() {
        return animalServico != null ? animalServico.getId() : null;
    }

    // Informações do animal e serviço
    public String getAnimalNome() {
        return animalServico != null && animalServico.getAnimal() != null ?
               animalServico.getAnimal().getNome() : null;
    }

    public String getServicoNome() {
        return animalServico != null && animalServico.getServico() != null ?
               animalServico.getServico().getNome() : null;
    }

    // Método para calcular valor final automaticamente
    @PrePersist
    @PreUpdate
    public void calcularValorFinal() {
        if (this.valorItem != null && this.descontoItem != null) {
            this.valorFinalItem = this.valorItem.subtract(this.descontoItem);
        }
    }

    // Método helper para verificar se tem desconto
    public boolean temDesconto() {
        return descontoItem != null && descontoItem.compareTo(BigDecimal.ZERO) > 0;
    }

    // Calcular percentual de desconto
    public BigDecimal getPercentualDesconto() {
        if (valorItem.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return descontoItem.multiply(new BigDecimal("100"))
                .divide(valorItem, 2, java.math.RoundingMode.HALF_UP);
    }

    // Calcular percentual pago do item
    public BigDecimal getPercentualPago() {
        if (valorFinalItem.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return valorPagoItem.multiply(new BigDecimal("100"))
                .divide(valorFinalItem, 2, java.math.RoundingMode.HALF_UP);
    }

    // Calcular valor pendente do item
    public BigDecimal getValorPendente() {
        return valorFinalItem.subtract(valorPagoItem);
    }

    // Verificar se item está completamente pago
    public boolean isPago() {
        return valorPagoItem.compareTo(valorFinalItem) >= 0;
    }

    // Verificar se item está parcialmente pago
    public boolean isParcial() {
        return valorPagoItem.compareTo(BigDecimal.ZERO) > 0 &&
               valorPagoItem.compareTo(valorFinalItem) < 0;
    }

    // Incluir valor dos serviços adicionais no cálculo
    public BigDecimal getValorTotalComAdicionais() {
        BigDecimal valorBase = this.valorFinalItem;

        if (animalServico != null && animalServico.getServicosAdicionais() != null) {
            BigDecimal valorAdicionais = animalServico.getServicosAdicionais().stream()
                    .map(ServicoAdicional::getValorTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            valorBase = valorBase.add(valorAdicionais);
        }

        return valorBase;
    }
}

