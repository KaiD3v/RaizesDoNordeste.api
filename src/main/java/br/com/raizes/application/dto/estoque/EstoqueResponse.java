package br.com.raizes.application.dto.estoque;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EstoqueResponse {

    private UUID id;
    private UUID produtoId;
    private String produtoNome;
    private UUID unidadeId;
    private Integer quantidade;
}
