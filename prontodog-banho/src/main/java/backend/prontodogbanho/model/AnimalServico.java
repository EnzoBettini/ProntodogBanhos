package backend.prontodogbanho.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="animal_servico")
public class AnimalServico {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="data_servico", nullable=false)
    private LocalDate dataServico;

    @Column(name="banhos_usados", nullable = false)
    private Integer banhosUsados;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="animal_id", nullable = false)
    private Animal animal;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="servico_id", nullable = false)
    private Servico servico;


    public AnimalServico() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataServico() {
        return dataServico;
    }

    public void setDataServico(LocalDate dataServico) {
        this.dataServico = dataServico;
    }

    public Integer getBanhosUsados() {
        return banhosUsados;
    }

    public void setBanhosUsados(Integer banhosUsados) {
        this.banhosUsados = banhosUsados;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }
}
