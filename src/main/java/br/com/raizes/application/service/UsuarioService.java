package br.com.raizes.application.service;

import br.com.raizes.application.dto.usuario.UsuarioResponse;
import br.com.raizes.application.mapper.UsuarioMapper;
import br.com.raizes.domain.entity.Usuario;
import br.com.raizes.domain.enums.Role;
import br.com.raizes.domain.exception.AcessoNegadoException;
import br.com.raizes.infrastructure.persistence.UsuarioRepository;
import br.com.raizes.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final SecurityUtils securityUtils;

    public UsuarioResponse getMe() {
        Usuario usuario = securityUtils.getCurrentUser();
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void anonimizar() {
        Usuario usuario = securityUtils.getCurrentUser();

        if (usuario.getRole() != Role.CLIENTE) {
            throw new AcessoNegadoException("Apenas clientes podem solicitar anonimização de dados");
        }

        usuario.setNome("[removido]");
        usuario.setEmail("[removido]_" + usuario.getId() + "@anonimizado.local");
        usuario.setSenhaHash("[removido]");
        usuario.setAnonimizado(true);
        usuarioRepository.save(usuario);
    }
}
