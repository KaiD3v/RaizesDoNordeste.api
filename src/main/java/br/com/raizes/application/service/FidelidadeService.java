package br.com.raizes.application.service;

import java.util.UUID;

import br.com.raizes.application.dto.fidelidade.FidelidadeSaldoResponse;
import br.com.raizes.application.dto.fidelidade.ResgatarFidelidadeRequest;
import br.com.raizes.application.dto.fidelidade.ResgatarFidelidadeResponse;
import br.com.raizes.domain.entity.Fidelidade;
import br.com.raizes.domain.entity.HistoricoResgate;
import br.com.raizes.domain.entity.Usuario;
import br.com.raizes.domain.exception.PontosInsuficientesException;
import br.com.raizes.infrastructure.persistence.FidelidadeRepository;
import br.com.raizes.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FidelidadeService {

    private static final int PONTOS_POR_REAL = 1;
    private static final int PONTOS_PARA_RESGATE = 100;
    private static final BigDecimal VALOR_RESGATE = BigDecimal.valueOf(10);

    private final FidelidadeRepository fidelidadeRepository;
    private final SecurityUtils securityUtils;

    public FidelidadeSaldoResponse consultarSaldo() {
        Usuario usuario = securityUtils.getCurrentUser();
        Fidelidade fidelidade = getOrCreate(usuario);

        return FidelidadeSaldoResponse.builder()
                .usuarioId(usuario.getId())
                .pontos(fidelidade.getPontos())
                .build();
    }

    @Transactional
    public ResgatarFidelidadeResponse resgatar(ResgatarFidelidadeRequest request) {
        Usuario usuario = securityUtils.getCurrentUser();
        Fidelidade fidelidade = getOrCreate(usuario);

        if (fidelidade.getPontos() < request.getPontos()) {
            throw new PontosInsuficientesException(
                    "Pontos insuficientes. Disponível: " + fidelidade.getPontos());
        }

        if (request.getPontos() % PONTOS_PARA_RESGATE != 0) {
            throw new PontosInsuficientesException(
                    "Resgate deve ser em múltiplos de " + PONTOS_PARA_RESGATE + " pontos");
        }

        int multiplos = request.getPontos() / PONTOS_PARA_RESGATE;
        BigDecimal valorDesconto = VALOR_RESGATE.multiply(BigDecimal.valueOf(multiplos));

        fidelidade.setPontos(fidelidade.getPontos() - request.getPontos());

        HistoricoResgate resgate = HistoricoResgate.builder()
                .fidelidade(fidelidade)
                .pontosUsados(request.getPontos())
                .valorDesconto(valorDesconto)
                .data(Instant.now())
                .build();
        fidelidade.getHistoricoResgates().add(resgate);

        fidelidadeRepository.save(fidelidade);

        return ResgatarFidelidadeResponse.builder()
                .pontosUsados(request.getPontos())
                .valorDesconto(valorDesconto)
                .pontosRestantes(fidelidade.getPontos())
                .mensagem("Resgate realizado: R$ " + valorDesconto + " de desconto")
                .build();
    }

    @Transactional
    public void creditarPontos(UUID usuarioId, BigDecimal totalPedido) {
        Fidelidade fidelidade = fidelidadeRepository.findByUsuarioId(usuarioId)
                .orElse(null);

        if (fidelidade == null) {
            return;
        }

        int pontos = totalPedido.setScale(0, RoundingMode.DOWN).intValue() * PONTOS_POR_REAL;
        fidelidade.setPontos(fidelidade.getPontos() + pontos);
        fidelidadeRepository.save(fidelidade);
    }

    private Fidelidade getOrCreate(Usuario usuario) {
        return fidelidadeRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> {
                    Fidelidade f = Fidelidade.builder()
                            .usuario(usuario)
                            .pontos(0)
                            .build();
                    return fidelidadeRepository.save(f);
                });
    }
}
