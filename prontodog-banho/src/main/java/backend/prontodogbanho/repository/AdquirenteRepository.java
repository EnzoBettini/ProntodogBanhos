package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.Adquirente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdquirenteRepository extends JpaRepository<Adquirente, Long> {

    // Buscar adquirentes ativos
    List<Adquirente> findByAtivoTrue();

    // Buscar por nome
    Optional<Adquirente> findByNome(String nome);

    // Buscar por código
    Optional<Adquirente> findByCodigo(String codigo);

    // Buscar adquirentes ordenados por nome
    List<Adquirente> findByAtivoTrueOrderByNomeAsc();
}

