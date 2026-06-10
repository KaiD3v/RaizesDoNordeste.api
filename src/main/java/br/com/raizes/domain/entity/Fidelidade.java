package br.com.raizes.domain.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fidelidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(nullable = false)
    @Builder.Default
    private Integer pontos = 0;

    @OneToMany(mappedBy = "fidelidade", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistoricoResgate> historicoResgates = new ArrayList<>();
}
