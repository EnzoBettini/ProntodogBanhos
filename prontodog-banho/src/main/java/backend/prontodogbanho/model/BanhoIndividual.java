package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="banhos_individuais", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanhoIndividual {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="data_banho", nullable=false)
    private LocalDate dataBanho;

    @Column(name="numero_banho", nullable=false)
    private Integer numeroBanho;

    @Column(name="observacoes")
    private String observacoes;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="animal_servico_id", nullable = false)
    @JsonBackReference("animalservico-banho")
    private AnimalServico animalServico;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="usuario_id")
    @JsonBackReference("usuario-banho")
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
