package backend.prontodogbanho.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="codigo_clientes_sistema", unique=true, nullable=false)
    private Long codigoClienteSistema;

    @Column(name="nome_completo", nullable=false)
    private String nomeCompleto;

    @Column(name="cpf", unique=true)
    private String cpf;

    @Column(name="codigo_simplesvet", unique=true)
    private Long codigoSimplesVet;

    @OneToMany(mappedBy="cliente", cascade=CascadeType.ALL)
    private List<Telefone> telefones;

    @OneToMany(mappedBy="cliente", cascade=CascadeType.ALL)
    private List<Email> emails;

    @OneToMany(mappedBy="cliente", cascade=CascadeType.ALL)
    private List<Animal> animais;
}
