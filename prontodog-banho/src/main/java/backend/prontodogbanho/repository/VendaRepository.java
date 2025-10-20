package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Buscar por código da venda
    Optional<Venda> findByCodigoVenda(Long codigoVenda);

    // Buscar vendas de um cliente
    List<Venda> findByCliente_Id(Long clienteId);

    // Buscar vendas de um cliente ordenadas por data
    List<Venda> findByCliente_IdOrderByDataVendaDesc(Long clienteId);

    // Buscar por status
    List<Venda> findByStatusVenda(String statusVenda);

    // Buscar vendas por período
    @Query("SELECT v FROM Venda v WHERE v.dataVenda BETWEEN :dataInicio AND :dataFim ORDER BY v.dataVenda DESC")
    List<Venda> findByPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    // Buscar vendas em aberto de um cliente
    @Query("SELECT v FROM Venda v WHERE v.cliente.id = :clienteId AND v.statusVenda IN ('em_aberto', 'parcial') ORDER BY v.dataVenda DESC")
    List<Venda> findVendasEmAbertoByCliente(@Param("clienteId") Long clienteId);

    // Buscar vendas por usuário (vendedor)
    List<Venda> findByUsuario_IdOrderByDataVendaDesc(Long usuarioId);

    // Buscar últimas vendas (para dashboard)
    @Query("SELECT v FROM Venda v ORDER BY v.dataVenda DESC")
    List<Venda> findUltimasVendas();

    // Contar vendas por status
    Long countByStatusVenda(String statusVenda);

    // Calcular total de vendas por período
    @Query("SELECT SUM(v.valorTotal) FROM Venda v WHERE v.dataVenda BETWEEN :dataInicio AND :dataFim AND v.statusVenda != 'cancelado'")
    Double calcularTotalVendasPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    // Buscar por tipo de venda
    List<Venda> findByTipoVendaOrderByDataVendaDesc(String tipoVenda);

    // Buscar próximo código da sequência
    @Query(value = "SELECT nextval('banhoetosa.seq_codigo_venda')", nativeQuery = true)
    Long getProximoCodigoVenda();
}

