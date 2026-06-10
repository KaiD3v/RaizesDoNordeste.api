package br.com.raizes.domain.exception;

public class CanalPedidoInvalidoException extends DomainException {

    public CanalPedidoInvalidoException(String message) {
        super("CANAL_PEDIDO_INVALIDO", message);
    }
}
