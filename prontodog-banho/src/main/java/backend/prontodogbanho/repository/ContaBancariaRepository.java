package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {

    // Buscar contas ativas
    List<ContaBancaria> findByAtivoTrue();

    // Buscar por nome
    Optional<ContaBancaria> findByNome(String nome);

    // Buscar por banco
    List<ContaBancaria> findByBanco(String banco);

    // Buscar por banco e ativas
    List<ContaBancaria> findByBancoAndAtivoTrue(String banco);

    // Buscar por tipo (corrente, poupanca, pagamento)
    List<ContaBancaria> findByTipo(String tipo);

    // Buscar por tipo e ativas
    List<ContaBancaria> findByTipoAndAtivoTrue(String tipo);

    // Buscar contas com PIX
    @Query("SELECT c FROM ContaBancaria c WHERE c.pixChave IS NOT NULL AND c.pixChave != '' AND c.ativo = true")
    List<ContaBancaria> findContasComPix();
}

