package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {

    @Query("SELECT e FROM Email e WHERE e.cliente.id = :idCliente")
    List<Email> findEmailsByCliente(@Param("idCliente") Long idCliente);
}
