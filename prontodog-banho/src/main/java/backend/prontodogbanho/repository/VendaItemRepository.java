package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.VendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendaItemRepository extends JpaRepository<VendaItem, Long> {

    // Buscar itens de uma venda
    List<VendaItem> findByVenda_Id(Long vendaId);

    // Buscar item por animal_servico
    Optional<VendaItem> findByAnimalServico_Id(Long animalServicoId);

    // Verificar se um animal_servico já está em uma venda
    boolean existsByAnimalServico_Id(Long animalServicoId);

    // Contar itens de uma venda
    Long countByVenda_Id(Long vendaId);

    // Calcular valor total dos itens de uma venda
    @Query("SELECT SUM(vi.valorFinalItem) FROM VendaItem vi WHERE vi.venda.id = :vendaId")
    Double calcularValorTotalItens(@Param("vendaId") Long vendaId);

    // Buscar itens de vendas de um cliente
    @Query("SELECT vi FROM VendaItem vi WHERE vi.venda.cliente.id = :clienteId ORDER BY vi.venda.dataVenda DESC")
    List<VendaItem> findByClienteId(@Param("clienteId") Long clienteId);
}

