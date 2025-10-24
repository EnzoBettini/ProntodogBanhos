package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.ContaBancaria;
import backend.prontodogbanho.repository.ContaBancariaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas-bancarias")
public class ContaBancariaController {

    private final ContaBancariaRepository contaBancariaRepository;

    public ContaBancariaController(ContaBancariaRepository contaBancariaRepository) {
        this.contaBancariaRepository = contaBancariaRepository;
    }

    @GetMapping
    public ResponseEntity<List<ContaBancaria>> listarTodas() {
        return ResponseEntity.ok(contaBancariaRepository.findAll());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<ContaBancaria>> listarAtivas() {
        return ResponseEntity.ok(contaBancariaRepository.findByAtivoTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaBancaria> buscarPorId(@PathVariable Long id) {
        return contaBancariaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContaBancaria> criar(@RequestBody @Valid ContaBancaria conta) {
        ContaBancaria novaConta = contaBancariaRepository.save(conta);
        return ResponseEntity.ok(novaConta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaBancaria> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ContaBancaria contaAtualizada) {
        return contaBancariaRepository.findById(id)
                .map(conta -> {
                    conta.setNome(contaAtualizada.getNome());
                    conta.setBanco(contaAtualizada.getBanco());
                    conta.setAgencia(contaAtualizada.getAgencia());
                    conta.setConta(contaAtualizada.getConta());
                    conta.setTipo(contaAtualizada.getTipo());
                    conta.setPixChave(contaAtualizada.getPixChave());
                    conta.setAtivo(contaAtualizada.getAtivo());
                    return ResponseEntity.ok(contaBancariaRepository.save(conta));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        return contaBancariaRepository.findById(id)
                .map(conta -> {
                    conta.setAtivo(false);
                    contaBancariaRepository.save(conta);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

