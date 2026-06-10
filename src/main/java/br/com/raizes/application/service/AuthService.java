package br.com.raizes.application.service;

import br.com.raizes.application.dto.auth.AuthResponse;
import br.com.raizes.application.dto.auth.LoginRequest;
import br.com.raizes.application.dto.auth.RegisterRequest;
import br.com.raizes.domain.entity.Consentimento;
import br.com.raizes.domain.entity.Fidelidade;
import br.com.raizes.domain.entity.Usuario;
import br.com.raizes.domain.enums.Role;
import br.com.raizes.domain.exception.NegocioException;
import br.com.raizes.infrastructure.persistence.ConsentimentoRepository;
import br.com.raizes.infrastructure.persistence.FidelidadeRepository;
import br.com.raizes.infrastructure.persistence.UsuarioRepository;
import br.com.raizes.infrastructure.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ConsentimentoRepository consentimentoRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!Boolean.TRUE.equals(request.getConsentimentoLGPD())) {
            throw new NegocioException("CONSENTIMENTO_LGPD_OBRIGATORIO",
                    "É necessário consentir com o tratamento de dados conforme a LGPD");
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new NegocioException("EMAIL_JA_CADASTRADO", "Email já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senhaHash(passwordEncoder.encode(request.getSenha()))
                .role(Role.CLIENTE)
                .dataCriacao(Instant.now())
                .anonimizado(false)
                .build();

        usuario = usuarioRepository.save(usuario);

        Consentimento consentimento = Consentimento.builder()
                .usuarioId(usuario.getId())
                .finalidade("CADASTRO_E_OPERACAO_PLATAFORMA")
                .dataConsentimento(Instant.now())
                .ipOrigem(getClientIp())
                .build();
        consentimentoRepository.save(consentimento);

        Fidelidade fidelidade = Fidelidade.builder()
                .usuario(usuario)
                .pontos(0)
                .build();
        fidelidadeRepository.save(fidelidade);

        String token = jwtService.generateToken(usuario.getId(), usuario.getRole());

        return AuthResponse.builder()
                .token(token)
                .userId(usuario.getId())
                .role(usuario.getRole())
                .email(usuario.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NegocioException("CREDENCIAIS_INVALIDAS", "Credenciais inválidas"));

        String token = jwtService.generateToken(usuario.getId(), usuario.getRole());

        return AuthResponse.builder()
                .token(token)
                .userId(usuario.getId())
                .role(usuario.getRole())
                .email(usuario.getEmail())
                .build();
    }

    private String getClientIp() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        return request.getRemoteAddr();
    }
}
