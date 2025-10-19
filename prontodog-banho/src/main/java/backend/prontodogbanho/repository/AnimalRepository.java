package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    @Query("SELECT MAX(a.codigoAnimalSistema) FROM Animal a")
    Long findMaxCodigoAnimalSistema();

    // 🔍 Busca paginada com filtros dinâmicos usando SQL nativo
    @Query(value = "SELECT a.* FROM banhoetosa.animais a " +
                   "JOIN banhoetosa.clientes c ON c.id = a.cliente_id " +
                   "WHERE (:nome IS NULL OR a.nome::text ILIKE CONCAT('%', :nome, '%')) " +
                   "AND (:tipo IS NULL OR a.tipo::text ILIKE CONCAT('%', :tipo, '%')) " +
                   "AND (:raca IS NULL OR a.raca::text ILIKE CONCAT('%', :raca, '%')) " +
                   "AND (:codigoSimplesVet IS NULL OR :codigoSimplesVet = '' OR a.codigo_simplesvet = :codigoNumerico) " +
                   "AND (:clienteNome IS NULL OR c.nome_completo::text ILIKE CONCAT('%', :clienteNome, '%')) " +
                   "ORDER BY a.nome",
           countQuery = "SELECT COUNT(*) FROM banhoetosa.animais a " +
                       "JOIN banhoetosa.clientes c ON c.id = a.cliente_id " +
                       "WHERE (:nome IS NULL OR a.nome::text ILIKE CONCAT('%', :nome, '%')) " +
                       "AND (:tipo IS NULL OR a.tipo::text ILIKE CONCAT('%', :tipo, '%')) " +
                       "AND (:raca IS NULL OR a.raca::text ILIKE CONCAT('%', :raca, '%')) " +
                       "AND (:codigoSimplesVet IS NULL OR :codigoSimplesVet = '' OR a.codigo_simplesvet = :codigoNumerico) " +
                       "AND (:clienteNome IS NULL OR c.nome_completo::text ILIKE CONCAT('%', :clienteNome, '%'))",
           nativeQuery = true)
    Page<Animal> findAnimaisComFiltros(
        @Param("nome") String nome,
        @Param("tipo") String tipo,
        @Param("raca") String raca,
        @Param("codigoSimplesVet") String codigoSimplesVet,
        @Param("codigoNumerico") Long codigoNumerico,
        @Param("clienteNome") String clienteNome,
        Pageable pageable
    );

    // 🔍 Busca simples por nome (para o SearchSelect)
    @Query("SELECT a FROM Animal a WHERE LOWER(a.nome) LIKE LOWER(CONCAT('%', :termo, '%')) ORDER BY a.nome ASC")
    Page<Animal> findByNomeContainingIgnoreCase(@Param("termo") String termo, Pageable pageable);

    // 🔍 Buscar animais de um cliente específico
    @Query("SELECT a FROM Animal a WHERE a.cliente.id = :clienteId ORDER BY a.nome ASC")
    java.util.List<Animal> findByCliente_Id(@Param("clienteId") Long clienteId);
}
