package backend.prontodogbanho.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarAnimalServicoDTO {

    // Usar String para evitar problemas de timezone na serialização
    private String dataServico;
    private Integer banhosUsados;
    private Long animalId;
    private Long servicoId;
    private Long usuarioId;

    // Data de expiração do pacote (opcional)
    private String dataExpiracao;

    // Campos de pagamento (opcionais)
    private String statusPagamento; // "pago", "em_aberto", "cancelado"
    private String dataPagamento;   // Data do pagamento (se pago)

    // Lista de datas dos banhos já realizados (como String)
    // Se banhosUsados > 0, deve ter as datas correspondentes
    private List<String> datasBanhosRealizados;

    // Lista de observações para cada banho (opcional)
    private List<String> observacoesBanhos;

    // Lista de serviços adicionais para criar junto com o animal serviço
    private List<CriarServicoAdicionalDTO> servicosAdicionais;

    // Métodos auxiliares para conversão segura de datas
    public LocalDate getDataServicoAsLocalDate() {
        if (dataServico == null || dataServico.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dataServico, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            System.err.println("❌ Erro ao parsear dataServico: " + dataServico + " - " + e.getMessage());
            return null;
        }
    }

    public LocalDate getDataExpiracaoAsLocalDate() {
        if (dataExpiracao == null || dataExpiracao.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dataExpiracao, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            System.err.println("❌ Erro ao parsear dataExpiracao: " + dataExpiracao + " - " + e.getMessage());
            return null;
        }
    }

    public LocalDate getDataPagamentoAsLocalDate() {
        if (dataPagamento == null || dataPagamento.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dataPagamento, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            System.err.println("❌ Erro ao parsear dataPagamento: " + dataPagamento + " - " + e.getMessage());
            return null;
        }
    }

    public List<LocalDate> getDatasBanhosRealizadosAsLocalDate() {
        if (datasBanhosRealizados == null) {
            return null;
        }

        return datasBanhosRealizados.stream()
            .filter(data -> data != null && !data.trim().isEmpty())
            .map(data -> {
                try {
                    LocalDate localDate = LocalDate.parse(data, DateTimeFormatter.ISO_LOCAL_DATE);
                    System.out.println("✅ Data convertida: " + data + " -> " + localDate);
                    return localDate;
                } catch (Exception e) {
                    System.err.println("❌ Erro ao parsear data do banho: " + data + " - " + e.getMessage());
                    return null;
                }
            })
            .filter(localDate -> localDate != null)
            .collect(Collectors.toList());
    }
}
