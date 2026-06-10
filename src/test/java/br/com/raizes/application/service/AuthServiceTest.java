package br.com.raizes.application.service;

import br.com.raizes.application.dto.auth.RegisterRequest;
import br.com.raizes.domain.exception.NegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Test
    void registerSemConsentimentoLgpdDeveFalhar() {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Teste");
        request.setEmail("teste@test.com");
        request.setSenha("123456");
        request.setConsentimentoLGPD(false);

        assertThrows(NegocioException.class, () -> authService.register(request));
    }
}
