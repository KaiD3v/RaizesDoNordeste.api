package br.com.raizes.application.dto.pagamento;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SimularPagamentoRequest {

    @NotNull(message = "Pedido é obrigatório")
    private UUID pedidoId;

    @NotBlank(message = "Número do cartão mock é obrigatório")
    private String numeroCartaoMock;
}
