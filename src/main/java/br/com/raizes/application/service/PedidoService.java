package br.com.raizes.application.service;

import java.util.UUID;

import br.com.raizes.application.dto.pedido.*;
import br.com.raizes.application.mapper.PedidoMapper;
import br.com.raizes.domain.entity.*;
import br.com.raizes.domain.enums.CanalPedido;
import br.com.raizes.domain.enums.Role;
import br.com.raizes.domain.enums.StatusPedido;
import br.com.raizes.domain.exception.AcessoNegadoException;
import br.com.raizes.domain.exception.RecursoNaoEncontradoException;
import br.com.raizes.domain.exception.StatusPedidoInvalidoException;
import br.com.raizes.infrastructure.audit.AuditService;
import br.com.raizes.infrastructure.persistence.PedidoRepository;
import br.com.raizes.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final UnidadeService unidadeService;
    private final ProdutoService produtoService;
    private final EstoqueService estoqueService;
    private final PromocaoService promocaoService;
    private final FidelidadeService fidelidadeService;
    private final AuditService auditService;
    private final SecurityUtils securityUtils;

    @Transactional
    public PedidoResponse criar(CriarPedidoRequest request) {
        Usuario usuario = securityUtils.getCurrentUser();
        Unidade unidade = unidadeService.findById(request.getUnidadeId());

        for (ItemPedidoRequest item : request.getItens()) {
            estoqueService.validarDisponibilidade(item.getProdutoId(), request.getUnidadeId(), item.getQuantidade());
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        Pedido pedido = Pedido.builder()
                .usuario(usuario)
                .unidade(unidade)
                .canalPedido(request.getCanalPedido())
                .status(StatusPedido.AGUARDANDO_PAGAMENTO)
                .dataCriacao(Instant.now())
                .estoqueBaixado(false)
                .build();

        for (ItemPedidoRequest itemReq : request.getItens()) {
            Produto produto = produtoService.findById(itemReq.getProdutoId());
            ItemPedido item = ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(itemReq.getQuantidade())
                    .precoUnitario(produto.getPreco())
                    .build();
            pedido.getItens().add(item);
            subtotal = subtotal.add(produto.getPreco().multiply(BigDecimal.valueOf(itemReq.getQuantidade())));
        }

        BigDecimal desconto = promocaoService.calcularDesconto(subtotal);
        pedido.setDescontoPromocao(desconto);
        pedido.setTotal(subtotal.subtract(desconto));

        pedido = pedidoRepository.save(pedido);

        auditService.logCriacaoPedido(usuario.getId(), toAuditMap(pedido));

        return toPedidoResponse(pedido);
    }

    public List<PedidoResponse> listar(CanalPedido canalPedido, StatusPedido status, UUID unidadeId) {
        Usuario usuario = securityUtils.getCurrentUser();

        UUID filtroUsuarioId = null;
        UUID filtroUnidadeId = unidadeId;

        switch (usuario.getRole()) {
            case CLIENTE -> filtroUsuarioId = usuario.getId();
            case ATENDENTE, COZINHA -> {
                if (usuario.getUnidade() == null) {
                    throw new AcessoNegadoException("Usuário sem unidade vinculada");
                }
                filtroUnidadeId = usuario.getUnidade().getId();
            }
            case GERENTE -> {
                auditService.logListagemGerente(usuario.getId(), Map.of(
                        "canalPedido", canalPedido != null ? canalPedido.name() : null,
                        "status", status != null ? status.name() : null,
                        "unidadeId", unidadeId
                ));
            }
        }

        return pedidoRepository.findWithFilters(filtroUsuarioId, filtroUnidadeId, canalPedido, status)
                .stream()
                .map(this::toPedidoResponse)
                .toList();
    }

    @Transactional
    public PedidoResponse atualizarStatus(UUID id, AtualizarStatusRequest request) {
        Usuario usuario = securityUtils.getCurrentUser();
        Pedido pedido = findById(id);

        validarAcessoUnidade(usuario, pedido);

        StatusPedido statusAnterior = pedido.getStatus();
        StatusPedido novoStatus = request.getStatus();

        validarTransicao(statusAnterior, novoStatus);

        if (novoStatus == StatusPedido.CANCELADO) {
            auditService.logCancelamento(usuario.getId(), toAuditMap(pedido));
        }

        pedido.setStatus(novoStatus);
        pedido = pedidoRepository.save(pedido);

        auditService.logMudancaStatus(usuario.getId(),
                Map.of("status", statusAnterior.name()),
                Map.of("status", novoStatus.name()));

        if (novoStatus == StatusPedido.ENTREGUE) {
            fidelidadeService.creditarPontos(pedido.getUsuario().getId(), pedido.getTotal());
        }

        return toPedidoResponse(pedido);
    }

    public Pedido findById(UUID id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + id));
    }

    @Transactional
    public void marcarEstoqueBaixado(Pedido pedido) {
        if (pedido.isEstoqueBaixado()) {
            return;
        }
        for (ItemPedido item : pedido.getItens()) {
            estoqueService.baixarEstoque(
                    item.getProduto().getId(),
                    pedido.getUnidade().getId(),
                    item.getQuantidade()
            );
        }
        pedido.setEstoqueBaixado(true);
        pedidoRepository.save(pedido);
    }

    @Transactional
    public void atualizarStatusInterno(Pedido pedido, StatusPedido novoStatus) {
        StatusPedido anterior = pedido.getStatus();
        pedido.setStatus(novoStatus);
        pedidoRepository.save(pedido);
        auditService.logMudancaStatus(null,
                Map.of("status", anterior.name()),
                Map.of("status", novoStatus.name()));
    }

    private void validarAcessoUnidade(Usuario usuario, Pedido pedido) {
        if (usuario.getRole() == Role.COZINHA || usuario.getRole() == Role.ATENDENTE) {
            if (usuario.getUnidade() == null ||
                !usuario.getUnidade().getId().equals(pedido.getUnidade().getId())) {
                throw new AcessoNegadoException("Acesso negado ao pedido de outra unidade");
            }
        }
    }

    private void validarTransicao(StatusPedido atual, StatusPedido novo) {
        if (novo == StatusPedido.CANCELADO) {
            if (atual != StatusPedido.AGUARDANDO_PAGAMENTO) {
                throw new StatusPedidoInvalidoException(
                        "Cancelamento permitido apenas antes do pagamento");
            }
            return;
        }

        boolean valido = switch (atual) {
            case AGUARDANDO_PAGAMENTO -> novo == StatusPedido.PAGO || novo == StatusPedido.CANCELADO;
            case PAGO -> novo == StatusPedido.EM_PREPARACAO;
            case EM_PREPARACAO -> novo == StatusPedido.PRONTO;
            case PRONTO -> novo == StatusPedido.ENTREGUE;
            default -> false;
        };

        if (!valido) {
            throw new StatusPedidoInvalidoException(
                    "Transição inválida de " + atual + " para " + novo);
        }
    }

    private PedidoResponse toPedidoResponse(Pedido pedido) {
        PedidoResponse response = pedidoMapper.toResponse(pedido);
        response.setItens(pedido.getItens().stream()
                .map(pedidoMapper::toItemResponse)
                .toList());
        return response;
    }

    private Map<String, Object> toAuditMap(Pedido pedido) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", pedido.getId());
        map.put("status", pedido.getStatus().name());
        map.put("canalPedido", pedido.getCanalPedido().name());
        map.put("total", pedido.getTotal());
        return map;
    }
}
