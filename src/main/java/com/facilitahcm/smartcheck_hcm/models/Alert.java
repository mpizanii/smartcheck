package com.facilitahcm.smartcheck_hcm.models;

import com.facilitahcm.smartcheck_hcm.enums.TipoAlerta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "time_punch_id", nullable = false)
    private TimePunch timePunch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAlerta tipoAlerta;

    @Column(length = 200)
    private String mensagem;

    @Column(nullable = false)
    private Boolean resolvido = false;

    @Column(name = "observacao_admin", length = 255)
    private String observacaoAdmin;

    @Column(name = "resolvido_em")
    private LocalDateTime resolvidoEm;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
}
