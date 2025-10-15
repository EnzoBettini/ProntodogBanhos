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

    // Lista de datas dos banhos já realizados (como String)
    // Se banhosUsados > 0, deve ter as datas correspondentes
    private List<String> datasBanhosRealizados;

    // Lista de observações para cada banho (opcional)
    private List<String> observacoesBanhos;

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
