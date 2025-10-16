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
@Table(name="servicos_adicionais", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicoAdicional {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="quantidade_adicional", nullable = false)
    private Integer quantidadeAdicional = 1;

    @Column(name="valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name="valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name="data_adicao", nullable = false)
    private LocalDateTime dataAdicao;

    @Column(name="observacoes", length = 255)
    private String observacoes;

    @Column(name="status_pagamento", nullable = false, length = 20)
    private String statusPagamento = "em_aberto";

    @Column(name="data_pagamento")
    private LocalDate dataPagamento;

    // Relacionamento com o serviço principal (AnimalServico)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="animal_servico_principal_id", nullable = false)
    @JsonBackReference("animal-servico-adicional")
    private AnimalServico animalServicoPrincipal;

    // Relacionamento com o tipo de serviço adicional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="servico_adicional_id", nullable = false)
    @JsonBackReference("servico-adicional")
    private Servico servicoAdicional;

    // Relacionamento com o usuário que adicionou
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable = false)
    @JsonBackReference("usuario-adicional")
    private Usuario usuario;

    // Método para calcular valor total automaticamente
    @PrePersist
    @PreUpdate
    public void calcularValorTotal() {
        if (this.valorUnitario != null && this.quantidadeAdicional != null) {
            this.valorTotal = this.valorUnitario.multiply(BigDecimal.valueOf(this.quantidadeAdicional));
        }
        if (this.dataAdicao == null) {
            this.dataAdicao = LocalDateTime.now();
        }
    }

    // Getters para expor IDs no JSON
    public Long getAnimalServicoPrincipalId() {
        return animalServicoPrincipal != null ? animalServicoPrincipal.getId() : null;
    }

    public Long getServicoAdicionalId() {
        return servicoAdicional != null ? servicoAdicional.getId() : null;
    }

    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    // Método helper para verificar se está pago
    public boolean isPago() {
        return "pago".equals(this.statusPagamento);
    }

    // Método helper para verificar se pode ser cancelado
    public boolean podeCancelar() {
        return !"pago".equals(this.statusPagamento);
    }
}
