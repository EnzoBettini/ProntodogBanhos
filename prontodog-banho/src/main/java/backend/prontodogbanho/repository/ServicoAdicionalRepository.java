package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.ServicoAdicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoAdicionalRepository extends JpaRepository<ServicoAdicional, Long> {

    /**
     * Buscar todos os serviços adicionais de um animal serviço específico
     */
    @Query("SELECT sa FROM ServicoAdicional sa WHERE sa.animalServicoPrincipal.id = :animalServicoId ORDER BY sa.dataAdicao DESC")
    List<ServicoAdicional> findByAnimalServicoPrincipalId(@Param("animalServicoId") Long animalServicoId);

    /**
     * Buscar serviços adicionais por status de pagamento
     */
    @Query("SELECT sa FROM ServicoAdicional sa WHERE sa.statusPagamento = :status ORDER BY sa.dataAdicao DESC")
    List<ServicoAdicional> findByStatusPagamento(@Param("status") String status);

    /**
     * Buscar serviços adicionais de um determinado tipo de serviço
     */
    @Query("SELECT sa FROM ServicoAdicional sa WHERE sa.servicoAdicional.id = :servicoId ORDER BY sa.dataAdicao DESC")
    List<ServicoAdicional> findByServicoAdicionalId(@Param("servicoId") Long servicoId);

    /**
     * Buscar serviços adicionais de um usuário específico
     */
    @Query("SELECT sa FROM ServicoAdicional sa WHERE sa.usuario.id = :usuarioId ORDER BY sa.dataAdicao DESC")
    List<ServicoAdicional> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Contar serviços adicionais em aberto
     */
    @Query("SELECT COUNT(sa) FROM ServicoAdicional sa WHERE sa.statusPagamento = 'em_aberto'")
    Long countEmAberto();

    /**
     * Contar serviços adicionais pagos
     */
    @Query("SELECT COUNT(sa) FROM ServicoAdicional sa WHERE sa.statusPagamento = 'pago'")
    Long countPagos();

    /**
     * Buscar serviços adicionais com joins para evitar N+1
     */
    @Query("SELECT sa FROM ServicoAdicional sa " +
           "LEFT JOIN FETCH sa.animalServicoPrincipal " +
           "LEFT JOIN FETCH sa.servicoAdicional " +
           "LEFT JOIN FETCH sa.usuario " +
           "WHERE sa.animalServicoPrincipal.id = :animalServicoId " +
           "ORDER BY sa.dataAdicao DESC")
    List<ServicoAdicional> findByAnimalServicoPrincipalIdWithFetch(@Param("animalServicoId") Long animalServicoId);
}
