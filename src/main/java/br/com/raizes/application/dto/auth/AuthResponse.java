package br.com.raizes.application.dto.auth;

import java.util.UUID;

import br.com.raizes.domain.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String token;
    private UUID userId;
    private Role role;
    private String email;
}
