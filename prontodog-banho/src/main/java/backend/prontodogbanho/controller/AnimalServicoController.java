package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.service.AnimalServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/animalservico")
public class AnimalServicoController {

    private final AnimalServicoService animalServicoService;

    public AnimalServicoController(AnimalServicoService animalServicoService) {
        this.animalServicoService = animalServicoService;
    }

    @GetMapping
    public List<AnimalServico> listarAnimalServicos() {
        return animalServicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalServico> buscaPorId(@PathVariable Long id) {
        Optional<AnimalServico> animalServicoOptional = this.animalServicoService.buscarPorId(id);
        return animalServicoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AnimalServico> adicionarServico(@RequestBody AnimalServico animalServico) {
        AnimalServico novoAnimalServico= this.animalServicoService.salvar(animalServico);
        return ResponseEntity.ok(novoAnimalServico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerSerivco(@PathVariable Long id) {
        this.animalServicoService.deletar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalServico> atualizarServico(@PathVariable Long id, @RequestBody AnimalServico animalServico) {
        AnimalServico servicoAtualizado = this.animalServicoService.atualizarTudo(id, animalServico);
        return ResponseEntity.ok(servicoAtualizado);
    }
}
