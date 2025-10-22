package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.Bandeira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BandeiraRepository extends JpaRepository<Bandeira, Long> {

    // Buscar bandeiras ativas
    List<Bandeira> findByAtivoTrue();

    // Buscar por nome
    Optional<Bandeira> findByNome(String nome);

    // Buscar por código
    Optional<Bandeira> findByCodigo(String codigo);

    // Buscar bandeiras ordenadas por nome
    List<Bandeira> findByAtivoTrueOrderByNomeAsc();
}

