package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.EmailCliente;
import backend.prontodogbanho.service.EmailClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/email")
public class EmailClienteController {

    private final EmailClienteService emailClienteService;

    public EmailClienteController(EmailClienteService emailClienteService) {
        this.emailClienteService = emailClienteService;
    }

    @GetMapping
    public List<EmailCliente> listarTodos() {
        return this.emailClienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailCliente> buscarPorId(@PathVariable Long id) {
        Optional<EmailCliente> email = this.emailClienteService.buscarPorId(id);
        return email.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EmailCliente> criar(@RequestBody EmailCliente emailCliente) {
        EmailCliente novoEmailCliente = this.emailClienteService.salvar(emailCliente);
        return ResponseEntity.ok(novoEmailCliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        this.emailClienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailCliente> atualizarEmail(@PathVariable Long id, @RequestBody EmailCliente novosDados) {
        EmailCliente emailClienteAtualizado = this.emailClienteService.atualizarEmail(id, novosDados);
        return ResponseEntity.ok(emailClienteAtualizado);
    }
}
