package backend.prontodogbanho.service;

import backend.prontodogbanho.model.Animal;
import backend.prontodogbanho.model.Cliente;
import backend.prontodogbanho.model.EmailCliente;
import backend.prontodogbanho.model.Telefone;
import backend.prontodogbanho.repository.AnimalRepository;
import backend.prontodogbanho.repository.ClienteRepository;
import backend.prontodogbanho.repository.EmailClienteRepository;
import backend.prontodogbanho.repository.TelefoneRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    private final AnimalRepository animalRepository;

    private final EmailClienteRepository emailClienteRepository;

    private final TelefoneRepository telefoneRepository;

    public ClienteService(ClienteRepository clienteRepository, AnimalRepository animalRepository, EmailClienteRepository emailClienteRepository, TelefoneRepository telefoneRepository) {
        this.clienteRepository = clienteRepository;
        this.animalRepository = animalRepository;
        this.emailClienteRepository = emailClienteRepository;
        this.telefoneRepository = telefoneRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        if (cliente.getCodigoClienteSistema() == null) {
            Long maxCodigo =  this.clienteRepository.findMaxCodigoClienteSistema();
            cliente.setCodigoClienteSistema(maxCodigo != null ? maxCodigo + 1 : 1L);
        }

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

    @Transactional
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
            // Remove TODOS os telefones existentes usando query customizada
            telefoneRepository.deleteAllByClienteId(clienteExistente.getId());

            // Limpa a lista do cliente para sincronizar o estado
            if (clienteExistente.getTelefones() != null) {
                clienteExistente.getTelefones().clear();
            } else {
                clienteExistente.setTelefones(new ArrayList<>());
            }

            // Adiciona os novos telefones estabelecendo a relação bidirecional
            for (Telefone novoTelefone : novosDados.getTelefones()) {
                // Cria um novo objeto para evitar problemas de referência
                Telefone telefoneParaSalvar = new Telefone();
                telefoneParaSalvar.setTelefone(novoTelefone.getTelefone());
                telefoneParaSalvar.setCliente(clienteExistente);
                clienteExistente.getTelefones().add(telefoneParaSalvar);
            }
        }

        if (novosDados.getEmailClientes() != null) {
            // Remove TODOS os emails existentes usando query customizada
            emailClienteRepository.deleteAllByClienteId(clienteExistente.getId());

            // Limpa a lista do cliente para sincronizar o estado
            if (clienteExistente.getEmailClientes() != null) {
                clienteExistente.getEmailClientes().clear();
            } else {
                clienteExistente.setEmailClientes(new ArrayList<>());
            }

            // Adiciona os novos emails estabelecendo a relação bidirecional
            for (EmailCliente novoEmail : novosDados.getEmailClientes()) {
                // Cria um novo objeto para evitar problemas de referência
                EmailCliente emailParaSalvar = new EmailCliente();
                emailParaSalvar.setEmail(novoEmail.getEmail());
                emailParaSalvar.setCliente(clienteExistente);
                clienteExistente.getEmailClientes().add(emailParaSalvar);
            }
        }

        return this.clienteRepository.save(clienteExistente);
    }

    public List<EmailCliente> atualizarEmail(Long idCliente, EmailCliente novosEmailCliente) {
        Cliente cliente = this.clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + idCliente));

        if(novosEmailCliente.getEmail() == null || novosEmailCliente.getEmail().isEmpty()) {
            throw new RuntimeException("Não pode atualizar um email vazio");
        }

        List<EmailCliente> emailClientes = this.emailClienteRepository.findEmailsByCliente(idCliente);

        if (emailClientes.isEmpty()) {
            novosEmailCliente.setCliente(cliente);
            emailClientes.add(this.emailClienteRepository.save(novosEmailCliente));
        } else {
            EmailCliente emailClienteExistente = emailClientes.getFirst();
            emailClienteExistente.setEmail(novosEmailCliente.getEmail());
            emailClientes.set(0, this.emailClienteRepository.save(emailClienteExistente));
        }
        return emailClientes;
    }

    @Transactional
    public Telefone atualizarTelefone(Long idCliente, String numeroAntigo, String numeroNovo) {
        Cliente cliente = this.clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + idCliente));

        Telefone telefoneExistente = this.telefoneRepository.findTelefonesByCliente(idCliente, numeroAntigo);

        if (telefoneExistente == null) {
            throw new RuntimeException("Telefone não encontrado para este cliente");
        }

        if (!telefoneExistente.getCliente().getId().equals(cliente.getId())) {
            throw new RuntimeException("Telefone não pertence a este cliente");
        }

        if (numeroNovo == null || !numeroNovo.matches("\\d{10,11}")) {
            throw new RuntimeException("Telefone inválido, deve ter 10 ou 11 dígitos");
        }

        telefoneExistente.setTelefone(numeroNovo);
        telefoneExistente = this.telefoneRepository.save(telefoneExistente);

        return telefoneExistente;
    }
}
