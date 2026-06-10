package br.com.raizes.api.controller;

import br.com.raizes.application.dto.fidelidade.FidelidadeSaldoResponse;
import br.com.raizes.application.dto.fidelidade.ResgatarFidelidadeRequest;
import br.com.raizes.application.dto.fidelidade.ResgatarFidelidadeResponse;
import br.com.raizes.application.service.FidelidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fidelidade")
@RequiredArgsConstructor
@Tag(name = "Fidelidade")
@SecurityRequirement(name = "bearerAuth")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    @GetMapping("/saldo")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Consultar saldo de pontos")
    public FidelidadeSaldoResponse saldo() {
        return fidelidadeService.consultarSaldo();
    }

    @PostMapping("/resgatar")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Resgatar pontos (100 pts = R$10)")
    public ResgatarFidelidadeResponse resgatar(@Valid @RequestBody ResgatarFidelidadeRequest request) {
        return fidelidadeService.resgatar(request);
    }
}
