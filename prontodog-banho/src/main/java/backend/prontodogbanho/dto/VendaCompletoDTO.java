package backend.prontodogbanho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendaCompletoDTO {

    private Long id;
    private Long codigoVenda;
    private LocalDateTime dataVenda;
    private String tipoVenda;
    private String statusVenda;

    // Cliente
    private Long clienteId;
    private String clienteNome;
    private String clienteCpf;

    // Usuário
    private Long usuarioId;
    private String usuarioNome;

    // Valores financeiros
    private BigDecimal valorBruto;
    private BigDecimal desconto;
    private BigDecimal valorTotal;
    private BigDecimal valorPago;
    private BigDecimal valorPendente;
    private BigDecimal percentualPago;

    // Quantidades
    private Integer quantidadeItens;
    private Integer quantidadeBaixas;

    private String observacoes;

    // Listas detalhadas
    private List<ItemDetalhadoDTO> itens;
    private List<BaixaDetalhadaDTO> baixas;

    // Informações de cancelamento
    private LocalDateTime canceladoEm;
    private String motivoCancelamento;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDetalhadoDTO {
        private Long id;
        private Long animalServicoId;

        // Animal
        private Long animalId;
        private String animalNome;
        private String animalTipo;
        private String animalRaca;

        // Serviço
        private Long servicoId;
        private String servicoNome;

        // Valores
        private BigDecimal valorItem;
        private BigDecimal descontoItem;
        private BigDecimal valorFinalItem;

        // Serviços adicionais
        private BigDecimal valorAdicionais;
        private Integer quantidadeAdicionais;
        private List<ServicoAdicionalResumoDTO> servicosAdicionais;

        private String observacoes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServicoAdicionalResumoDTO {
        private Long id;
        private Long servicoId;
        private String servicoNome;
        private Integer quantidade;
        private BigDecimal valorUnitario;
        private BigDecimal valorTotal;
        private String observacoes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaixaDetalhadaDTO {
        private Long id;
        private LocalDateTime dataBaixa;

        // Forma de pagamento
        private Long formaPagamentoId;
        private String formaPagamentoNome;
        private String formaPagamentoTipo;

        // Valores
        private BigDecimal valorBaixa;
        private BigDecimal valorTaxa;
        private BigDecimal valorLiquido;

        // Parcelamento
        private Integer numeroParcelas;
        private String dataPrimeiraParcela;
        private BigDecimal valorParcela;

        // Usuário
        private Long usuarioId;
        private String usuarioNome;

        private String observacoes;
    }
}

