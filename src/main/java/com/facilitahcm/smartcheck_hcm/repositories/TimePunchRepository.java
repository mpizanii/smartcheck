package com.facilitahcm.smartcheck_hcm.repositories;

import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TimePunchRepository extends JpaRepository<TimePunch, Long> {
    @Query("""
            SELECT tp FROM TimePunch tp
            WHERE (:employeeId IS NULL OR tp.employee.id = :employeeId)
                AND (:dataHoraInicio IS NULL OR tp.dataHora >= :dataHoraInicio)
                AND (:dataHoraFim IS NULL OR tp.dataHora <= :dataHoraFim)
            ORDER BY tp.dataHora desc
    """)
    Page<TimePunch> buscarPontos(
            @Param("employeeId") Long employeeId,
            @Param("dataHoraInicio")LocalDateTime dataHoraInicio,
            @Param("dataHoraFim") LocalDateTime dataHoraFim,
            Pageable pageable
    );
    Optional<TimePunch> findTopByEmployee_IdOrderByDataHoraDesc(Long employeeId); // "Optional" trata de casos que não houver ponto batido ainda; "_" Deixa explícito que está filtrando pelo id da relação employee
}
