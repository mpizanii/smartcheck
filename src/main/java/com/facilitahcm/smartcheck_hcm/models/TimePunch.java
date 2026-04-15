package com.facilitahcm.smartcheck_hcm.models;

import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "time_punches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimePunch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoTimePunch tipoTimePunch;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @OneToMany(mappedBy = "timePunch", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Alert> alerts = new ArrayList<>();
}
