package br.com.raizes.infrastructure.audit;

import java.util.UUID;

import br.com.raizes.domain.entity.LogAuditoria;
import br.com.raizes.infrastructure.persistence.LogAuditoriaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final LogAuditoriaRepository logAuditoriaRepository;
    private final ObjectMapper objectMapper;

    public void log(UUID usuarioId, String acao, Object dadosAntes, Object dadosDepois) {
        LogAuditoria log = LogAuditoria.builder()
                .usuarioId(usuarioId)
                .acao(acao)
                .dadosAntes(toJson(dadosAntes))
                .dadosDepois(toJson(dadosDepois))
                .timestamp(Instant.now())
                .ip(getClientIp())
                .build();
        logAuditoriaRepository.save(log);
    }

    public void logCriacaoPedido(UUID usuarioId, Object pedido) {
        log(usuarioId, "CRIACAO_PEDIDO", null, pedido);
    }

    public void logMudancaStatus(UUID usuarioId, Object antes, Object depois) {
        log(usuarioId, "MUDANCA_STATUS_PEDIDO", antes, depois);
    }

    public void logCancelamento(UUID usuarioId, Object pedido) {
        log(usuarioId, "CANCELAMENTO_PEDIDO", pedido, null);
    }

    public void logListagemGerente(UUID usuarioId, Object filtros) {
        log(usuarioId, "LISTAGEM_PEDIDOS_GERENTE", null, filtros);
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
    }

    private String getClientIp() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
