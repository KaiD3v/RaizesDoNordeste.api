package br.com.raizes.infrastructure.persistence;

import java.util.UUID;

import br.com.raizes.domain.entity.LogAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, UUID> {
}
