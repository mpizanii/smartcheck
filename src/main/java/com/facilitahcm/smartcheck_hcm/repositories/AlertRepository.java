package com.facilitahcm.smartcheck_hcm.repositories;

import com.facilitahcm.smartcheck_hcm.enums.TipoAlerta;
import com.facilitahcm.smartcheck_hcm.models.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByTimePunchId(Long timePunchId);
    List<Alert> findAllByResolvidoFalse();
    List<Alert> findAllByTipoAlerta(TipoAlerta tipoAlerta);
}
