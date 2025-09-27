package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="telefones_clientes", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Telefone {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cliente_id", nullable=false)
    @JsonBackReference("cliente-telefone")
    private Cliente cliente;

    @Column(name="telefone", nullable=false)
    @Pattern(regexp="^\\d{10,11}$", message="Telefone inválido")
    private String telefone;
}
