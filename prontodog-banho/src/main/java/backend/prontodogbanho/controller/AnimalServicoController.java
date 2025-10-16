package backend.prontodogbanho.controller;

import backend.prontodogbanho.dto.CriarAnimalServicoDTO;
import backend.prontodogbanho.model.AnimalServico;
import backend.prontodogbanho.service.AnimalServicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<AnimalServico> atualizarServico(@PathVariable Long id, @RequestBody Map<String, Object> dadosAtualizacao) {
        System.out.println("🔍 CONTROLLER - Dados recebidos: " + dadosAtualizacao);

        AnimalServico animalServico = new AnimalServico();

        // Processar campos diretos
        if (dadosAtualizacao.containsKey("dataServico")) {
            animalServico.setDataServico(LocalDate.parse((String) dadosAtualizacao.get("dataServico")));
        }
        if (dadosAtualizacao.containsKey("dataExpiracao") && dadosAtualizacao.get("dataExpiracao") != null) {
            animalServico.setDataExpiracao(LocalDate.parse((String) dadosAtualizacao.get("dataExpiracao")));
        }
        if (dadosAtualizacao.containsKey("statusPagamento")) {
            animalServico.setStatusPagamento((String) dadosAtualizacao.get("statusPagamento"));
        }
        if (dadosAtualizacao.containsKey("dataPagamento") && dadosAtualizacao.get("dataPagamento") != null) {
            animalServico.setDataPagamento(LocalDate.parse((String) dadosAtualizacao.get("dataPagamento")));
        }

        // Processar IDs dos relacionamentos
        Long servicoId = null;
        Long usuarioId = null;

        if (dadosAtualizacao.containsKey("servicoId") && dadosAtualizacao.get("servicoId") != null) {
            Object servicoIdObj = dadosAtualizacao.get("servicoId");
            servicoId = servicoIdObj instanceof Integer ? ((Integer) servicoIdObj).longValue() : (Long) servicoIdObj;
            System.out.println("🔍 CONTROLLER - Serviço ID extraído: " + servicoId);
        }

        if (dadosAtualizacao.containsKey("usuarioId") && dadosAtualizacao.get("usuarioId") != null) {
            Object usuarioIdObj = dadosAtualizacao.get("usuarioId");
            usuarioId = usuarioIdObj instanceof Integer ? ((Integer) usuarioIdObj).longValue() : (Long) usuarioIdObj;
            System.out.println("🔍 CONTROLLER - Usuário ID extraído: " + usuarioId);
        }

        AnimalServico servicoAtualizado = this.animalServicoService.atualizarComIDs(id, animalServico, servicoId, usuarioId);
        return ResponseEntity.ok(servicoAtualizado);
    }

    // Endpoints para controle de expiração de pacotes

    /**
     * Busca pacotes que vão vencer em X dias
     * GET /animalservico/expiracao/vao-vencer?dias=7
     */
    @GetMapping("/expiracao/vao-vencer")
    public ResponseEntity<List<AnimalServico>> buscarPacotesQueVaoVencer(
            @RequestParam(defaultValue = "7") int dias) {
        List<AnimalServico> pacotes = animalServicoService.buscarPacotesQueVaoVencer(dias);
        return ResponseEntity.ok(pacotes);
    }

    /**
     * Busca pacotes já vencidos
     * GET /animalservico/expiracao/vencidos
     */
    @GetMapping("/expiracao/vencidos")
    public ResponseEntity<List<AnimalServico>> buscarPacotesVencidos() {
        List<AnimalServico> pacotes = animalServicoService.buscarPacotesVencidos();
        return ResponseEntity.ok(pacotes);
    }

    /**
     * Busca pacotes válidos (não vencidos ou sem data de expiração)
     * GET /animalservico/expiracao/validos
     */
    @GetMapping("/expiracao/validos")
    public ResponseEntity<List<AnimalServico>> buscarPacotesValidos() {
        List<AnimalServico> pacotes = animalServicoService.buscarPacotesValidos();
        return ResponseEntity.ok(pacotes);
    }

    /**
     * Busca pacotes que expiram hoje
     * GET /animalservico/expiracao/hoje
     */
    @GetMapping("/expiracao/hoje")
    public ResponseEntity<List<AnimalServico>> buscarPacotesQueExpiramHoje() {
        List<AnimalServico> pacotes = animalServicoService.buscarPacotesQueExpiramHoje();
        return ResponseEntity.ok(pacotes);
    }

    // Endpoints para controle de pagamento

    /**
     * Busca serviços por status de pagamento
     * GET /animalservico/pagamento/status?status=pago
     */
    @GetMapping("/pagamento/status")
    public ResponseEntity<List<AnimalServico>> buscarPorStatusPagamento(
            @RequestParam String status) {
        try {
            List<AnimalServico> servicos = animalServicoService.buscarPorStatusPagamento(status);
            return ResponseEntity.ok(servicos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Busca serviços em aberto (não pagos)
     * GET /animalservico/pagamento/em-aberto
     */
    @GetMapping("/pagamento/em-aberto")
    public ResponseEntity<List<AnimalServico>> buscarServicosEmAberto() {
        List<AnimalServico> servicos = animalServicoService.buscarServicosEmAberto();
        return ResponseEntity.ok(servicos);
    }

    /**
     * Busca serviços pagos
     * GET /animalservico/pagamento/pagos
     */
    @GetMapping("/pagamento/pagos")
    public ResponseEntity<List<AnimalServico>> buscarServicosPagos() {
        List<AnimalServico> servicos = animalServicoService.buscarServicosPagos();
        return ResponseEntity.ok(servicos);
    }

    /**
     * Busca serviços cancelados
     * GET /animalservico/pagamento/cancelados
     */
    @GetMapping("/pagamento/cancelados")
    public ResponseEntity<List<AnimalServico>> buscarServicosCancelados() {
        List<AnimalServico> servicos = animalServicoService.buscarServicosCancelados();
        return ResponseEntity.ok(servicos);
    }

    /**
     * Marca um serviço como pago
     * PUT /animalservico/{id}/marcar-pago?dataPagamento=2024-01-15
     */
    @PutMapping("/{id}/marcar-pago")
    public ResponseEntity<?> marcarComoPago(
            @PathVariable Long id,
            @RequestParam(required = false) String dataPagamento) {
        try {
            LocalDate dataPag = null;
            if (dataPagamento != null && !dataPagamento.isEmpty()) {
                dataPag = LocalDate.parse(dataPagamento);
            }

            AnimalServico servicoAtualizado = animalServicoService.marcarComoPago(id, dataPag);
            return ResponseEntity.ok(servicoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body("Erro ao marcar como pago: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Erro interno: " + e.getMessage());
        }
    }

    /**
     * Marca um serviço como cancelado
     * PUT /animalservico/{id}/marcar-cancelado
     */
    @PutMapping("/{id}/marcar-cancelado")
    public ResponseEntity<?> marcarComoCancelado(@PathVariable Long id) {
        try {
            AnimalServico servicoAtualizado = animalServicoService.marcarComoCancelado(id);
            return ResponseEntity.ok(servicoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body("Erro ao marcar como cancelado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Erro interno: " + e.getMessage());
        }
    }

    /**
     * Reativa um serviço (volta para em_aberto)
     * PUT /animalservico/{id}/reativar
     */
    @PutMapping("/{id}/reativar")
    public ResponseEntity<?> reativarServico(@PathVariable Long id) {
        try {
            AnimalServico servicoAtualizado = animalServicoService.reativarServico(id);
            return ResponseEntity.ok(servicoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body("Erro ao reativar serviço: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Erro interno: " + e.getMessage());
        }
    }

    /**
     * Busca serviços pagos em um período específico
     * GET /animalservico/pagamento/pagos-periodo?dataInicio=2024-01-01&dataFim=2024-01-31
     */
    @GetMapping("/pagamento/pagos-periodo")
    public ResponseEntity<?> buscarServicosPagosPorPeriodo(
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {
        try {
            LocalDate inicio = LocalDate.parse(dataInicio);
            LocalDate fim = LocalDate.parse(dataFim);

            List<AnimalServico> servicos = animalServicoService.buscarServicosPagosPorPeriodo(inicio, fim);
            return ResponseEntity.ok(servicos);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Erro na consulta por período: " + e.getMessage());
        }
    }
}
