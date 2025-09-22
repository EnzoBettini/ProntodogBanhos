package backend.prontodogbanho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="servicos")
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
    private List<AnimalServico> servicosAnimais;
}
