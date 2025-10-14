package backend.prontodogbanho.repository;

import backend.prontodogbanho.model.BanhoIndividual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BanhoIndividualRepository extends JpaRepository<BanhoIndividual, Long> {

    // Buscar todos os banhos de um AnimalServico específico
    List<BanhoIndividual> findByAnimalServicoIdOrderByNumeroBanho(Long animalServicoId);

    // Buscar banhos por animal específico
    @Query("SELECT bi FROM BanhoIndividual bi WHERE bi.animalServico.animal.id = :animalId ORDER BY bi.dataBanho DESC")
    List<BanhoIndividual> findByAnimalIdOrderByDataBanhoDesc(@Param("animalId") Long animalId);

    // Buscar banhos por cliente específico
    @Query("SELECT bi FROM BanhoIndividual bi WHERE bi.animalServico.animal.cliente.id = :clienteId ORDER BY bi.dataBanho DESC")
    List<BanhoIndividual> findByClienteIdOrderByDataBanhoDesc(@Param("clienteId") Long clienteId);

    // Buscar banhos realizados em uma data específica
    List<BanhoIndividual> findByDataBanhoOrderByCreatedAt(LocalDate dataBanho);

    // Buscar banhos em um período
    List<BanhoIndividual> findByDataBanhoBetweenOrderByDataBanhoDesc(LocalDate dataInicio, LocalDate dataFim);

    // Buscar último banho de um AnimalServico
    @Query("SELECT bi FROM BanhoIndividual bi WHERE bi.animalServico.id = :animalServicoId ORDER BY bi.numeroBanho DESC LIMIT 1")
    BanhoIndividual findUltimoBanhoByAnimalServicoId(@Param("animalServicoId") Long animalServicoId);

    // Contar quantos banhos foram realizados de um AnimalServico
    Long countByAnimalServicoId(Long animalServicoId);

    // Verificar se já existe um banho com determinado número para um AnimalServico
    boolean existsByAnimalServicoIdAndNumeroBanho(Long animalServicoId, Integer numeroBanho);
}
