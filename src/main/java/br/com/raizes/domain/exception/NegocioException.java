package br.com.raizes.domain.exception;

public class NegocioException extends DomainException {

    public NegocioException(String errorCode, String message) {
        super(errorCode, message);
    }
}
