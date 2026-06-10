package br.com.raizes.application.dto.unidade;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnidadeResponse {

    private UUID id;
    private String nome;
    private String endereco;
}
