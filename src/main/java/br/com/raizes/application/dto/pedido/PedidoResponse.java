package br.com.raizes.application.dto.pedido;

import java.util.UUID;

import br.com.raizes.domain.enums.CanalPedido;
import br.com.raizes.domain.enums.StatusPedido;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PedidoResponse {

    private UUID id;
    private UUID usuarioId;
    private UUID unidadeId;
    private CanalPedido canalPedido;
    private StatusPedido status;
    private BigDecimal total;
    private BigDecimal descontoPromocao;
    private Instant dataCriacao;
    private List<ItemPedidoResponse> itens;
}
