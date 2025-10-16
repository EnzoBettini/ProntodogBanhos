package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name="servicos", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name="pode_ser_adicional")
    private Boolean podeSerAdicional = true;

    @Column(name="categoria")
    private String categoria = "geral";

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL)
    @JsonManagedReference("servico-servico")
    private List<AnimalServico> servicosAnimais;

    @OneToMany(mappedBy = "servicoAdicional", cascade = CascadeType.ALL)
    @JsonManagedReference("servico-adicional")
    private List<ServicoAdicional> servicosComoAdicional;

    // Métodos helper
    public boolean isPacote() {
        return quantidade != null && quantidade > 1;
    }

    public boolean podeSerUsadoComoAdicional() {
        return podeSerAdicional != null && podeSerAdicional;
    }

    public String getCategoria() {
        if (categoria == null || categoria.isEmpty()) {
            // Auto-categorização baseada no nome se categoria não estiver definida
            if (nome != null) {
                String nomeLower = nome.toLowerCase();
                if (nomeLower.contains("banho")) return "banho";
                if (nomeLower.contains("tosa")) return "tosa";
                if (isPacote()) return "pacote";
            }
            return "geral";
        }
        return categoria;
    }
}
