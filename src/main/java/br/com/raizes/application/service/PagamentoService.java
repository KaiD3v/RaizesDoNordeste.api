package br.com.raizes.application.service;

import br.com.raizes.application.dto.pagamento.PagamentoResponse;
import br.com.raizes.application.dto.pagamento.SimularPagamentoRequest;
import br.com.raizes.domain.entity.Pagamento;
import br.com.raizes.domain.entity.Pedido;
import br.com.raizes.domain.entity.Usuario;
import br.com.raizes.domain.enums.StatusPagamento;
import br.com.raizes.domain.enums.StatusPedido;
import br.com.raizes.domain.exception.AcessoNegadoException;
import br.com.raizes.domain.exception.NegocioException;
import br.com.raizes.infrastructure.persistence.PagamentoRepository;
import br.com.raizes.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoService pedidoService;
    private final SecurityUtils securityUtils;

    @Transactional
    public PagamentoResponse simular(SimularPagamentoRequest request) {
        Usuario usuario = securityUtils.getCurrentUser();
        Pedido pedido = pedidoService.findById(request.getPedidoId());

        if (usuario.getRole() == br.com.raizes.domain.enums.Role.CLIENTE &&
            !pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Você só pode pagar seus próprios pedidos");
        }

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new NegocioException("PEDIDO_NAO_AGUARDANDO_PAGAMENTO",
                    "Pedido não está aguardando pagamento");
        }

        boolean aprovado = !request.getNumeroCartaoMock().endsWith("1111");
        StatusPagamento statusPagamento = aprovado ? StatusPagamento.APROVADO : StatusPagamento.RECUSADO;

        Pagamento pagamento = Pagamento.builder()
                .pedido(pedido)
                .status(statusPagamento)
                .transacaoMockId(UUID.randomUUID().toString())
                .data(Instant.now())
                .build();
        pagamento = pagamentoRepository.save(pagamento);

        StatusPedido statusPedido = pedido.getStatus();
        String mensagem;

        if (aprovado) {
            pedidoService.atualizarStatusInterno(pedido, StatusPedido.PAGO);
            pedidoService.marcarEstoqueBaixado(pedido);
            pedidoService.atualizarStatusInterno(pedido, StatusPedido.EM_PREPARACAO);
            statusPedido = StatusPedido.EM_PREPARACAO;
            mensagem = "Pagamento aprovado. Pedido em preparação.";
        } else {
            mensagem = "Pagamento recusado. Pedido permanece aguardando pagamento.";
        }

        return PagamentoResponse.builder()
                .id(pagamento.getId())
                .pedidoId(pedido.getId())
                .status(statusPagamento)
                .statusPedido(statusPedido)
                .transacaoMockId(pagamento.getTransacaoMockId())
                .data(pagamento.getData())
                .mensagem(mensagem)
                .build();
    }
}
