package br.com.raizes.application.dto.pedido;

import java.util.UUID;

import br.com.raizes.domain.enums.CanalPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CriarPedidoRequest {

    @NotNull(message = "Unidade é obrigatória")
    private UUID unidadeId;

    @NotNull(message = "Canal do pedido é obrigatório")
    private CanalPedido canalPedido;

    @NotEmpty(message = "Itens do pedido são obrigatórios")
    @Valid
    private List<ItemPedidoRequest> itens;

    @NotBlank(message = "Forma de pagamento é obrigatória")
    private String formaPagamento;
}
