package com.facilitahcm.smartcheck_hcm.repositories;

import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimePunchRepository extends JpaRepository<TimePunch, Long> {
    List<TimePunch> findAllByEmployeeId(Long employeeId);
}
