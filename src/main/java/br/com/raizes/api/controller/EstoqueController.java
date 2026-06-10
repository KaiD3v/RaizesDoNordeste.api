package br.com.raizes.api.controller;

import java.util.UUID;

import br.com.raizes.application.dto.estoque.EstoqueResponse;
import br.com.raizes.application.dto.estoque.MovimentarEstoqueRequest;
import br.com.raizes.application.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque")
@SecurityRequirement(name = "bearerAuth")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Consultar saldo de estoque")
    public EstoqueResponse consultar(@RequestParam UUID unidadeId, @RequestParam UUID produtoId) {
        return estoqueService.consultar(unidadeId, produtoId);
    }

    @PostMapping("/movimentar")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Movimentar estoque (entrada/saída)")
    public EstoqueResponse movimentar(@Valid @RequestBody MovimentarEstoqueRequest request) {
        return estoqueService.movimentar(request);
    }
}
