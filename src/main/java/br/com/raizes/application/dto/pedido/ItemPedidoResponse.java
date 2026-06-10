package br.com.raizes.application.dto.pedido;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ItemPedidoResponse {

    private UUID produtoId;
    private String produtoNome;
    private Integer quantidade;
    private BigDecimal precoUnitario;
}
