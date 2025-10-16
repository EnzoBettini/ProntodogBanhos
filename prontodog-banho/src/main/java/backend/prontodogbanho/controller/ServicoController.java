package backend.prontodogbanho.controller;

import backend.prontodogbanho.model.Servico;
import backend.prontodogbanho.service.ServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/servico")
public class ServicoController {

    private ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public List<Servico> listarServicos() {
        return this.servicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        Optional<Servico> servicoAtualizado = this.servicoService.buscarPorId(id);
        return servicoAtualizado.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Servico> inserirServico(@RequestBody Servico servico) {
        Servico novoServico = this.servicoService.salvar(servico);
        return ResponseEntity.ok(novoServico);
    }

    @GetMapping("/simples")
    public List<Map<String, Object>> listarServicosSimples() {
        return this.servicoService.listarTodos()
            .stream()
            .map(servico -> {
                Map<String, Object> servicoSimples = new HashMap<>();
                servicoSimples.put("id", servico.getId());
                servicoSimples.put("nome", servico.getNome());
                servicoSimples.put("descricao", servico.getDescricao());
                servicoSimples.put("quantidade", servico.getQuantidade());
                servicoSimples.put("valor", servico.getValor());
                servicoSimples.put("podeSerAdicional", servico.getPodeSerAdicional());
                servicoSimples.put("categoria", servico.getCategoria());
                return servicoSimples;
            })
            .toList();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> deletarServico(@PathVariable Long id) {
        this.servicoService.deletar(id);
        return ResponseEntity.ok().build();
    }
}
