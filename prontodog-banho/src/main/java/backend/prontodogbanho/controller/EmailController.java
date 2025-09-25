package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.Email;
import backend.prontodogbanho.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public List<Email> listarTodos() {
        return this.emailService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Email> buscarPorId(@PathVariable Long id) {
        Optional<Email> email = this.emailService.buscarPorId(id);
        return email.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Email> criar(@RequestBody Email email) {
        Email novoEmail = this.emailService.salvar(email);
        return ResponseEntity.ok(novoEmail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        this.emailService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Email> atualizarEmail(@PathVariable Long id, @RequestBody Email novosDados) {
        Email emailAtualizado = this.emailService.atualizarEmail(id, novosDados);
        return ResponseEntity.ok(emailAtualizado);
    }
}
