package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="bandeiras", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bandeira {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="nome", nullable=false, unique=true, length=100)
    private String nome;

    @Column(name="codigo", length=50)
    private String codigo;

    @Column(name="ativo")
    private Boolean ativo = true;

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy="bandeira", cascade=CascadeType.ALL)
    @JsonManagedReference("bandeira-taxa")
    private List<MaquininhaTaxa> taxas;

    @OneToMany(mappedBy="bandeira", cascade=CascadeType.ALL)
    @JsonManagedReference("bandeira-baixa")
    private List<VendaBaixa> baixas;

    // Métodos helper
    public boolean isAtiva() {
        return ativo != null && ativo;
    }

    public int getTotalTaxasConfiguradas() {
        return taxas != null ? taxas.size() : 0;
    }

    // Bandeiras mais comuns
    public boolean isVisa() {
        return "VISA".equalsIgnoreCase(codigo) || "Visa".equalsIgnoreCase(nome);
    }

    public boolean isMastercard() {
        return "MASTER".equalsIgnoreCase(codigo) || nome.toLowerCase().contains("master");
    }

    public boolean isElo() {
        return "ELO".equalsIgnoreCase(codigo) || "Elo".equalsIgnoreCase(nome);
    }

    public boolean isAmex() {
        return "AMEX".equalsIgnoreCase(codigo) || nome.toLowerCase().contains("amex") || nome.toLowerCase().contains("american");
    }
}

