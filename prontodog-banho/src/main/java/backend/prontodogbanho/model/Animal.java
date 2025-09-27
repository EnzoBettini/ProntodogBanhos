package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="animais", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="codigo_animal_sistema", nullable = false, unique = true)
    private Long codigoAnimalSistema;

    @Column(name="nome", nullable=false)
    private String nome;

    @Column(name="codigo_simplesvet", unique = true)
    private Long codigoSimplesVet;

    @Column(name="tipo")
    private String tipo;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cliente_id", nullable = false)
    @JsonBackReference("cliente-animal")
    private Cliente cliente;

    @OneToMany(mappedBy="animal", cascade=CascadeType.ALL)
    @JsonManagedReference("animal-servico")
    private List<AnimalServico> servicos;
}
