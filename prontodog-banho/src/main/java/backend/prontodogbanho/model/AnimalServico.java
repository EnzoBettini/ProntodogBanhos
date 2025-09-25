package backend.prontodogbanho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="animal_id", nullable = false)
    private Animal animal;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="servico_id", nullable = false)
    private Servico servico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable = false)
    private Usuario usuario;
}
