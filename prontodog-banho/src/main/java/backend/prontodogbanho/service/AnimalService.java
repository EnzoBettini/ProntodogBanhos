package backend.prontodogbanho.service;

import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalService {
    private final AnimalRepository animalRespository;

    public AnimalService(AnimalRepository animalRespository) {
        this.animalRespository = animalRespository;
    }

    public List<Animal> listarTodos() {
        return animalRespository.findAll();
    }
    public Optional<Animal> buscarPorId(Long id) {
        return animalRespository.findById(id);
    }

    public Animal salvar(Animal animal) {
        return animalRespository.save(animal);
    }

    public void deletar(Long id) {
        animalRespository.deleteById(id);
    }

    public Animal atualizarNome(Long id, Animal novosDados) {
        Optional<Animal> animalOptional = this.animalRespository.findById(id);
        if (animalOptional.isPresent()) {
            Animal animalExistente = animalOptional.get();
            animalExistente.setNome(novosDados.getNome());
            return animalRespository.save(animalExistente);
        } else {
            throw new RuntimeException("Animal não encontrado com id: " + id);
        }
    }
}
