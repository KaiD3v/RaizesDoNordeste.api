package br.com.raizes.application.dto.unidade;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UnidadeRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;
}
