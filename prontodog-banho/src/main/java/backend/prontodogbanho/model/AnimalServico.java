package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable = false)
    @JsonBackReference("usuario-servico")
    private Usuario usuario;

    @OneToMany(mappedBy="animalServico", cascade=CascadeType.ALL)
    @JsonManagedReference("animalservico-banho")
    private List<BanhoIndividual> banhosIndividuais;
}
