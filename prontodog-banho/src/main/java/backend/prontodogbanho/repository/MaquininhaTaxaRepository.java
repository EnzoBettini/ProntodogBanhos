package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.MaquininhaTaxa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaquininhaTaxaRepository extends JpaRepository<MaquininhaTaxa, Long> {

    // Buscar todas as taxas de uma maquininha
    List<MaquininhaTaxa> findByMaquininha_Id(Long maquininhaId);

    // Buscar todas as taxas de uma bandeira
    List<MaquininhaTaxa> findByBandeira_Id(Long bandeiraId);

    // Buscar taxa específica por maquininha e bandeira
    List<MaquininhaTaxa> findByMaquininha_IdAndBandeira_Id(Long maquininhaId, Long bandeiraId);

    // Buscar taxa específica por tipo de transação
    List<MaquininhaTaxa> findByMaquininha_IdAndTipoTransacao(Long maquininhaId, String tipoTransacao);

    // Buscar taxa específica por maquininha, bandeira e tipo
    List<MaquininhaTaxa> findByMaquininha_IdAndBandeira_IdAndTipoTransacao(
        Long maquininhaId,
        Long bandeiraId,
        String tipoTransacao
    );

    // Buscar taxa exata (incluindo número de parcelas)
    @Query("SELECT t FROM MaquininhaTaxa t WHERE " +
           "t.maquininha.id = :maquininhaId AND " +
           "t.bandeira.id = :bandeiraId AND " +
           "t.tipoTransacao = :tipoTransacao AND " +
           "((:numeroParcelas IS NULL AND t.numeroParcelas IS NULL) OR t.numeroParcelas = :numeroParcelas)")
    Optional<MaquininhaTaxa> findTaxaExata(
        @Param("maquininhaId") Long maquininhaId,
        @Param("bandeiraId") Long bandeiraId,
        @Param("tipoTransacao") String tipoTransacao,
        @Param("numeroParcelas") Integer numeroParcelas
    );

    // Buscar todas as taxas de débito de uma maquininha
    @Query("SELECT t FROM MaquininhaTaxa t WHERE t.maquininha.id = :maquininhaId AND t.tipoTransacao = 'debito'")
    List<MaquininhaTaxa> findTaxasDebito(@Param("maquininhaId") Long maquininhaId);

    // Buscar todas as taxas de crédito de uma maquininha
    @Query("SELECT t FROM MaquininhaTaxa t WHERE " +
           "t.maquininha.id = :maquininhaId AND " +
           "(t.tipoTransacao = 'credito_avista' OR t.tipoTransacao = 'credito_parcelado') " +
           "ORDER BY t.tipoTransacao, t.numeroParcelas")
    List<MaquininhaTaxa> findTaxasCredito(@Param("maquininhaId") Long maquininhaId);

    // Contar taxas configuradas por maquininha
    Long countByMaquininha_Id(Long maquininhaId);

    // Verificar se existe taxa configurada
    boolean existsByMaquininha_IdAndBandeira_IdAndTipoTransacaoAndNumeroParcelas(
        Long maquininhaId,
        Long bandeiraId,
        String tipoTransacao,
        Integer numeroParcelas
    );

    // Deletar todas as taxas de uma maquininha
    void deleteByMaquininha_Id(Long maquininhaId);

    // Deletar todas as taxas de uma bandeira específica de uma maquininha
    void deleteByMaquininha_IdAndBandeira_Id(Long maquininhaId, Long bandeiraId);
}

