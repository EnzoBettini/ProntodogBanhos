package backend.prontodogbanho.model;

import jakarta.persistence.*;

@Entity
@Table(name="email_clientes")
public class Email {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private  Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cliente_id", nullable=false)
    private Cliente cliente;

    @Column(name="email", nullable=false)
    private String email;

    public Email() {}

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
