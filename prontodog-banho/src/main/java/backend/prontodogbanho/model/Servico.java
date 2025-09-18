package backend.prontodogbanho.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="servicos")
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

    public Servico() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public List<AnimalServico> getServicosAnimais() {
        return servicosAnimais;
    }

    public void setServicosAnimais(List<AnimalServico> servicosAnimais) {
        this.servicosAnimais = servicosAnimais;
    }
}
