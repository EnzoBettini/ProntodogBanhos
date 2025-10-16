package backend.prontodogbanho.controller;

import backend.prontodogbanho.dto.CriarServicoAdicionalDTO;
import backend.prontodogbanho.dto.ServicoAdicionalCompletoDTO;
import backend.prontodogbanho.service.ServicoAdicionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/servicos-adicionais")
@CrossOrigin(origins = "*") // Para permitir CORS do frontend
public class ServicoAdicionalController {

    @Autowired
    private ServicoAdicionalService servicoAdicionalService;

    /**
     * Criar um novo serviço adicional
     */
    @PostMapping
    public ResponseEntity<?> criarServicoAdicional(@Valid @RequestBody CriarServicoAdicionalDTO dto) {
        try {
            ServicoAdicionalCompletoDTO resultado = servicoAdicionalService.criarServicoAdicional(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao criar serviço adicional: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor: " + e.getMessage());
        }
    }

    /**
     * Listar todos os serviços adicionais
     */
    @GetMapping
    public ResponseEntity<List<ServicoAdicionalCompletoDTO>> listarTodos() {
        try {
            List<ServicoAdicionalCompletoDTO> servicosAdicionais = servicoAdicionalService.listarTodos();
            return ResponseEntity.ok(servicosAdicionais);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Buscar serviço adicional por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            ServicoAdicionalCompletoDTO servicoAdicional = servicoAdicionalService.buscarPorId(id);
            return ResponseEntity.ok(servicoAdicional);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor: " + e.getMessage());
        }
    }

    /**
     * Listar serviços adicionais de um animal serviço específico
     */
    @GetMapping("/animal-servico/{animalServicoId}")
    public ResponseEntity<List<ServicoAdicionalCompletoDTO>> listarPorAnimalServico(@PathVariable Long animalServicoId) {
        try {
            List<ServicoAdicionalCompletoDTO> servicosAdicionais =
                servicoAdicionalService.listarPorAnimalServico(animalServicoId);
            return ResponseEntity.ok(servicosAdicionais);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Atualizar status de pagamento de um serviço adicional
     */
    @PutMapping("/{id}/status-pagamento")
    public ResponseEntity<?> atualizarStatusPagamento(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String dataPagamento) {
        try {
            System.out.println("🔍 DEBUG: Recebendo requisição para atualizar status de pagamento");
            System.out.println("  - ID: " + id);
            System.out.println("  - Status: " + status);
            System.out.println("  - Data Pagamento (string): " + dataPagamento);

            LocalDateTime data = null;
            if (dataPagamento != null && !dataPagamento.isEmpty()) {
                try {
                    data = LocalDateTime.parse(dataPagamento);
                    System.out.println("  - Data Pagamento (parsed): " + data);
                } catch (Exception e) {
                    System.err.println("❌ Erro ao parsear data: " + dataPagamento);
                    System.err.println("  - Erro: " + e.getMessage());
                    throw new RuntimeException("Formato de data inválido: " + dataPagamento);
                }
            }

            System.out.println("📞 Chamando service para atualizar status...");
            ServicoAdicionalCompletoDTO resultado =
                servicoAdicionalService.atualizarStatusPagamento(id, status, data);

            System.out.println("✅ Status atualizado com sucesso!");
            System.out.println("  - Resultado: " + resultado);

            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            System.err.println("❌ Erro runtime: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro ao atualizar status: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor: " + e.getMessage());
        }
    }

    /**
     * Remover serviço adicional
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerServicoAdicional(@PathVariable Long id) {
        try {
            servicoAdicionalService.removerServicoAdicional(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor: " + e.getMessage());
        }
    }

    /**
     * Calcular valor total dos serviços adicionais de um animal serviço
     */
    @GetMapping("/animal-servico/{animalServicoId}/valor-total")
    public ResponseEntity<?> calcularValorTotal(@PathVariable Long animalServicoId) {
        try {
            BigDecimal valorTotal = servicoAdicionalService.calcularValorTotalAdicionais(animalServicoId);
            return ResponseEntity.ok(valorTotal);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor: " + e.getMessage());
        }
    }

    /**
     * Contar serviços adicionais por status
     */
    @GetMapping("/contar/{status}")
    public ResponseEntity<?> contarPorStatus(@PathVariable String status) {
        try {
            Long count = servicoAdicionalService.contarPorStatus(status);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint para criar múltiplos serviços adicionais de uma vez
     * (útil para quando vem do cadastro de animal serviço)
     */
    @PostMapping("/batch")
    public ResponseEntity<?> criarMultiplosServicosAdicionais(@Valid @RequestBody List<CriarServicoAdicionalDTO> dtos) {
        try {
            List<ServicoAdicionalCompletoDTO> resultados = dtos.stream()
                    .map(dto -> servicoAdicionalService.criarServicoAdicional(dto))
                    .toList();

            return ResponseEntity.status(HttpStatus.CREATED).body(resultados);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao criar serviços adicionais: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor: " + e.getMessage());
        }
    }
}
