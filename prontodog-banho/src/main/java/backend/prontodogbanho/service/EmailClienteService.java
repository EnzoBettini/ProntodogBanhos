package backend.prontodogbanho.service;

import backend.prontodogbanho.model.EmailCliente;
import backend.prontodogbanho.repository.EmailClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmailClienteService {

    private EmailClienteRepository emailClienteRepository;

    public EmailClienteService(EmailClienteRepository emailClienteRepository) {
        this.emailClienteRepository = emailClienteRepository;
    }

    public List<EmailCliente> listarTodos() {
        return emailClienteRepository.findAll();
    }

    public Optional<EmailCliente> buscarPorId(Long id) {
        return emailClienteRepository.findById(id);
    }

    public EmailCliente salvar(EmailCliente emailCliente) {
        return emailClienteRepository.save(emailCliente);
    }

    public void deletar(Long id) {
        emailClienteRepository.deleteById(id);
    }

    public EmailCliente atualizarEmail(Long id, EmailCliente novosDados) {
        Optional<EmailCliente> emailOptional = emailClienteRepository.findById(id);
        if (emailOptional.isPresent()) {
            EmailCliente emailClienteExistente = emailOptional.get();
            emailClienteExistente.setEmail(novosDados.getEmail());
            return emailClienteRepository.save(emailClienteExistente);
        } else {
            throw new RuntimeException("Email não encontrado com id: " + id);
        }
    }
}
