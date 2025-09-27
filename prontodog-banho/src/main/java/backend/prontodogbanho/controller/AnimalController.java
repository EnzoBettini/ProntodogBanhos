package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.service.AnimalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
}
