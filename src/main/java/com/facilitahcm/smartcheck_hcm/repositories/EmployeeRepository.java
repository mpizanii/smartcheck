package com.facilitahcm.smartcheck_hcm.repositories;

import com.facilitahcm.smartcheck_hcm.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByMatricula(String matricula);
    boolean existsByMatricula(String matricula);

    @Query("SELECT e FROM Employee e JOIN FETCH e.workplace")
    List<Employee> findAllWithWorkplace();

    @Query("SELECT e FROM Employee e JOIN FETCH e.workplace WHERE e.workplace.id = :workplaceId")
    List<Employee> findByWorkplaceId(Long workplaceId);
}
