package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.VendaBaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaBaixaRepository extends JpaRepository<VendaBaixa, Long> {

    // Buscar baixas de uma venda
    List<VendaBaixa> findByVenda_IdOrderByDataBaixaDesc(Long vendaId);

    // Buscar baixas por forma de pagamento
    List<VendaBaixa> findByFormaPagamento_Id(Long formaPagamentoId);

    // Buscar baixas por período
    @Query("SELECT vb FROM VendaBaixa vb WHERE vb.dataBaixa BETWEEN :dataInicio AND :dataFim ORDER BY vb.dataBaixa DESC")
    List<VendaBaixa> findByPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    // Buscar baixas por usuário
    List<VendaBaixa> findByUsuario_IdOrderByDataBaixaDesc(Long usuarioId);

    // Contar baixas de uma venda
    Long countByVenda_Id(Long vendaId);

    // Calcular total pago em uma venda
    @Query("SELECT SUM(vb.valorBaixa) FROM VendaBaixa vb WHERE vb.venda.id = :vendaId")
    Double calcularTotalPagoVenda(@Param("vendaId") Long vendaId);

    // Calcular total de taxas pagas em uma venda
    @Query("SELECT SUM(vb.valorTaxa) FROM VendaBaixa vb WHERE vb.venda.id = :vendaId")
    Double calcularTotalTaxasVenda(@Param("vendaId") Long vendaId);

    // Buscar baixas por forma de pagamento em um período
    @Query("SELECT vb FROM VendaBaixa vb WHERE vb.formaPagamento.id = :formaPagamentoId AND vb.dataBaixa BETWEEN :dataInicio AND :dataFim")
    List<VendaBaixa> findByFormaPagamentoAndPeriodo(
        @Param("formaPagamentoId") Long formaPagamentoId,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );

    // Buscar baixas parceladas
    @Query("SELECT vb FROM VendaBaixa vb WHERE vb.numeroParcelas > 1 ORDER BY vb.dataBaixa DESC")
    List<VendaBaixa> findBaixasParceladas();
}

