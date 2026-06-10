package br.com.raizes.domain.exception;

public class RecursoNaoEncontradoException extends DomainException {

    public RecursoNaoEncontradoException(String message) {
        super("RECURSO_NAO_ENCONTRADO", message);
    }
}
