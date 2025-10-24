package backend.prontodogbanho.dto;

import backend.prontodogbanho.model.VendaBaixa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecebimentoPendenteDTO {

    private Long baixaId;
    private Long vendaId;
    private Long codigoVenda;

    // Cliente
    private Long clienteId;
    private String clienteNome;

    // Forma de pagamento
    private String formaPagamentoNome;

    // Maquininha
    private Long maquininhaId;
    private String maquininhaNome;
    private String adquirenteNome;

    // Bandeira
    private Long bandeiraId;
    private String bandeiraNome;

    // Valores
    private BigDecimal valorBaixa;
    private BigDecimal valorTaxa;
    private BigDecimal valorLiquido;

    // Datas
    private LocalDateTime dataBaixa;
    private LocalDate dataPrevistaRecebimento;
    private LocalDate dataEfetivaRecebimento;
    private String statusRecebimento;

    // Informações adicionais
    private String tipoTransacao;
    private String tipoTransacaoDescricao;
    private Integer numeroParcelas;

    // Conta destino
    private String contaDestino;

    // Dias até receber
    private Integer diasAteRecebimento;
    private Boolean atrasado;

    // Construtor a partir da entidade
    public static RecebimentoPendenteDTO fromEntity(VendaBaixa baixa) {
        RecebimentoPendenteDTO dto = new RecebimentoPendenteDTO();

        dto.setBaixaId(baixa.getId());
        dto.setVendaId(baixa.getVendaId());
        dto.setCodigoVenda(baixa.getVenda() != null ? baixa.getVenda().getCodigoVenda() : null);

        // Cliente
        if (baixa.getVenda() != null && baixa.getVenda().getCliente() != null) {
            dto.setClienteId(baixa.getVenda().getCliente().getId());
            dto.setClienteNome(baixa.getVenda().getCliente().getNomeCompleto());
        }

        // Forma de pagamento
        dto.setFormaPagamentoNome(baixa.getFormaPagamentoNome());

        // Maquininha
        dto.setMaquininhaId(baixa.getMaquininhaId());
        dto.setMaquininhaNome(baixa.getMaquininhaNome());
        if (baixa.getMaquininha() != null && baixa.getMaquininha().getAdquirente() != null) {
            dto.setAdquirenteNome(baixa.getMaquininha().getAdquirente().getNome());
        }

        // Bandeira
        dto.setBandeiraId(baixa.getBandeiraId());
        dto.setBandeiraNome(baixa.getBandeiraNome());

        // Valores
        dto.setValorBaixa(baixa.getValorBaixa());
        dto.setValorTaxa(baixa.getValorTaxa());
        dto.setValorLiquido(baixa.getValorLiquido());

        // Datas
        dto.setDataBaixa(baixa.getDataBaixa());
        dto.setDataPrevistaRecebimento(baixa.getDataPrevistaRecebimento());
        dto.setDataEfetivaRecebimento(baixa.getDataEfetivaRecebimento());
        dto.setStatusRecebimento(baixa.getStatusRecebimento());

        // Informações adicionais
        dto.setTipoTransacao(baixa.getTipoTransacao());
        dto.setTipoTransacaoDescricao(baixa.getDescricaoTipoTransacao());
        dto.setNumeroParcelas(baixa.getNumeroParcelas());

        // Conta destino
        if (baixa.getMaquininha() != null && baixa.getMaquininha().getContaBancaria() != null) {
            dto.setContaDestino(baixa.getMaquininha().getContaBancaria().getNome());
        }

        // Dias até recebimento
        dto.setDiasAteRecebimento(baixa.getDiasAteRecebimento());
        dto.setAtrasado(baixa.isAtrasado());

        return dto;
    }
}

