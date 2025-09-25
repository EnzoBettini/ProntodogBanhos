package backend.prontodogbanho.service;

import backend.prontodogbanho.model.Cliente;
import backend.prontodogbanho.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }

    public Cliente atualizarTudo(Long id, Cliente novosDados) {
        Optional<Cliente> clienteOptional = this.clienteRepository.findById(id);

        if (clienteOptional.isPresent()) {
            Cliente clienteExistente = clienteOptional.get();

            clienteExistente.setNomeCompleto(novosDados.getNomeCompleto());
            clienteExistente.setCodigoSimplesVet(novosDados.getCodigoSimplesVet());
            clienteExistente.setCpf(novosDados.getCpf());
            clienteExistente.setTelefones(novosDados.getTelefones());
            clienteExistente.setEmails(novosDados.getEmails());
            clienteExistente.setEmails(novosDados.getEmails());

            return clienteRepository.save(clienteExistente);
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }
}
