package backend.prontodogbanho.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="clientes")
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

    public Cliente(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigoClienteSistema() {
        return codigoClienteSistema;
    }

    public void setCodigoClienteSistema(Long codigoClienteSistema) {
        this.codigoClienteSistema = codigoClienteSistema;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getCodigoSimplesVet() {
        return codigoSimplesVet;
    }

    public void setCodigoSimplesVet(Long codigoSimplesVet) {
        this.codigoSimplesVet = codigoSimplesVet;
    }

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<Telefone> telefones) {
        this.telefones = telefones;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public List<Animal> getAnimais() {
        return animais;
    }

    public void setAnimais(List<Animal> animais) {
        this.animais = animais;
    }
}
