package br.com.raizes.application.service;

import br.com.raizes.TestIds;
import br.com.raizes.application.dto.fidelidade.ResgatarFidelidadeRequest;
import br.com.raizes.domain.entity.Fidelidade;
import br.com.raizes.domain.entity.Usuario;
import br.com.raizes.domain.enums.Role;
import br.com.raizes.domain.exception.PontosInsuficientesException;
import br.com.raizes.infrastructure.persistence.FidelidadeRepository;
import br.com.raizes.infrastructure.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FidelidadeServiceTest {

    @Mock
    private FidelidadeRepository fidelidadeRepository;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private FidelidadeService fidelidadeService;

    private Usuario usuario;
    private Fidelidade fidelidade;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id(TestIds.USUARIO_CLIENTE).role(Role.CLIENTE).build();
        fidelidade = Fidelidade.builder()
                .id(UUID.randomUUID())
                .usuario(usuario)
                .pontos(200)
                .build();
    }

    @Test
    void creditarPontosAoEntregar() {
        when(fidelidadeRepository.findByUsuarioId(TestIds.USUARIO_CLIENTE)).thenReturn(Optional.of(fidelidade));
        when(fidelidadeRepository.save(any())).thenReturn(fidelidade);

        fidelidadeService.creditarPontos(TestIds.USUARIO_CLIENTE, BigDecimal.valueOf(35.50));

        assertEquals(235, fidelidade.getPontos());
    }

    @Test
    void resgatar100PontosDeveDarDesconto10() {
        ResgatarFidelidadeRequest request = new ResgatarFidelidadeRequest();
        request.setPontos(100);

        when(securityUtils.getCurrentUser()).thenReturn(usuario);
        when(fidelidadeRepository.findByUsuarioId(TestIds.USUARIO_CLIENTE)).thenReturn(Optional.of(fidelidade));
        when(fidelidadeRepository.save(any())).thenReturn(fidelidade);

        var response = fidelidadeService.resgatar(request);

        assertEquals(100, response.getPontosUsados());
        assertEquals(BigDecimal.valueOf(10), response.getValorDesconto());
        assertEquals(100, response.getPontosRestantes());
    }

    @Test
    void resgatarSemPontosDeveFalhar() {
        fidelidade.setPontos(50);
        ResgatarFidelidadeRequest request = new ResgatarFidelidadeRequest();
        request.setPontos(100);

        when(securityUtils.getCurrentUser()).thenReturn(usuario);
        when(fidelidadeRepository.findByUsuarioId(TestIds.USUARIO_CLIENTE)).thenReturn(Optional.of(fidelidade));

        assertThrows(PontosInsuficientesException.class, () -> fidelidadeService.resgatar(request));
    }
}
