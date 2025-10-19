package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.FormaPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {

    // Buscar formas de pagamento ativas
    List<FormaPagamento> findByAtivoTrue();

    // Buscar por tipo (dinheiro, credito, debito, pix, etc)
    List<FormaPagamento> findByTipo(String tipo);

    // Buscar por tipo e ativas
    List<FormaPagamento> findByTipoAndAtivoTrue(String tipo);

    // Buscar por nome
    Optional<FormaPagamento> findByNome(String nome);

    // Buscar formas que permitem parcelamento
    @Query("SELECT f FROM FormaPagamento f WHERE f.parcelasMax > 1 AND f.ativo = true")
    List<FormaPagamento> findFormasComParcelamento();
}

