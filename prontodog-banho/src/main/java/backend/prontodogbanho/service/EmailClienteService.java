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

    public Email atualizarEmail(Long id, Email novosDados) {
        Optional<Email> emailOptional = emailRepository.findById(id);
        if (emailOptional.isPresent()) {
            Email emailExistente = emailOptional.get();
            emailExistente.setEmail(novosDados.getEmail());
            return emailRepository.save(emailExistente);
        } else {
            throw new RuntimeException("Email não encontrado com id: " + id);
        }
    }
}
