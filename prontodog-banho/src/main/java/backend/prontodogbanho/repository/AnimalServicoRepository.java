package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.AnimalServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalServicoRepository extends JpaRepository<AnimalServico, Long> {

    /**
     * 📊 Buscar todos os serviços de um animal ordenados por data (mais recente primeiro)
     */
    List<AnimalServico> findByAnimalIdOrderByDataServicoDesc(Long animalId);
}
