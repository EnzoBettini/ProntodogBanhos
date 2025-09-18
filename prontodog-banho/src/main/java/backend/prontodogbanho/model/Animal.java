package backend.prontodogbanho.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="animais")
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
    private Long codigoSimpleSvet;

    @Column(name="tipo")
    private String tipo;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy="animal", cascade=CascadeType.ALL)
    private List<AnimalServico> servicos;

    public Animal() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigoAnimalSistema() {
        return codigoAnimalSistema;
    }

    public void setCodigoAnimalSistema(Long codigoAnimalSistema) {
        this.codigoAnimalSistema = codigoAnimalSistema;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getCodigoSimpleSvet() {
        return codigoSimpleSvet;
    }

    public void setCodigoSimpleSvet(Long codigoSimpleSvet) {
        this.codigoSimpleSvet = codigoSimpleSvet;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<AnimalServico> getServicos() {
        return servicos;
    }

    public void setServicos(List<AnimalServico> servicos) {
        this.servicos = servicos;
    }
}
