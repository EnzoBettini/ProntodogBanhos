package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.FormaPagamento;
import backend.prontodogbanho.service.FormaPagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formas-pagamento")
public class FormaPagamentoController {

    private final FormaPagamentoService formaPagamentoService;

    public FormaPagamentoController(FormaPagamentoService formaPagamentoService) {
        this.formaPagamentoService = formaPagamentoService;
    }

    // GET /api/formas-pagamento - Listar todas
    @GetMapping
    public ResponseEntity<List<FormaPagamento>> listarTodas() {
        return ResponseEntity.ok(formaPagamentoService.listarTodos());
    }

    // GET /api/formas-pagamento/ativas - Listar apenas ativas
    @GetMapping("/ativas")
    public ResponseEntity<List<FormaPagamento>> listarAtivas() {
        return ResponseEntity.ok(formaPagamentoService.listarAtivos());
    }

    // GET /api/formas-pagamento/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<FormaPagamento> buscarPorId(@PathVariable Long id) {
        return formaPagamentoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/formas-pagamento/tipo/{tipo} - Buscar por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<FormaPagamento>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(formaPagamentoService.listarPorTipo(tipo));
    }

    // GET /api/formas-pagamento/parcelamento - Listar formas com parcelamento
    @GetMapping("/parcelamento")
    public ResponseEntity<List<FormaPagamento>> listarComParcelamento() {
        return ResponseEntity.ok(formaPagamentoService.listarComParcelamento());
    }

    // POST /api/formas-pagamento - Criar nova
    @PostMapping
    public ResponseEntity<FormaPagamento> criar(@RequestBody @Valid FormaPagamento formaPagamento) {
        FormaPagamento novaForma = formaPagamentoService.salvar(formaPagamento);
        return ResponseEntity.ok(novaForma);
    }

    // PUT /api/formas-pagamento/{id} - Atualizar
    @PutMapping("/{id}")
    public ResponseEntity<FormaPagamento> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid FormaPagamento formaPagamento) {
        try {
            FormaPagamento formaAtualizada = formaPagamentoService.atualizar(id, formaPagamento);
            return ResponseEntity.ok(formaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/formas-pagamento/{id}/ativar - Ativar
    @PutMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        formaPagamentoService.ativar(id);
        return ResponseEntity.ok().build();
    }

    // PUT /api/formas-pagamento/{id}/desativar - Desativar
    @PutMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        formaPagamentoService.desativar(id);
        return ResponseEntity.ok().build();
    }

    // DELETE /api/formas-pagamento/{id} - Excluir
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        formaPagamentoService.excluir(id);
        return ResponseEntity.ok().build();
    }
}

