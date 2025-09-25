package backend.prontodogbanho.service;

import backend.prontodogbanho.model.Telefone;
import backend.prontodogbanho.repository.TelefoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TelefoneService {

    private TelefoneRepository telefoneRepository;

    public TelefoneService(TelefoneRepository telefoneRepository) {
        this.telefoneRepository = telefoneRepository;
    }

    public List<Telefone> listarTodos() {
        return telefoneRepository.findAll();
    }

    public Optional<Telefone> buscarPorId(Long id) {
        return telefoneRepository.findById(id);
    }

    public Telefone salvar(Telefone telefone) {
        return telefoneRepository.save(telefone);
    }

    public void deletar(Long id) {
        telefoneRepository.deleteById(id);
    }
}
