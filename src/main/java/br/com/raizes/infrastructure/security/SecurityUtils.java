package br.com.raizes.infrastructure.security;

import br.com.raizes.domain.entity.Usuario;
import br.com.raizes.domain.exception.AcessoNegadoException;
import br.com.raizes.domain.exception.RecursoNaoEncontradoException;
import br.com.raizes.infrastructure.persistence.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AcessoNegadoException("Usuário não autenticado");
        }
        return (UUID) authentication.getPrincipal();
    }

    public Usuario getCurrentUser() {
        UUID userId = getCurrentUserId();
        return usuarioRepository.findById(userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }
}
