package br.com.raizes.infrastructure.persistence;

import java.util.UUID;

import br.com.raizes.domain.entity.Fidelidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FidelidadeRepository extends JpaRepository<Fidelidade, UUID> {

    Optional<Fidelidade> findByUsuarioId(UUID usuarioId);
}
