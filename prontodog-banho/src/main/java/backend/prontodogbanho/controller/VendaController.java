package backend.prontodogbanho.controller;

import backend.prontodogbanho.dto.*;
import backend.prontodogbanho.service.VendaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    // ============================================
    // LISTAGEM
    // ============================================

    // GET /api/vendas - Listar todas as vendas (resumo)
    @GetMapping
    public ResponseEntity<List<VendaResumoDTO>> listarTodas() {
        return ResponseEntity.ok(vendaService.listarTodas());
    }

    // GET /api/vendas/{id} - Buscar venda completa por ID
    @GetMapping("/{id}")
    public ResponseEntity<VendaCompletoDTO> buscarPorId(@PathVariable Long id) {
        return vendaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/vendas/codigo/{codigoVenda} - Buscar por código
    @GetMapping("/codigo/{codigoVenda}")
    public ResponseEntity<VendaCompletoDTO> buscarPorCodigo(@PathVariable Long codigoVenda) {
        return vendaService.buscarPorCodigo(codigoVenda)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/vendas/cliente/{clienteId} - Listar vendas de um cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VendaResumoDTO>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(vendaService.listarPorCliente(clienteId));
    }

    // GET /api/vendas/status/{status} - Listar vendas por status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<VendaResumoDTO>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(vendaService.listarPorStatus(status));
    }

    // ============================================
    // CRIAR E ATUALIZAR VENDA
    // ============================================

    // POST /api/vendas - Criar nova venda
    @PostMapping
    public ResponseEntity<VendaCompletoDTO> criarVenda(@RequestBody @Valid CriarVendaDTO dto) {
        try {
            VendaCompletoDTO venda = vendaService.criarVenda(dto);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /api/vendas/{id} - Atualizar venda
    @PutMapping("/{id}")
    public ResponseEntity<VendaCompletoDTO> atualizarVenda(
            @PathVariable Long id,
            @RequestBody @Valid AtualizarVendaDTO dto) {
        try {
            VendaCompletoDTO venda = vendaService.atualizarVenda(id, dto);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /api/vendas/{id}/cancelar - Cancelar venda
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<VendaCompletoDTO> cancelarVenda(
            @PathVariable Long id,
            @RequestBody @Valid CancelarVendaDTO dto) {
        try {
            VendaCompletoDTO venda = vendaService.cancelarVenda(id, dto);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/vendas/{id} - Excluir venda permanentemente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirVenda(@PathVariable Long id) {
        try {
            vendaService.excluirVenda(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================================
    // GERENCIAR ITENS
    // ============================================

    // POST /api/vendas/{vendaId}/itens/{animalServicoId} - Adicionar item existente
    @PostMapping("/{vendaId}/itens/{animalServicoId}")
    public ResponseEntity<VendaCompletoDTO> adicionarItem(
            @PathVariable Long vendaId,
            @PathVariable Long animalServicoId) {
        try {
            VendaCompletoDTO venda = vendaService.adicionarItem(vendaId, animalServicoId);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // POST /api/vendas/{vendaId}/itens - Adicionar novo item (criar AnimalServico na hora)
    @PostMapping("/{vendaId}/itens")
    public ResponseEntity<VendaCompletoDTO> adicionarItemNovo(
            @PathVariable Long vendaId,
            @RequestBody CriarVendaDTO.ItemVendaDTO itemDTO) {
        try {
            VendaCompletoDTO venda = vendaService.adicionarItemNovo(vendaId, itemDTO);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/vendas/{vendaId}/itens/{itemId} - Remover item
    @DeleteMapping("/{vendaId}/itens/{itemId}")
    public ResponseEntity<VendaCompletoDTO> removerItem(
            @PathVariable Long vendaId,
            @PathVariable Long itemId) {
        try {
            VendaCompletoDTO venda = vendaService.removerItem(vendaId, itemId);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ============================================
    // GERENCIAR BAIXAS (PAGAMENTOS)
    // ============================================

    // POST /api/vendas/baixas - Registrar pagamento
    @PostMapping("/baixas")
    public ResponseEntity<VendaCompletoDTO> registrarBaixa(@RequestBody @Valid CriarVendaBaixaDTO dto) {
        try {
            VendaCompletoDTO venda = vendaService.registrarBaixa(dto);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DELETE /api/vendas/{vendaId}/baixas/{baixaId} - Remover baixa
    @DeleteMapping("/{vendaId}/baixas/{baixaId}")
    public ResponseEntity<VendaCompletoDTO> removerBaixa(
            @PathVariable Long vendaId,
            @PathVariable Long baixaId) {
        try {
            VendaCompletoDTO venda = vendaService.removerBaixa(vendaId, baixaId);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

