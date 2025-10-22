package backend.prontodogbanho.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="adquirentes", schema="banhoetosa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Adquirente {

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

    @OneToMany(mappedBy="adquirente", cascade=CascadeType.ALL)
    @JsonManagedReference("adquirente-maquininha")
    private List<Maquininha> maquininhas;

    // Métodos helper
    public boolean isAtivo() {
        return ativo != null && ativo;
    }

    public int getTotalMaquininhas() {
        return maquininhas != null ? maquininhas.size() : 0;
    }

    public long getMaquininhasAtivas() {
        if (maquininhas == null) return 0;
        return maquininhas.stream()
                .filter(m -> m.getAtivo() != null && m.getAtivo())
                .count();
    }
}

