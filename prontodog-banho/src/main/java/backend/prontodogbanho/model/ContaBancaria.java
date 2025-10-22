package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="contas_bancarias", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaBancaria {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="nome", nullable=false, unique=true, length=200)
    private String nome;

    @Column(name="banco", nullable=false, length=100)
    private String banco;

    @Column(name="agencia", length=20)
    private String agencia;

    @Column(name="conta", length=50)
    private String conta;

    @Column(name="tipo", length=50)
    private String tipo; // corrente, poupanca, pagamento

    @Column(name="pix_chave", length=200)
    private String pixChave;

    @Column(name="ativo")
    private Boolean ativo = true;

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy="contaBancaria", cascade=CascadeType.ALL)
    @JsonManagedReference("conta-bancaria-maquininha")
    private List<Maquininha> maquininhas;

    // Atualizar timestamp antes de salvar
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Métodos helper
    public boolean isAtiva() {
        return ativo != null && ativo;
    }

    public boolean isContaCorrente() {
        return "corrente".equalsIgnoreCase(tipo);
    }

    public boolean isContaPoupanca() {
        return "poupanca".equalsIgnoreCase(tipo);
    }

    public boolean isContaPagamento() {
        return "pagamento".equalsIgnoreCase(tipo);
    }

    public boolean temPixChave() {
        return pixChave != null && !pixChave.trim().isEmpty();
    }

    // Retornar descrição completa
    public String getDescricaoCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append(nome);
        if (banco != null) {
            sb.append(" - ").append(banco);
        }
        if (agencia != null && conta != null) {
            sb.append(" (Ag: ").append(agencia).append(" / Cc: ").append(conta).append(")");
        }
        return sb.toString();
    }
}

