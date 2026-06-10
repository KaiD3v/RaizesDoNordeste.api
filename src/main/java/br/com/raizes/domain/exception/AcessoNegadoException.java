package br.com.raizes.domain.exception;

public class AcessoNegadoException extends DomainException {

    public AcessoNegadoException(String message) {
        super("ACESSO_NEGADO", message);
    }
}
