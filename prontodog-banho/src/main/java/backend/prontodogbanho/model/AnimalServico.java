package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="animal_servico", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalServico {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="data_servico", nullable=false)
    private LocalDate dataServico;

    @Column(name="banhos_usados", nullable = false)
    private Integer banhosUsados;

    @Column(name="data_expiracao")
    private LocalDate dataExpiracao;

    @Column(name="status_pagamento", nullable = false)
    private String statusPagamento = "em_aberto";

    @Column(name="data_pagamento")
    private LocalDate dataPagamento;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="animal_id", nullable = false)
    @JsonBackReference("animal-servico")
    private Animal animal;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="servico_id", nullable = false)
    @JsonBackReference("servico-servico")
    private Servico servico;

    // Método para expor o servicoId no JSON sem quebrar o relacionamento
    public Long getServicoId() {
        return servico != null ? servico.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable = false)
    @JsonBackReference("usuario-servico")
    private Usuario usuario;

    // Método para expor o usuarioId no JSON sem quebrar o relacionamento
    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }

    @OneToMany(mappedBy="animalServico", cascade=CascadeType.ALL)
    @JsonManagedReference("animalservico-banho")
    private List<BanhoIndividual> banhosIndividuais;

    @OneToMany(mappedBy="animalServicoPrincipal", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JsonManagedReference("animal-servico-adicional")
    private List<ServicoAdicional> servicosAdicionais;

    // Métodos helper para cálculos financeiros
    public BigDecimal getValorTotalComAdicionais() {
        BigDecimal valorPrincipal = servico != null && servico.getValor() != null ?
            BigDecimal.valueOf(servico.getValor()) : BigDecimal.ZERO;

        BigDecimal valorAdicionais = servicosAdicionais != null ?
            servicosAdicionais.stream()
                .map(ServicoAdicional::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;

        return valorPrincipal.add(valorAdicionais);
    }

    public BigDecimal getValorAdicionaisPagos() {
        return servicosAdicionais != null ?
            servicosAdicionais.stream()
                .filter(ServicoAdicional::isPago)
                .map(ServicoAdicional::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
    }

    public BigDecimal getValorAdicionaisEmAberto() {
        return servicosAdicionais != null ?
            servicosAdicionais.stream()
                .filter(sa -> "em_aberto".equals(sa.getStatusPagamento()))
                .map(ServicoAdicional::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
    }

    public int getQuantidadeAdicionais() {
        return servicosAdicionais != null ? servicosAdicionais.size() : 0;
    }

    public boolean temAdicionais() {
        return servicosAdicionais != null && !servicosAdicionais.isEmpty();
    }
}
