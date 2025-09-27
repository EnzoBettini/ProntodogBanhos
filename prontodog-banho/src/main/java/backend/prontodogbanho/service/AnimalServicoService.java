package backend.prontodogbanho.service;


import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.repository.AnimalServicoRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public AnimalServico salvar(AnimalServico animalServico) {
        return animalServicoRepository.save(animalServico);
    }

    public void deletar(Long id) {
        animalServicoRepository.deleteById(id);
    }

    @Transactional
    public AnimalServico atualizarTudo(Long id, AnimalServico novosDados) {
        Optional<AnimalServico> animalServicoOptional = animalServicoRepository.findById(id);

        if (animalServicoOptional.isPresent()) {
            AnimalServico animalServicoExistente = animalServicoOptional.get();

            animalServicoExistente.setDataServico(novosDados.getDataServico());
            animalServicoExistente.setBanhosUsados(novosDados.getBanhosUsados());
            animalServicoExistente.setAnimal(novosDados.getAnimal());
            animalServicoExistente.setServico(novosDados.getServico());
            animalServicoExistente.setUsuario(novosDados.getUsuario());

            return animalServicoRepository.save(animalServicoExistente);
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }
}
