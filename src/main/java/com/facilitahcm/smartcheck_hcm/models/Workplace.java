package com.facilitahcm.smartcheck_hcm.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workplaces")
@Getter
@Setter
@NoArgsConstructor // Cria construtor sem nenhum argumento
@AllArgsConstructor // Cria construtor com todos os argumentos
@Builder // Cria objetos de maneira mais fácil
public class Workplace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String nome;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "raio_metros", nullable = false)
    private Integer raioMetros;

    @OneToMany(mappedBy = "workplace", fetch = FetchType.LAZY) // mappedBy = representa o campo da outra tabela | FetchType.Lazy = evita trazer a lista sem eu chamar exatamente ela
    @Builder.Default // Evita que o Lombok builder ignore essa inicialização
    private List<Employee> employees = new ArrayList<>();
}
