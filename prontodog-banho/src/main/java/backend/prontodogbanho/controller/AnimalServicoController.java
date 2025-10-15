package backend.prontodogbanho.controller;

import backend.prontodogbanho.dto.CriarAnimalServicoDTO;
import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.service.AnimalServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/animalservico")
public class AnimalServicoController {

    private final AnimalServicoService animalServicoService;

    public AnimalServicoController(AnimalServicoService animalServicoService) {
        this.animalServicoService = animalServicoService;
    }

    @GetMapping
    public List<AnimalServico> listarAnimalServicos() {
        return animalServicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalServico> buscaPorId(@PathVariable Long id) {
        Optional<AnimalServico> animalServicoOptional = this.animalServicoService.buscarPorId(id);
        return animalServicoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AnimalServico> adicionarServico(@RequestBody AnimalServico animalServico) {
        AnimalServico novoAnimalServico= this.animalServicoService.salvar(animalServico);
        return ResponseEntity.ok(novoAnimalServico);
    }

    @GetMapping("/teste-completo")
    public ResponseEntity<String> testeEndpointCompleto() {
        System.out.println("🧪 Endpoint /teste-completo chamado com sucesso!");
        return ResponseEntity.ok("Endpoint para criar animal serviço completo está funcionando! ✅");
    }

    @PostMapping("/criar-completo")
    public ResponseEntity<?> criarAnimalServicoCompleto(@RequestBody CriarAnimalServicoDTO dto) {
        try {
            System.out.println("🔄 Recebendo requisição para criar animal serviço completo: " + dto);

            AnimalServico novoAnimalServico = this.animalServicoService.criarComBanhosIndividuais(dto);

            System.out.println("✅ Animal serviço completo criado com sucesso: " + novoAnimalServico.getId());

            return ResponseEntity.ok(novoAnimalServico);
        } catch (RuntimeException e) {
            System.err.println("❌ Erro ao criar animal serviço completo: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.badRequest()
                .body("Erro ao criar animal serviço: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado ao criar animal serviço completo: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.internalServerError()
                .body("Erro interno do servidor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerSerivco(@PathVariable Long id) {
        this.animalServicoService.deletar(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalServico> atualizarServico(@PathVariable Long id, @RequestBody AnimalServico animalServico) {
        AnimalServico servicoAtualizado = this.animalServicoService.atualizarTudo(id, animalServico);
        return ResponseEntity.ok(servicoAtualizado);
    }
}
