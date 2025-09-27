package backend.prontodogbanho.service;

import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.model.Cliente;
import backend.prontodogbanho.model.EmailCliente;
import backend.prontodogbanho.repository.AnimalRepository;
import backend.prontodogbanho.repository.ClienteRepository;
import backend.prontodogbanho.repository.EmailClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    private final AnimalRepository animalRepository;

    private final EmailClienteRepository emailClienteRepository;

    public ClienteService(ClienteRepository clienteRepository, AnimalRepository animalRepository,  EmailClienteRepository emailClienteRepository) {
        this.clienteRepository = clienteRepository;
        this.animalRepository = animalRepository;
        this.emailClienteRepository = emailClienteRepository;
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

    public Cliente atualizarDados(Long id, Cliente novosDados) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + id));

        if (novosDados.getNomeCompleto() != null) {
            clienteExistente.setNomeCompleto(novosDados.getNomeCompleto());
        }

        if (novosDados.getCodigoSimplesVet() != null) {
            clienteExistente.setCodigoSimplesVet(novosDados.getCodigoSimplesVet());
        }

        if (novosDados.getCpf() != null) {
            clienteExistente.setCpf(novosDados.getCpf());
        }

        if (novosDados.getTelefones() != null) {
            clienteExistente.setTelefones(novosDados.getTelefones());
        }

        if (novosDados.getEmailClientes() != null) {
            clienteExistente.setEmailClientes(novosDados.getEmailClientes());
        }

        return this.clienteRepository.save(clienteExistente);
    }

    public List<EmailCliente> atualizarEmail(Long idCliente, EmailCliente novosEmailCliente) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + idCliente));

        if(novosEmailCliente.getEmail() == null || novosEmailCliente.getEmail().isEmpty()) {
            throw new RuntimeException("Não pode atualizar um email vazio");
        }

        List<EmailCliente> emailClientes = emailClienteRepository.findEmailsByCliente(idCliente);

        if (emailClientes.isEmpty()) {
            novosEmailCliente.setCliente(cliente);
            emailClientes.add(emailClienteRepository.save(novosEmailCliente));
        } else {
            EmailCliente emailClienteExistente = emailClientes.getFirst();
            emailClienteExistente.setEmail(novosEmailCliente.getEmail());
            emailClientes.set(0, emailClienteRepository.save(emailClienteExistente));
        }

        return emailClientes;
    }


}
