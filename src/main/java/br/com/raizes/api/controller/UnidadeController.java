package br.com.raizes.api.controller;

import java.util.UUID;

import br.com.raizes.application.dto.unidade.UnidadeRequest;
import br.com.raizes.application.dto.unidade.UnidadeResponse;
import br.com.raizes.application.service.UnidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades")
@SecurityRequirement(name = "bearerAuth")
public class UnidadeController {

    private final UnidadeService unidadeService;

    @GetMapping
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Listar todas as unidades")
    public List<UnidadeResponse> listar() {
        return unidadeService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Buscar unidade por ID")
    public UnidadeResponse buscar(@PathVariable UUID id) {
        return unidadeService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Criar nova unidade")
    public UnidadeResponse criar(@Valid @RequestBody UnidadeRequest request) {
        return unidadeService.criar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Atualizar unidade")
    public UnidadeResponse atualizar(@PathVariable UUID id, @Valid @RequestBody UnidadeRequest request) {
        return unidadeService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(summary = "Excluir unidade")
    public void deletar(@PathVariable UUID id) {
        unidadeService.deletar(id);
    }
}
