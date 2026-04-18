package com.facilitahcm.smartcheck_hcm.repositories;

import com.facilitahcm.smartcheck_hcm.models.Diaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DiariaRepository extends JpaRepository<Diaria, Long> {
    Optional<Diaria> findDiariaByDataAndEmployee_Id(LocalDate data, Long employeeId);
}
