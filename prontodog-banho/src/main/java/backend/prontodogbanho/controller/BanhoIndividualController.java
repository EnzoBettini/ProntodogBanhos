package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.BanhoIndividual;
import backend.prontodogbanho.service.BanhoIndividualService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/banho-individual")
public class BanhoIndividualController {

    private final BanhoIndividualService banhoIndividualService;

    public BanhoIndividualController(BanhoIndividualService banhoIndividualService) {
        this.banhoIndividualService = banhoIndividualService;
    }

    // Listar todos os banhos
    @GetMapping
    public List<BanhoIndividual> listarTodos() {
        return banhoIndividualService.listarTodos();
    }

    // Buscar banho por ID
    @GetMapping("/{id}")
    public ResponseEntity<BanhoIndividual> buscarPorId(@PathVariable Long id) {
        Optional<BanhoIndividual> banho = banhoIndividualService.buscarPorId(id);
        return banho.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Buscar banhos de um AnimalServico específico
    @GetMapping("/animal-servico/{animalServicoId}")
    public List<BanhoIndividual> buscarPorAnimalServico(@PathVariable Long animalServicoId) {
        return banhoIndividualService.buscarPorAnimalServico(animalServicoId);
    }

    // Buscar banhos de um animal
    @GetMapping("/animal/{animalId}")
    public List<BanhoIndividual> buscarPorAnimal(@PathVariable Long animalId) {
        return banhoIndividualService.buscarPorAnimal(animalId);
    }

    // Buscar banhos de um cliente
    @GetMapping("/cliente/{clienteId}")
    public List<BanhoIndividual> buscarPorCliente(@PathVariable Long clienteId) {
        return banhoIndividualService.buscarPorCliente(clienteId);
    }

    // Buscar banhos por data específica
    @GetMapping("/data/{data}")
    public List<BanhoIndividual> buscarPorData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return banhoIndividualService.buscarPorData(data);
    }

    // Buscar banhos por período
    @GetMapping("/periodo")
    public List<BanhoIndividual> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return banhoIndividualService.buscarPorPeriodo(dataInicio, dataFim);
    }

    // Registrar um novo banho
    @PostMapping
    public ResponseEntity<BanhoIndividual> registrarBanho(@RequestBody Map<String, Object> dadosBanho) {
        try {
            Long animalServicoId = Long.valueOf(dadosBanho.get("animalServicoId").toString());
            LocalDate dataBanho = LocalDate.parse(dadosBanho.get("dataBanho").toString());
            String observacoes = dadosBanho.get("observacoes") != null ?
                                dadosBanho.get("observacoes").toString() : null;
            Long usuarioId = dadosBanho.get("usuarioId") != null ?
                            Long.valueOf(dadosBanho.get("usuarioId").toString()) : null;

            BanhoIndividual novoBanho = banhoIndividualService.registrarBanho(
                animalServicoId, dataBanho, observacoes, usuarioId);

            return ResponseEntity.ok(novoBanho);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Atualizar um banho existente
    @PutMapping("/{id}")
    public ResponseEntity<BanhoIndividual> atualizarBanho(
            @PathVariable Long id,
            @RequestBody Map<String, Object> dadosAtualizacao) {
        try {
            LocalDate novaData = dadosAtualizacao.get("dataBanho") != null ?
                                LocalDate.parse(dadosAtualizacao.get("dataBanho").toString()) : null;
            String novasObservacoes = dadosAtualizacao.get("observacoes") != null ?
                                    dadosAtualizacao.get("observacoes").toString() : null;
            Long novoUsuarioId = dadosAtualizacao.get("usuarioId") != null ?
                               Long.valueOf(dadosAtualizacao.get("usuarioId").toString()) : null;

            BanhoIndividual banhoAtualizado = banhoIndividualService.atualizarBanho(
                id, novaData, novasObservacoes, novoUsuarioId);

            return ResponseEntity.ok(banhoAtualizado);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Excluir um banho
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirBanho(@PathVariable Long id) {
        try {
            banhoIndividualService.excluirBanho(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Verificar quantos banhos restam em um pacote
    @GetMapping("/restantes/{animalServicoId}")
    public ResponseEntity<Map<String, Integer>> verificarBanhosRestantes(@PathVariable Long animalServicoId) {
        Integer banhosRestantes = banhoIndividualService.calcularBanhosRestantes(animalServicoId);
        return ResponseEntity.ok(Map.of("banhosRestantes", banhosRestantes));
    }
}
