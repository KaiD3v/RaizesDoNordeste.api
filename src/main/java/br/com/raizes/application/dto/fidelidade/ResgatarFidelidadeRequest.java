package br.com.raizes.application.dto.fidelidade;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResgatarFidelidadeRequest {

    @NotNull(message = "Pontos são obrigatórios")
    @Min(value = 100, message = "Mínimo de 100 pontos para resgate")
    private Integer pontos;
}
