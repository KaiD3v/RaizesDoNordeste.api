package br.com.raizes.application.dto.estoque;

import java.util.UUID;

import br.com.raizes.domain.enums.TipoMovimentacaoEstoque;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MovimentarEstoqueRequest {

    @NotNull(message = "Produto é obrigatório")
    private UUID produtoId;

    @NotNull(message = "Unidade é obrigatória")
    private UUID unidadeId;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    private Integer quantidade;

    @NotNull(message = "Tipo de movimentação é obrigatório")
    private TipoMovimentacaoEstoque tipo;
}
