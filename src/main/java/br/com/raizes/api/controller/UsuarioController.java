package br.com.raizes.api.controller;

import br.com.raizes.application.dto.usuario.UsuarioResponse;
import br.com.raizes.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE', 'COZINHA', 'GERENTE')")
    @Operation(summary = "Consultar perfil do usuário autenticado")
    public UsuarioResponse getMe() {
        return usuarioService.getMe();
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Anonimizar dados pessoais (LGPD)")
    public void anonimizar() {
        usuarioService.anonimizar();
    }
}
