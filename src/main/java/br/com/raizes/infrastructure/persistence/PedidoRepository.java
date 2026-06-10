package br.com.raizes.infrastructure.persistence;

import java.util.UUID;

import br.com.raizes.domain.entity.Pedido;
import br.com.raizes.domain.enums.CanalPedido;
import br.com.raizes.domain.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    @Query("SELECT p FROM Pedido p WHERE " +
           "(:usuarioId IS NULL OR p.usuario.id = :usuarioId) AND " +
           "(:unidadeId IS NULL OR p.unidade.id = :unidadeId) AND " +
           "(:canalPedido IS NULL OR p.canalPedido = :canalPedido) AND " +
           "(:status IS NULL OR p.status = :status) " +
           "ORDER BY p.dataCriacao DESC")
    List<Pedido> findWithFilters(
            @Param("usuarioId") UUID usuarioId,
            @Param("unidadeId") UUID unidadeId,
            @Param("canalPedido") CanalPedido canalPedido,
            @Param("status") StatusPedido status);
}
