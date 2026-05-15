package com.facilitahcm.smartcheck_hcm.repositories;

import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TimePunchRepository extends JpaRepository<TimePunch, Long>, JpaSpecificationExecutor<TimePunch> {
    Optional<TimePunch> findTopByEmployee_IdOrderByDataHoraDesc(Long employeeId); // "Optional" trata de casos que não houver ponto batido ainda; "_" Deixa explícito que está filtrando pelo id da relação employee
}
