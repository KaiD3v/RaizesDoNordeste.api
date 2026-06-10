package br.com.raizes.application.mapper;

import br.com.raizes.application.dto.pedido.ItemPedidoResponse;
import br.com.raizes.application.dto.pedido.PedidoResponse;
import br.com.raizes.domain.entity.ItemPedido;
import br.com.raizes.domain.entity.Pedido;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    public PedidoResponse toResponse(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        return PedidoResponse.builder()
                .id(pedido.getId())
                .usuarioId(pedido.getUsuario() != null ? pedido.getUsuario().getId() : null)
                .unidadeId(pedido.getUnidade() != null ? pedido.getUnidade().getId() : null)
                .canalPedido(pedido.getCanalPedido())
                .status(pedido.getStatus())
                .total(pedido.getTotal())
                .descontoPromocao(pedido.getDescontoPromocao())
                .dataCriacao(pedido.getDataCriacao())
                .build();
    }

    public ItemPedidoResponse toItemResponse(ItemPedido item) {
        if (item == null) {
            return null;
        }

        return ItemPedidoResponse.builder()
                .produtoId(item.getProduto() != null ? item.getProduto().getId() : null)
                .produtoNome(item.getProduto() != null ? item.getProduto().getNome() : null)
                .quantidade(item.getQuantidade())
                .precoUnitario(item.getPrecoUnitario())
                .build();
    }
}
