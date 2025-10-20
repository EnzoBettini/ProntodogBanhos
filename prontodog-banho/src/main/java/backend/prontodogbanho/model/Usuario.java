package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="usuarios", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="nome", nullable = false)
    private String nome;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="role", nullable = false)
    private String role;

    @OneToMany(mappedBy="usuario", cascade=CascadeType.ALL)
    @JsonManagedReference("usuario-servico")
    private List<AnimalServico> animalServicos;

    @OneToMany(mappedBy="usuario", cascade=CascadeType.ALL)
    @JsonManagedReference("usuario-banho")
    private List<BanhoIndividual> banhosIndividuais;

    @OneToMany(mappedBy="usuario", cascade=CascadeType.ALL)
    @JsonManagedReference("usuario-adicional")
    private List<ServicoAdicional> servicosAdicionais;

    @OneToMany(mappedBy="usuario", cascade=CascadeType.ALL)
    @JsonManagedReference("usuario-venda")
    private List<Venda> vendas;

    @OneToMany(mappedBy="usuario", cascade=CascadeType.ALL)
    @JsonManagedReference("usuario-baixa")
    private List<VendaBaixa> baixas;
}
