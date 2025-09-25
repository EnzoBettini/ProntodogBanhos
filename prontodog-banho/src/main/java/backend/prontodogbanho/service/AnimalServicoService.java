package backend.prontodogbanho.service;


import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.repository.AnimalServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalServicoService {

    private AnimalServicoRepository animalServicoRepository;

    public AnimalServicoService(AnimalServicoRepository animalServicoRepository) {
        this.animalServicoRepository = animalServicoRepository;
    }

    public List<AnimalServico> listarTodos() {
        return animalServicoRepository.findAll();
    }

    public Optional<AnimalServico> buscarPorId(Long id) {
        return animalServicoRepository.findById(id);
    }

    public AnimalServico salvar(AnimalServico animal) {
        return animalServicoRepository.save(animal);
    }

    public void deletar(Long id) {
        animalServicoRepository.deleteById(id);
    }
}
