package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="servicos", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servico {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="nome", nullable=false, unique=true)
    private String nome;

    @Column(name="descricao")
    private String descricao;

    @Column(name="quantidade")
    private Integer quantidade;

    @Column(name="valor")
    private Double valor;

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL)
    @JsonManagedReference("servico-servico")
    private List<AnimalServico> servicosAnimais;
}
