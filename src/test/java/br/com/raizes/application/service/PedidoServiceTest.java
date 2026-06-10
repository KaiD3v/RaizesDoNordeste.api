package br.com.raizes.application.service;

import br.com.raizes.TestIds;
import br.com.raizes.application.dto.pedido.CriarPedidoRequest;
import br.com.raizes.application.mapper.PedidoMapper;
import br.com.raizes.application.dto.pedido.ItemPedidoRequest;
import br.com.raizes.domain.entity.*;
import br.com.raizes.domain.enums.CanalPedido;
import br.com.raizes.domain.enums.Role;
import br.com.raizes.domain.exception.EstoqueInsuficienteException;
import br.com.raizes.infrastructure.audit.AuditService;
import br.com.raizes.infrastructure.persistence.PedidoRepository;
import br.com.raizes.infrastructure.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UnidadeService unidadeService;
    @Mock
    private ProdutoService produtoService;
    @Mock
    private EstoqueService estoqueService;
    @Mock
    private PromocaoService promocaoService;
    @Mock
    private FidelidadeService fidelidadeService;
    @Mock
    private AuditService auditService;
    @Mock
    private SecurityUtils securityUtils;
    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuario;
    private Unidade unidade;
    private Produto produto;
    private UUID pedidoId;

    @BeforeEach
    void setUp() {
        pedidoId = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(TestIds.USUARIO_CLIENTE)
                .nome("Cliente")
                .email("cliente@test.com")
                .role(Role.CLIENTE)
                .dataCriacao(Instant.now())
                .build();

        unidade = Unidade.builder().id(TestIds.UNIDADE_CENTRO).nome("Centro").endereco("Rua A").build();

        produto = Produto.builder()
                .id(TestIds.PRODUTO_BAIAO)
                .nome("Baião")
                .preco(BigDecimal.valueOf(28.90))
                .categoria("Pratos")
                .unidade(unidade)
                .build();
    }

    @Test
    void criarPedidoSemEstoqueDeveLancar409() {
        CriarPedidoRequest request = new CriarPedidoRequest();
        request.setUnidadeId(TestIds.UNIDADE_CENTRO);
        request.setCanalPedido(CanalPedido.APP);
        request.setFormaPagamento("MOCK");

        ItemPedidoRequest item = new ItemPedidoRequest();
        item.setProdutoId(TestIds.PRODUTO_BAIAO);
        item.setQuantidade(100);
        request.setItens(List.of(item));

        when(securityUtils.getCurrentUser()).thenReturn(usuario);
        when(unidadeService.findById(TestIds.UNIDADE_CENTRO)).thenReturn(unidade);
        doThrow(new EstoqueInsuficienteException("Estoque insuficiente"))
                .when(estoqueService).validarDisponibilidade(TestIds.PRODUTO_BAIAO, TestIds.UNIDADE_CENTRO, 100);

        assertThrows(EstoqueInsuficienteException.class, () -> pedidoService.criar(request));
    }

    @Test
    void criarPedidoComCanalValidoDeveSalvar() {
        CriarPedidoRequest request = new CriarPedidoRequest();
        request.setUnidadeId(TestIds.UNIDADE_CENTRO);
        request.setCanalPedido(CanalPedido.WEB);
        request.setFormaPagamento("MOCK");

        ItemPedidoRequest item = new ItemPedidoRequest();
        item.setProdutoId(TestIds.PRODUTO_BAIAO);
        item.setQuantidade(1);
        request.setItens(List.of(item));

        when(securityUtils.getCurrentUser()).thenReturn(usuario);
        when(unidadeService.findById(TestIds.UNIDADE_CENTRO)).thenReturn(unidade);
        when(produtoService.findById(TestIds.PRODUTO_BAIAO)).thenReturn(produto);
        when(promocaoService.calcularDesconto(any())).thenReturn(BigDecimal.ZERO);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> {
            Pedido p = inv.getArgument(0);
            p.setId(pedidoId);
            return p;
        });
        when(pedidoMapper.toResponse(any())).thenReturn(
                br.com.raizes.application.dto.pedido.PedidoResponse.builder().id(pedidoId).build());

        pedidoService.criar(request);

        verify(pedidoRepository).save(any(Pedido.class));
        verify(auditService).logCriacaoPedido(eq(TestIds.USUARIO_CLIENTE), any());
    }
}
