package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.Bandeira;
import backend.prontodogbanho.repository.BandeiraRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bandeiras")
public class BandeiraController {

    private final BandeiraRepository bandeiraRepository;

    public BandeiraController(BandeiraRepository bandeiraRepository) {
        this.bandeiraRepository = bandeiraRepository;
    }

    @GetMapping
    public ResponseEntity<List<Bandeira>> listarTodas() {
        return ResponseEntity.ok(bandeiraRepository.findAll());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<Bandeira>> listarAtivas() {
        return ResponseEntity.ok(bandeiraRepository.findByAtivoTrueOrderByNomeAsc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bandeira> buscarPorId(@PathVariable Long id) {
        return bandeiraRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

