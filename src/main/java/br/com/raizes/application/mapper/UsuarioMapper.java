package br.com.raizes.application.mapper;

import br.com.raizes.application.dto.usuario.UsuarioResponse;
import br.com.raizes.domain.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .unidadeId(usuario.getUnidade() != null ? usuario.getUnidade().getId() : null)
                .dataCriacao(usuario.getDataCriacao())
                .build();
    }
}
