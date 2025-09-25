package backend.prontodogbanho.service;

import backend.prontodogbanho.model.Email;
import backend.prontodogbanho.repository.EmailRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmailService {

    private EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public List<Email> listarTodos() {
        return emailRepository.findAll();
    }

    public Optional<Email> buscarPorId(Long id) {
        return emailRepository.findById(id);
    }

    public Email salvar(Email email) {
        return emailRepository.save(email);
    }

    public void deletar(Long id) {
        emailRepository.deleteById(id);
    }
}
