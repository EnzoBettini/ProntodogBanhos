package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="email_clientes", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailCliente {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private  Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cliente_id")
    @JsonBackReference("cliente-email")
    private Cliente cliente;

    @Column(name="email", nullable=false)
    @Email(message = "Email inválido")
    private String email;
}
