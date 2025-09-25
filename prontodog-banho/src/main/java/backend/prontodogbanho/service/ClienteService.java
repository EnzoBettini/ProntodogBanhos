package backend.prontodogbanho.service;

import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.model.Cliente;
import backend.prontodogbanho.repository.ClienteRespository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRespository clienteRespository;

    public ClienteService(ClienteRespository clienteRespository) {
        this.clienteRespository = clienteRespository;
    }

    public List<Cliente> listarTodos() {
        return clienteRespository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRespository.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        return clienteRespository.save(cliente);
    }

    public void deletar(Long id) {
        clienteRespository.deleteById(id);
    }

    public Cliente atualizarTudo(Long id, Cliente novosDados) {
        Optional<Cliente> clienteOptional = this.clienteRespository.findById(id);

        if (clienteOptional.isPresent()) {
            Cliente clienteExistente = clienteOptional.get();

            clienteExistente.setNomeCompleto(novosDados.getNomeCompleto());
            clienteExistente.setCodigoSimplesVet(novosDados.getCodigoSimplesVet());
            clienteExistente.setCpf(novosDados.getCpf());
            clienteExistente.setTelefones(novosDados.getTelefones());
            clienteExistente.setEmails(novosDados.getEmails());
            clienteExistente.setEmails(novosDados.getEmails());

            return clienteRespository.save(clienteExistente);
        } else {
            throw new RuntimeException("AnimalServico não encontrado com id: " + id);
        }
    }
}
