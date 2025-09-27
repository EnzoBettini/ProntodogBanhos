package backend.prontodogbanho.service;

import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.model.Cliente;
import backend.prontodogbanho.repository.AnimalRepository;
import backend.prontodogbanho.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    private final AnimalRepository animalRepository;

    public ClienteService(ClienteRepository clienteRepository, AnimalRepository animalRepository) {
        this.clienteRepository = clienteRepository;
        this.animalRepository = animalRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente salvar(Cliente cliente) {
        // Gera código do cliente se estiver nulo
        if (cliente.getCodigoClienteSistema() == null) {
            Long maxCodigo =  this.clienteRepository.findMaxCodigoClienteSistema();
            cliente.setCodigoClienteSistema(maxCodigo != null ? maxCodigo + 1 : 1L);
        }

        // Trata os animais
        if (cliente.getAnimais() != null) {
            for (Animal animal : cliente.getAnimais()) {
                if (animal.getCodigoAnimalSistema() == null) {
                    Long maxCodigoAnimal = this.animalRepository.findMaxCodigoAnimalSistema();
                    animal.setCodigoAnimalSistema(maxCodigoAnimal != null ? maxCodigoAnimal + 1 : 1L);
                }
                animal.setCliente(cliente);
            }
        }

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
