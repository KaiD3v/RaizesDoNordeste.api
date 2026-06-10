package br.com.raizes.infrastructure.persistence;

import java.util.UUID;

import br.com.raizes.domain.entity.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeRepository extends JpaRepository<Unidade, UUID> {
}
