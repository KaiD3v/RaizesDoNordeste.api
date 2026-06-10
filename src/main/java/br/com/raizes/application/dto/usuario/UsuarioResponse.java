package br.com.raizes.application.dto.usuario;

import java.util.UUID;

import br.com.raizes.domain.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UsuarioResponse {

    private UUID id;
    private String nome;
    private String email;
    private Role role;
    private UUID unidadeId;
    private Instant dataCriacao;
}
