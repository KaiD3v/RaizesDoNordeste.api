package br.com.raizes.domain.entity;

import java.util.UUID;

import br.com.raizes.domain.enums.TipoPromocao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "promocoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promocao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPromocao tipo;

    @Column(name = "valor_desconto", precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    @Column(name = "dia_semana")
    private Integer diaSemana;

    @Column(name = "produto_gratis_id")
    private UUID produtoGratisId;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativa = true;
}
