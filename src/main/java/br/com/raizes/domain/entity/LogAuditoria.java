package br.com.raizes.domain.entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "logs_auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id")
    private UUID usuarioId;

    @Column(nullable = false)
    private String acao;

    @Column(name = "dados_antes", columnDefinition = "TEXT")
    private String dadosAntes;

    @Column(name = "dados_depois", columnDefinition = "TEXT")
    private String dadosDepois;

    @Column(nullable = false)
    private Instant timestamp;

    @Column
    private String ip;
}
