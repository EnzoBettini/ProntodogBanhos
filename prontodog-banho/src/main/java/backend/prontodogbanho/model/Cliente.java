package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Entity
@Table(name="clientes", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="codigo_cliente_sistema", unique=true, nullable=false)
    private Long codigoClienteSistema;

    @Column(name="nome_completo", nullable=false)
    private String nomeCompleto;

    @Column(name="cpf", unique=true)
    @Pattern(regexp="^\\d{11}$", message="CPF inválido")
    private String cpf;

    @Column(name="codigo_simplesvet", unique=true)
    private Long codigoSimplesVet;

    @OneToMany(mappedBy="cliente", cascade=CascadeType.ALL)
    @JsonManagedReference("cliente-telefone")
    private List<Telefone> telefones;

    @OneToMany(mappedBy="cliente", cascade=CascadeType.ALL)
    @JsonManagedReference("cliente-email")
    private List<EmailCliente> emailClientes;

    @OneToMany(mappedBy="cliente", cascade=CascadeType.ALL)
    @JsonManagedReference("cliente-animal")
    private List<Animal> animais;
}
