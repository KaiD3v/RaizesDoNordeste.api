package br.com.raizes.application.service;

import br.com.raizes.TestIds;
import br.com.raizes.application.dto.pagamento.SimularPagamentoRequest;
import br.com.raizes.domain.entity.*;
import br.com.raizes.domain.enums.CanalPedido;
import br.com.raizes.domain.enums.Role;
import br.com.raizes.domain.enums.StatusPagamento;
import br.com.raizes.domain.enums.StatusPedido;
import br.com.raizes.infrastructure.persistence.PagamentoRepository;
import br.com.raizes.infrastructure.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private PedidoService pedidoService;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Usuario usuario;
    private Pedido pedido;
    private UUID pedidoId;

    @BeforeEach
    void setUp() {
        pedidoId = UUID.randomUUID();

        usuario = Usuario.builder()
                .id(TestIds.USUARIO_CLIENTE)
                .email("cliente@test.com")
                .role(Role.CLIENTE)
                .build();

        Unidade unidade = Unidade.builder().id(TestIds.UNIDADE_CENTRO).build();

        pedido = Pedido.builder()
                .id(pedidoId)
                .usuario(usuario)
                .unidade(unidade)
                .canalPedido(CanalPedido.APP)
                .status(StatusPedido.AGUARDANDO_PAGAMENTO)
                .total(BigDecimal.valueOf(28.90))
                .dataCriacao(Instant.now())
                .estoqueBaixado(false)
                .build();
    }

    @Test
    void pagamentoComCartao1111DeveSerRecusado() {
        SimularPagamentoRequest request = new SimularPagamentoRequest();
        request.setPedidoId(pedidoId);
        request.setNumeroCartaoMock("4111111111111111");

        when(securityUtils.getCurrentUser()).thenReturn(usuario);
        when(pedidoService.findById(pedidoId)).thenReturn(pedido);
        when(pagamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = pagamentoService.simular(request);

        assertEquals(StatusPagamento.RECUSADO, response.getStatus());
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, response.getStatusPedido());
        verify(pedidoService, never()).marcarEstoqueBaixado(any());
    }

    @Test
    void pagamentoAprovadoDeveBaixarEstoque() {
        SimularPagamentoRequest request = new SimularPagamentoRequest();
        request.setPedidoId(pedidoId);
        request.setNumeroCartaoMock("4111111111111112");

        when(securityUtils.getCurrentUser()).thenReturn(usuario);
        when(pedidoService.findById(pedidoId)).thenReturn(pedido);
        when(pagamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = pagamentoService.simular(request);

        assertEquals(StatusPagamento.APROVADO, response.getStatus());
        assertEquals(StatusPedido.EM_PREPARACAO, response.getStatusPedido());
        verify(pedidoService).marcarEstoqueBaixado(pedido);
    }
}
