package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.AnimalServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalServicoRepository extends JpaRepository<AnimalServico, Long> {
}
