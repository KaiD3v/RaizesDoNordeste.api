package br.com.raizes.domain.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "historico_resgates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoResgate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fidelidade_id", nullable = false)
    private Fidelidade fidelidade;

    @Column(name = "pontos_usados", nullable = false)
    private Integer pontosUsados;

    @Column(name = "valor_desconto", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    @Column(nullable = false)
    private Instant data;
}
