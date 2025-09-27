package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone,Long> {

    @Query("SELECT t FROM Telefone t WHERE t.cliente.id = :idCliente AND t.telefone = :telefoneSearch")
    Telefone findTelefonesByCliente(@Param("idCliente") Long idCliente, @Param("telefoneSearch") String telefoneSearch);
}
