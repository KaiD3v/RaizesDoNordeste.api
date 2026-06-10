package br.com.raizes.domain.exception;

public class EstoqueInsuficienteException extends DomainException {

    public EstoqueInsuficienteException(String message) {
        super("ESTOQUE_INSUFICIENTE", message);
    }
}
