package br.com.raizes.infrastructure.persistence;

import java.util.UUID;

import br.com.raizes.domain.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, UUID> {

    Optional<Estoque> findByProdutoIdAndUnidadeId(UUID produtoId, UUID unidadeId);
}
