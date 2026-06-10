package br.com.raizes.application.dto.pagamento;

import java.util.UUID;

import br.com.raizes.domain.enums.StatusPagamento;
import br.com.raizes.domain.enums.StatusPedido;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PagamentoResponse {

    private UUID id;
    private UUID pedidoId;
    private StatusPagamento status;
    private StatusPedido statusPedido;
    private String transacaoMockId;
    private Instant data;
    private String mensagem;
}
