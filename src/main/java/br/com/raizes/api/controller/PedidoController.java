package br.com.raizes.api.controller;

import java.util.UUID;

import br.com.raizes.application.dto.pedido.AtualizarStatusRequest;
import br.com.raizes.application.dto.pedido.CriarPedidoRequest;
import br.com.raizes.application.dto.pedido.PedidoResponse;
import br.com.raizes.application.service.PedidoService;
import br.com.raizes.domain.enums.CanalPedido;
import br.com.raizes.domain.enums.StatusPedido;
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
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE')")
    @Operation(summary = "Criar pedido")
    public PedidoResponse criar(@Valid @RequestBody CriarPedidoRequest request) {
        return pedidoService.criar(request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE', 'COZINHA', 'GERENTE')")
    @Operation(summary = "Listar pedidos com filtros")
    public List<PedidoResponse> listar(
            @RequestParam(required = false) CanalPedido canalPedido,
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) UUID unidadeId) {
        return pedidoService.listar(canalPedido, status, unidadeId);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('COZINHA', 'GERENTE')")
    @Operation(summary = "Atualizar status do pedido")
    public PedidoResponse atualizarStatus(@PathVariable UUID id,
                                          @Valid @RequestBody AtualizarStatusRequest request) {
        return pedidoService.atualizarStatus(id, request);
    }
}
