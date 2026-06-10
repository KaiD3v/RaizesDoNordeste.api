package br.com.raizes.application.dto.fidelidade;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FidelidadeSaldoResponse {

    private UUID usuarioId;
    private Integer pontos;
}
