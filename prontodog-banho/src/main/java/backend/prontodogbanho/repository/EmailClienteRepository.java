package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.EmailCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailClienteRepository extends JpaRepository<EmailCliente, Long> {

    @Query("SELECT e FROM EmailCliente e WHERE e.cliente.id = :idCliente")
    List<EmailCliente> findEmailsByCliente(@Param("idCliente") Long idCliente);
}
