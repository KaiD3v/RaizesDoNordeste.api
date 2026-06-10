package br.com.raizes.api.exception;

import br.com.raizes.application.dto.error.ErrorDetail;
import br.com.raizes.application.dto.error.ErrorResponse;
import br.com.raizes.domain.exception.CanalPedidoInvalidoException;
import br.com.raizes.domain.exception.DomainException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                            HttpServletRequest request) {
        List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toDetail)
                .toList();

        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDACAO_FALHOU",
                "Erro de validação nos campos", details, request.getRequestURI());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        String message = "Corpo da requisição inválido";
        List<ErrorDetail> details = Collections.emptyList();

        if (ex.getCause() instanceof InvalidFormatException cause) {
            if (cause.getTargetType() != null && cause.getTargetType().isEnum()) {
                message = "Valor de enum inválido: " + cause.getValue();
                details = List.of(new ErrorDetail(
                        cause.getPathReference(), "Valor inválido para enum"));
                return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "ENUM_INVALIDO",
                        message, details, request.getRequestURI());
            }
        }

        return buildResponse(HttpStatus.BAD_REQUEST, "REQUISICAO_INVALIDA",
                message, details, request.getRequestURI());
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException ex, HttpServletRequest request) {
        HttpStatus status = mapDomainStatus(ex);
        return buildResponse(status, ex.getErrorCode(), ex.getMessage(),
                Collections.emptyList(), request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                            HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, "ACESSO_NEGADO",
                "Acesso negado", Collections.emptyList(), request.getRequestURI());
    }

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAuth(AuthenticationException ex,
                                                    HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "NAO_AUTENTICADO",
                "Credenciais inválidas ou token ausente", Collections.emptyList(),
                request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "ERRO_INTERNO",
                "Erro interno do servidor", Collections.emptyList(), request.getRequestURI());
    }

    private HttpStatus mapDomainStatus(DomainException ex) {
        if (ex instanceof CanalPedidoInvalidoException) {
            return HttpStatus.UNPROCESSABLE_ENTITY;
        }
        return switch (ex.getErrorCode()) {
            case "ESTOQUE_INSUFICIENTE" -> HttpStatus.CONFLICT;
            case "STATUS_PEDIDO_INVALIDO", "CANAL_PEDIDO_INVALIDO" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "RECURSO_NAO_ENCONTRADO" -> HttpStatus.NOT_FOUND;
            case "ACESSO_NEGADO" -> HttpStatus.FORBIDDEN;
            case "CONSENTIMENTO_LGPD_OBRIGATORIO", "PONTOS_INSUFICIENTES",
                 "EMAIL_JA_CADASTRADO", "PEDIDO_NAO_AGUARDANDO_PAGAMENTO" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };
    }

    private ErrorDetail toDetail(FieldError error) {
        return new ErrorDetail(error.getField(), error.getDefaultMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error,
                                                          String message, List<ErrorDetail> details,
                                                          String path) {
        ErrorResponse body = ErrorResponse.builder()
                .error(error)
                .message(message)
                .details(details)
                .timestamp(Instant.now())
                .path(path)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
