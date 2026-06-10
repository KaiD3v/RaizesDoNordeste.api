package br.com.raizes.api.controller;

import java.util.UUID;

import br.com.raizes.application.dto.produto.ProdutoRequest;
import br.com.raizes.application.dto.produto.ProdutoResponse;
import br.com.raizes.application.service.ProdutoService;
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
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    @Operation(summary = "Listar produtos por unidade (público)")
    public List<ProdutoResponse> listar(@RequestParam UUID unidadeId) {
        return produtoService.listarPorUnidade(unidadeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Criar produto")
    public ProdutoResponse criar(@Valid @RequestBody ProdutoRequest request) {
        return produtoService.criar(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar produto")
    public ProdutoResponse atualizar(@PathVariable UUID id, @Valid @RequestBody ProdutoRequest request) {
        return produtoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('GERENTE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Excluir produto")
    public void deletar(@PathVariable UUID id) {
        produtoService.deletar(id);
    }
}
