package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{
    @Query("SELECT MAX(c.codigoClienteSistema) FROM Cliente c")
    Long findMaxCodigoClienteSistema();
}