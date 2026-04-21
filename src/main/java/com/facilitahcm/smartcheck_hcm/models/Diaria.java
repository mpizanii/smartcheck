package com.facilitahcm.smartcheck_hcm.models;

import com.facilitahcm.smartcheck_hcm.enums.ModoDiaria;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diarias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false, unique = true)
    private Employee employee;

    @Column(name = "data_diaria", nullable = false, unique = true)
    LocalDate data;

    @Column(name = "modo_diaria", nullable = false)
    ModoDiaria modoDiaria;

    @Column(name = "created_by")
    LocalDateTime dataCriacao;
}
