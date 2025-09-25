package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.Telefone;
import backend.prontodogbanho.repository.TelefoneRepository;
import backend.prontodogbanho.service.TelefoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/telefone")
public class TelefoneController {

    private TelefoneService telefoneService;

    public TelefoneController(TelefoneService telefoneService) {
        this.telefoneService = telefoneService;
    }

    @GetMapping
    public List<Telefone> listar() {
        return this.telefoneService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Telefone> buscarPorId(@PathVariable Long id) {
        Optional<Telefone> telefone = this.telefoneService.buscarPorId(id);
        return telefone.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Telefone> salvar(@RequestBody Telefone telefone) {
        Telefone novoTelefone = this.telefoneService.salvar(telefone);
        return ResponseEntity.ok(novoTelefone);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        this.telefoneService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
