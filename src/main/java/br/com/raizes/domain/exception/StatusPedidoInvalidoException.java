package br.com.raizes.domain.exception;

public class StatusPedidoInvalidoException extends DomainException {

    public StatusPedidoInvalidoException(String message) {
        super("STATUS_PEDIDO_INVALIDO", message);
    }
}
