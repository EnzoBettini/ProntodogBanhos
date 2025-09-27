package backend.prontodogbanho.service;

import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalService {
    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public List<Animal> listarTodos() {
        return animalRepository.findAll();
    }
    public Optional<Animal> buscarPorId(Long id) {
        return animalRepository.findById(id);
    }

    @Transactional
    public Animal salvar(Animal animal) {
        if (animal.getCodigoAnimalSistema() == null) {
            Long maxCodigo = animalRepository.findMaxCodigoAnimalSistema();
            animal.setCodigoAnimalSistema(maxCodigo != null ? maxCodigo + 1 : 1L);
        }
        return animalRepository.save(animal);
    }

    public void deletar(Long id) {
        animalRepository.deleteById(id);
    }

    @Transactional
    public Animal atualizarNome(Long id, Animal novosDados) {
        Optional<Animal> animalOptional = this.animalRepository.findById(id);
        if (animalOptional.isPresent()) {
            Animal animalExistente = animalOptional.get();
            animalExistente.setNome(novosDados.getNome());
            return animalRepository.save(animalExistente);
        } else {
            throw new RuntimeException("Animal não encontrado com id: " + id);
        }
    }

    @Transactional
    public Animal atualizarCodigoSimplesVet(Long id, Animal novosDados) {
        Optional<Animal> animalOptional = this.animalRepository.findById(id);
        if (animalOptional.isPresent()) {
            Animal animalExistente = animalOptional.get();
            animalExistente.setCodigoSimplesVet(novosDados.getCodigoSimplesVet());
            return animalRepository.save(animalExistente);
        } else {
            throw new RuntimeException("Animal não encontrado");
        }
    }
}
