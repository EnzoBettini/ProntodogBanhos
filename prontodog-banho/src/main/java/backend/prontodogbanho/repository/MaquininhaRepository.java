package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.Maquininha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaquininhaRepository extends JpaRepository<Maquininha, Long> {

    // Buscar maquininhas ativas
    List<Maquininha> findByAtivoTrue();

    // Buscar por adquirente
    List<Maquininha> findByAdquirente_Id(Long adquirenteId);

    // Buscar por adquirente e ativas
    List<Maquininha> findByAdquirente_IdAndAtivoTrue(Long adquirenteId);

    // Buscar por conta bancária
    List<Maquininha> findByContaBancaria_Id(Long contaBancariaId);

    // Buscar por nome (parcial, case insensitive)
    @Query("SELECT m FROM Maquininha m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND m.ativo = true")
    List<Maquininha> searchByNome(@Param("nome") String nome);

    // Buscar maquininhas que aceitam PIX
    List<Maquininha> findByAceitaPixTrueAndAtivoTrue();

    // Buscar maquininhas com antecipação automática
    List<Maquininha> findByAntecipacaoAutomaticaTrueAndAtivoTrue();

    // Buscar maquininhas que aceitam antecipação
    List<Maquininha> findByAceitaAntecipacaoTrueAndAtivoTrue();

    // Buscar maquininha com todas as relações carregadas (para detalhes)
    @Query("SELECT DISTINCT m FROM Maquininha m " +
           "LEFT JOIN FETCH m.adquirente " +
           "LEFT JOIN FETCH m.contaBancaria " +
           "LEFT JOIN FETCH m.contaPix " +
           "WHERE m.id = :id")
    Optional<Maquininha> findByIdWithDetails(@Param("id") Long id);

    // Buscar maquininhas com suas taxas
    @Query("SELECT DISTINCT m FROM Maquininha m " +
           "LEFT JOIN FETCH m.taxas " +
           "WHERE m.ativo = true")
    List<Maquininha> findAllWithTaxas();
}

