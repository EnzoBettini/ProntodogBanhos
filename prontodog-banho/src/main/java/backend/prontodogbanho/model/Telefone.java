package backend.prontodogbanho.model;

import jakarta.persistence.*;

@Entity
@Table(name="telefones_clientes")
public class Telefone {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cliente_id", nullable=false)
    private Cliente cliente;

    @Column(name="telefone", nullable=false)
    private String telefone;

    public Telefone() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
