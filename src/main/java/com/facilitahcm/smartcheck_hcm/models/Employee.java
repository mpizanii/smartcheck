package com.facilitahcm.smartcheck_hcm.models;

import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 30)
    private String matricula;

    @Column(nullable = false, length = 80)
    private String cargo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workplace_id", nullable = false)
    private Workplace workplace;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_trabalho", nullable = false, length = 20)
    private TipoTrabalho tipoTrabalho;
}
