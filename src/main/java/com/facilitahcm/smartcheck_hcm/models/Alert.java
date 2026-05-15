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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "time_punch_id", nullable = false)
    private TimePunch timePunch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAlerta tipoAlerta;

    @Column(length = 200)
    private String mensagem;

    @Column(nullable = false)
    private boolean resolvido;

    @Column
    private String observacaoAdmin;

    @Column
    private LocalDateTime resolvidoEm;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
}
