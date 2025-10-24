package backend.prontodogbanho.controller;

import backend.prontodogbanho.dto.RecebimentoPendenteDTO;
import backend.prontodogbanho.model.VendaBaixa;
import backend.prontodogbanho.repository.VendaBaixaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recebimentos")
public class RecebimentoController {

    private final VendaBaixaRepository vendaBaixaRepository;

    public RecebimentoController(VendaBaixaRepository vendaBaixaRepository) {
        this.vendaBaixaRepository = vendaBaixaRepository;
    }

    // GET /api/recebimentos/pendentes - Listar todos os recebimentos pendentes
    @GetMapping("/pendentes")
    public ResponseEntity<List<RecebimentoPendenteDTO>> listarPendentes() {
        List<VendaBaixa> baixas = vendaBaixaRepository.findAll().stream()
                .filter(b -> b.temMaquininha())
                .filter(b -> b.isRecebimentoPendente() || b.isAntecipado())
                .filter(b -> !b.jaRecebeu())
                .collect(Collectors.toList());

        List<RecebimentoPendenteDTO> dtos = baixas.stream()
                .map(RecebimentoPendenteDTO::fromEntity)
                .sorted((a, b) -> {
                    if (a.getDataPrevistaRecebimento() == null) return 1;
                    if (b.getDataPrevistaRecebimento() == null) return -1;
                    return a.getDataPrevistaRecebimento().compareTo(b.getDataPrevistaRecebimento());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // GET /api/recebimentos/proximos - Listar recebimentos dos próximos N dias
    @GetMapping("/proximos")
    public ResponseEntity<List<RecebimentoPendenteDTO>> listarProximos(
            @RequestParam(defaultValue = "7") int dias) {

        LocalDate dataLimite = LocalDate.now().plusDays(dias);

        List<VendaBaixa> baixas = vendaBaixaRepository.findAll().stream()
                .filter(b -> b.temMaquininha())
                .filter(b -> b.isRecebimentoPendente())
                .filter(b -> !b.jaRecebeu())
                .filter(b -> b.getDataPrevistaRecebimento() != null)
                .filter(b -> !b.getDataPrevistaRecebimento().isAfter(dataLimite))
                .collect(Collectors.toList());

        List<RecebimentoPendenteDTO> dtos = baixas.stream()
                .map(RecebimentoPendenteDTO::fromEntity)
                .sorted((a, b) -> a.getDataPrevistaRecebimento().compareTo(b.getDataPrevistaRecebimento()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // GET /api/recebimentos/atrasados - Listar recebimentos atrasados
    @GetMapping("/atrasados")
    public ResponseEntity<List<RecebimentoPendenteDTO>> listarAtrasados() {
        List<VendaBaixa> baixas = vendaBaixaRepository.findAll().stream()
                .filter(b -> b.temMaquininha())
                .filter(b -> b.isRecebimentoPendente())
                .filter(b -> !b.jaRecebeu())
                .filter(VendaBaixa::isAtrasado)
                .collect(Collectors.toList());

        List<RecebimentoPendenteDTO> dtos = baixas.stream()
                .map(RecebimentoPendenteDTO::fromEntity)
                .sorted((a, b) -> a.getDataPrevistaRecebimento().compareTo(b.getDataPrevistaRecebimento()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // POST /api/recebimentos/{baixaId}/marcar-recebido - Marcar como recebido
    @PostMapping("/{baixaId}/marcar-recebido")
    public ResponseEntity<VendaBaixa> marcarComoRecebido(
            @PathVariable Long baixaId,
            @RequestBody(required = false) Map<String, String> request) {

        return vendaBaixaRepository.findById(baixaId)
                .map(baixa -> {
                    LocalDate dataEfetiva = LocalDate.now();

                    // Se enviou data específica, usar ela
                    if (request != null && request.containsKey("dataEfetivaRecebimento")) {
                        dataEfetiva = LocalDate.parse(request.get("dataEfetivaRecebimento"));
                    }

                    baixa.setDataEfetivaRecebimento(dataEfetiva);
                    baixa.setStatusRecebimento("recebido");

                    VendaBaixa atualizada = vendaBaixaRepository.save(baixa);
                    return ResponseEntity.ok(atualizada);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/recebimentos/{baixaId}/marcar-antecipado - Marcar como antecipado
    @PostMapping("/{baixaId}/marcar-antecipado")
    public ResponseEntity<VendaBaixa> marcarComoAntecipado(
            @PathVariable Long baixaId,
            @RequestBody Map<String, String> request) {

        return vendaBaixaRepository.findById(baixaId)
                .map(baixa -> {
                    LocalDate dataEfetiva = LocalDate.now();

                    if (request != null && request.containsKey("dataEfetivaRecebimento")) {
                        dataEfetiva = LocalDate.parse(request.get("dataEfetivaRecebimento"));
                    }

                    baixa.setDataEfetivaRecebimento(dataEfetiva);
                    baixa.setStatusRecebimento("antecipado");

                    VendaBaixa atualizada = vendaBaixaRepository.save(baixa);
                    return ResponseEntity.ok(atualizada);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/recebimentos/resumo - Resumo do fluxo de caixa
    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Object>> obterResumo() {
        List<VendaBaixa> todas = vendaBaixaRepository.findAll().stream()
                .filter(VendaBaixa::temMaquininha)
                .filter(b -> !b.jaRecebeu())
                .collect(Collectors.toList());

        long totalPendentes = todas.stream()
                .filter(VendaBaixa::isRecebimentoPendente)
                .count();

        double valorTotalPendente = todas.stream()
                .filter(VendaBaixa::isRecebimentoPendente)
                .mapToDouble(b -> b.getValorLiquido().doubleValue())
                .sum();

        long atrasados = todas.stream()
                .filter(VendaBaixa::isAtrasado)
                .count();

        LocalDate hoje = LocalDate.now();
        long proximos7Dias = todas.stream()
                .filter(b -> b.getDataPrevistaRecebimento() != null)
                .filter(b -> !b.getDataPrevistaRecebimento().isAfter(hoje.plusDays(7)))
                .count();

        long proximos30Dias = todas.stream()
                .filter(b -> b.getDataPrevistaRecebimento() != null)
                .filter(b -> !b.getDataPrevistaRecebimento().isAfter(hoje.plusDays(30)))
                .count();

        Map<String, Object> resumo = Map.of(
                "totalPendentes", totalPendentes,
                "valorTotalPendente", valorTotalPendente,
                "atrasados", atrasados,
                "proximos7Dias", proximos7Dias,
                "proximos30Dias", proximos30Dias
        );

        return ResponseEntity.ok(resumo);
    }
}

