package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.Adquirente;
import backend.prontodogbanho.repository.AdquirenteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adquirentes")
public class AdquirenteController {

    private final AdquirenteRepository adquirenteRepository;

    public AdquirenteController(AdquirenteRepository adquirenteRepository) {
        this.adquirenteRepository = adquirenteRepository;
    }

    @GetMapping
    public ResponseEntity<List<Adquirente>> listarTodos() {
        return ResponseEntity.ok(adquirenteRepository.findAll());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Adquirente>> listarAtivos() {
        return ResponseEntity.ok(adquirenteRepository.findByAtivoTrueOrderByNomeAsc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Adquirente> buscarPorId(@PathVariable Long id) {
        return adquirenteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

