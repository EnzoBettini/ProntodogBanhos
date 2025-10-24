package backend.prontodogbanho.controller;

import backend.prontodogbanho.dto.*;
import backend.prontodogbanho.model.Maquininha;
import backend.prontodogbanho.model.MaquininhaTaxa;
import backend.prontodogbanho.service.MaquininhaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maquininhas")
public class MaquininhaController {

    private final MaquininhaService maquininhaService;

    public MaquininhaController(MaquininhaService maquininhaService) {
        this.maquininhaService = maquininhaService;
    }

    // ========== ENDPOINTS PRINCIPAIS ==========

    // GET /api/maquininhas - Listar todas
    @GetMapping
    public ResponseEntity<List<Maquininha>> listarTodas() {
        return ResponseEntity.ok(maquininhaService.listarTodas());
    }

    // GET /api/maquininhas/ativas - Listar apenas ativas
    @GetMapping("/ativas")
    public ResponseEntity<List<Maquininha>> listarAtivas() {
        return ResponseEntity.ok(maquininhaService.listarAtivas());
    }

    // GET /api/maquininhas/ativas/resumo - Listar ativas com resumo (para dropdowns)
    @GetMapping("/ativas/resumo")
    public ResponseEntity<List<MaquininhaResumoDTO>> listarAtivasResumo() {
        return ResponseEntity.ok(maquininhaService.listarAtivasResumo());
    }

    // GET /api/maquininhas/{id} - Buscar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Maquininha> buscarPorId(@PathVariable Long id) {
        try {
            Maquininha maquininha = maquininhaService.buscarPorId(id);
            return ResponseEntity.ok(maquininha);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/maquininhas/{id}/completo - Buscar por ID com detalhes completos
    @GetMapping("/{id}/completo")
    public ResponseEntity<MaquininhaCompletoDTO> buscarPorIdCompleto(@PathVariable Long id) {
        try {
            MaquininhaCompletoDTO dto = maquininhaService.buscarPorIdCompleto(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/maquininhas - Criar nova maquininha (wizard completo)
    @PostMapping
    public ResponseEntity<Maquininha> criar(@RequestBody @Valid CriarMaquininhaDTO dto) {
        try {
            Maquininha maquininha = maquininhaService.criar(dto);
            return ResponseEntity.ok(maquininha);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/maquininhas/{id} - Atualizar maquininha
    @PutMapping("/{id}")
    public ResponseEntity<Maquininha> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarMaquininhaDTO dto) {
        try {
            Maquininha maquininha = maquininhaService.atualizar(id, dto);
            return ResponseEntity.ok(maquininha);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/maquininhas/{id} - Excluir (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            maquininhaService.excluir(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/maquininhas/{id}/ativar - Reativar
    @PutMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        try {
            maquininhaService.ativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ENDPOINTS DE TAXAS ==========

    // POST /api/maquininhas/{id}/taxas - Adicionar taxa
    @PostMapping("/{id}/taxas")
    public ResponseEntity<MaquininhaTaxa> adicionarTaxa(
            @PathVariable Long id,
            @RequestBody @Valid CriarMaquininhaDTO.TaxaMaquininhaDTO taxaDto) {
        try {
            MaquininhaTaxa taxa = maquininhaService.adicionarTaxa(id, taxaDto);
            return ResponseEntity.ok(taxa);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/maquininhas/{id}/taxas - Listar todas as taxas de uma maquininha
    @GetMapping("/{id}/taxas")
    public ResponseEntity<List<MaquininhaTaxa>> listarTaxas(@PathVariable Long id) {
        try {
            Maquininha maquininha = maquininhaService.buscarPorId(id);
            return ResponseEntity.ok(maquininha.getTaxas());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ENDPOINTS DE CÁLCULO ==========

    // POST /api/maquininhas/{id}/calcular-taxa - Calcular taxa de uma transação
    @PostMapping("/{id}/calcular-taxa")
    public ResponseEntity<Map<String, Object>> calcularTaxa(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            Long bandeiraId = Long.valueOf(request.get("bandeiraId").toString());
            String tipoTransacao = request.get("tipoTransacao").toString();
            Integer numeroParcelas = request.get("numeroParcelas") != null
                ? Integer.valueOf(request.get("numeroParcelas").toString())
                : null;
            BigDecimal valor = new BigDecimal(request.get("valor").toString());

            BigDecimal taxaCalculada = maquininhaService.calcularTaxa(
                id,
                bandeiraId,
                tipoTransacao,
                numeroParcelas,
                valor
            );

            BigDecimal valorLiquido = valor.subtract(taxaCalculada);

            Map<String, Object> response = Map.of(
                "valor", valor,
                "taxa", taxaCalculada,
                "valorLiquido", valorLiquido
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST /api/maquininhas/{id}/calcular-data-recebimento - Calcular data de recebimento
    @PostMapping("/{id}/calcular-data-recebimento")
    public ResponseEntity<Map<String, Object>> calcularDataRecebimento(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            String tipoTransacao = request.get("tipoTransacao").toString();
            LocalDate dataTransacao = request.get("dataTransacao") != null
                ? LocalDate.parse(request.get("dataTransacao").toString())
                : LocalDate.now();

            LocalDate dataRecebimento = maquininhaService.calcularDataRecebimento(
                id,
                tipoTransacao,
                dataTransacao
            );

            Map<String, Object> response = Map.of(
                "dataTransacao", dataTransacao,
                "dataRecebimento", dataRecebimento,
                "diasAteRecebimento", java.time.temporal.ChronoUnit.DAYS.between(dataTransacao, dataRecebimento)
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ========== ENDPOINT DE BUSCA DE TAXA ESPECÍFICA ==========

    // GET /api/maquininhas/{id}/taxa - Buscar taxa específica
    @GetMapping("/{id}/taxa")
    public ResponseEntity<MaquininhaTaxa> buscarTaxa(
            @PathVariable Long id,
            @RequestParam Long bandeiraId,
            @RequestParam String tipoTransacao,
            @RequestParam(required = false) Integer numeroParcelas) {

        MaquininhaTaxa taxa = maquininhaService.buscarTaxa(
            id,
            bandeiraId,
            tipoTransacao,
            numeroParcelas
        );

        if (taxa != null) {
            return ResponseEntity.ok(taxa);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ENDPOINT DE CÁLCULO DE VALOR COM TAXA ==========

    // GET /api/maquininhas/{id}/calcular-valor-final
    // Calcula o valor que o cliente deve pagar (valor original + taxa)
    @GetMapping("/{id}/calcular-valor-final")
    public ResponseEntity<Map<String, Object>> calcularValorFinal(
            @PathVariable Long id,
            @RequestParam Long bandeiraId,
            @RequestParam String tipoTransacao,
            @RequestParam(required = false) Integer numeroParcelas,
            @RequestParam BigDecimal valorOriginal) {

        try {
            Map<String, Object> calculo = maquininhaService.calcularValorComTaxa(
                id,
                bandeiraId,
                tipoTransacao,
                numeroParcelas,
                valorOriginal
            );

            return ResponseEntity.ok(calculo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("error", e.getMessage())
            );
        }
    }
}

