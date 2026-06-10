package br.com.raizes.application.dto.pedido;

import br.com.raizes.domain.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AtualizarStatusRequest {

    @NotNull(message = "Status é obrigatório")
    private StatusPedido status;
}
