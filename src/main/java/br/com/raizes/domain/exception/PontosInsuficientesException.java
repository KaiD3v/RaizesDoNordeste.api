package br.com.raizes.domain.exception;

public class PontosInsuficientesException extends DomainException {

    public PontosInsuficientesException(String message) {
        super("PONTOS_INSUFICIENTES", message);
    }
}
