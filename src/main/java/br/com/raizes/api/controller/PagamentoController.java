package br.com.raizes.api.controller;

import br.com.raizes.application.dto.pagamento.PagamentoResponse;
import br.com.raizes.application.dto.pagamento.SimularPagamentoRequest;
import br.com.raizes.application.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping("/simular")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE')")
    @Operation(summary = "Simular pagamento mock")
    public PagamentoResponse simular(@Valid @RequestBody SimularPagamentoRequest request) {
        return pagamentoService.simular(request);
    }
}
