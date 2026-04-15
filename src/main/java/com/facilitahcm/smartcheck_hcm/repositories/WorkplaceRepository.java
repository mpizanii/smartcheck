package com.facilitahcm.smartcheck_hcm.repositories;

import com.facilitahcm.smartcheck_hcm.models.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkplaceRepository extends JpaRepository<Workplace, Long> {
    Optional<Workplace> findByNome(String nome);
    boolean existsByNome(String nome);

    @Query("SELECT DISTINCT w FROM Workplace w LEFT JOIN FETCH w.employees ") // Evita problema de N + 1 query
    List<Workplace> findAllWithEmployees();
}
