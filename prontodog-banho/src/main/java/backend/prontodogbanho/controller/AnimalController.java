package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.service.AnimalService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/animal")
public class AnimalController {

    private final AnimalService animalService;

    public AnimalController(AnimalService animalService) {
        this.animalService = animalService;
    }

    @GetMapping
    public List<Animal> listarAnimals(){
        return this.animalService.listarTodos();
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<Animal>> buscarAnimaisFiltrado(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String raca,
            @RequestParam(required = false) String codigoSimplesVet,
            @RequestParam(required = false) String clienteNome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nome") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        try {
            System.out.println("🔍 Buscando animais com filtros:");
            System.out.println("  - Nome: " + nome);
            System.out.println("  - Tipo: " + tipo);
            System.out.println("  - Raça: " + raca);
            System.out.println("  - Código: " + codigoSimplesVet);
            System.out.println("  - Cliente: " + clienteNome);
            System.out.println("  - Página: " + page + " | Tamanho: " + size);
            System.out.println("  - Ordenação: " + sortBy + " " + sortDir);

            Page<Animal> resultado = this.animalService.buscarAnimaisComFiltros(
                nome, tipo, raca, codigoSimplesVet, clienteNome,
                page, size, sortBy, sortDir
            );

            System.out.println("✅ Encontrados " + resultado.getTotalElements() + " animais (página " +
                             (page + 1) + " de " + resultado.getTotalPages() + ")");

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar animais: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Animal> buscaPorId(@PathVariable Long id){
        Optional<Animal> animal = this.animalService.buscarPorId(id);
        return animal.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Animal> criar(@RequestBody Animal novoAnimal) {
        Animal novoAnimalResponse = this.animalService.salvar(novoAnimal);
        return ResponseEntity.ok(novoAnimalResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Animal> deletarPorId(@PathVariable Long id){
        this.animalService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/atualizarnome/{id}")
    public ResponseEntity<Animal> atualizarNomeAnimal(@PathVariable Long id, @RequestBody Animal novoAnimal) {
        Animal animalAtualizado = this.animalService.atualizarNome(id, novoAnimal);
        return ResponseEntity.ok(animalAtualizado);
    }

    @PutMapping("/atualizarcodigosimplesvet/{id}")
    public ResponseEntity<Animal> atualizarCodigoSimplesVet(@PathVariable Long id, @RequestBody Animal novoAnimal) {
        Animal animalAtualizado = this.animalService.atualizarCodigoSimplesVet(id, novoAnimal);
        return ResponseEntity.ok(animalAtualizado);
    }

    @PutMapping("/atualizarraca/{id}")
    public ResponseEntity<Animal> atualizarRaca(@PathVariable Long id, @RequestBody Animal novoAnimal) {
        Animal animalAtualizado = this.animalService.atualizarRaca(id, novoAnimal);
        return ResponseEntity.ok(animalAtualizado);
    }

    @PutMapping("/atualizarpeso/{id}")
    public ResponseEntity<Animal> atualizarPeso(@PathVariable Long id, @RequestBody Animal novoAnimal) {
        Animal animalAtualizado = this.animalService.atualizarPeso(id, novoAnimal);
        return ResponseEntity.ok(animalAtualizado);
    }

    @PutMapping("/atualizarcompleto/{id}")
    public ResponseEntity<Animal> atualizarCompleto(@PathVariable Long id, @RequestBody Animal novoAnimal) {
        Animal animalAtualizado = this.animalService.atualizarCompleto(id, novoAnimal);
        return ResponseEntity.ok(animalAtualizado);
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<Map<String, Object>> buscarHistoricoPorAnimal(@PathVariable Long id) {
        try {
            System.out.println("📊 Buscando histórico para animal ID: " + id);
            Map<String, Object> historico = this.animalService.buscarHistoricoCompleto(id);
            System.out.println("✅ Histórico encontrado com sucesso!");
            return ResponseEntity.ok(historico);
        } catch (RuntimeException e) {
            System.err.println("❌ Erro ao buscar histórico: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ Erro interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("erro", "Erro interno do servidor"));
        }
    }
}
