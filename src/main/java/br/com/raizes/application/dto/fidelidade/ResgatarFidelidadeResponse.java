package br.com.raizes.application.dto.fidelidade;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ResgatarFidelidadeResponse {

    private Integer pontosUsados;
    private BigDecimal valorDesconto;
    private Integer pontosRestantes;
    private String mensagem;
}
