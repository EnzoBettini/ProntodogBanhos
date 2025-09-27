package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.Cliente;
import backend.prontodogbanho.model.EmailCliente;
import backend.prontodogbanho.model.Telefone;
import backend.prontodogbanho.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<Cliente> listarClientes(){
        return this.clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        Optional<Cliente> clienteOptional = this.clienteService.buscarPorId(id);
        return clienteOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Cliente> adicionarCliente(@RequestBody @Valid Cliente novoCliente) {
        Cliente novoClienteResponse = this.clienteService.salvar(novoCliente);
        return ResponseEntity.ok(novoClienteResponse);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        this.clienteService.deletar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable Long id, @RequestBody Cliente novosDadosCliente) {
        Cliente novoCliente = this.clienteService.atualizarDados(id, novosDadosCliente);
        return ResponseEntity.ok(novoCliente);
    }

    @PutMapping("/atualizaremail/{id}")
    public ResponseEntity<List<EmailCliente>> atualizarEmail(@PathVariable Long id,@Valid @RequestBody EmailCliente novosDadosEmailCliente) {
        List<EmailCliente> emailsAtualizados = this.clienteService.atualizarEmail(id, novosDadosEmailCliente);
        return ResponseEntity.ok(emailsAtualizados);
    }

    @PutMapping("/atualizartelefone/{idCliente}/{numeroAntigo}/{numeroNovo}")
    public ResponseEntity<Telefone> atualizarTelefone(@PathVariable Long idCliente,@PathVariable String numeroAntigo,@PathVariable String numeroNovo) {
        Telefone telefoneAtualizado = this.clienteService.atualizarTelefone(idCliente, numeroAntigo, numeroNovo);
        return ResponseEntity.ok(telefoneAtualizado);
    }
}
