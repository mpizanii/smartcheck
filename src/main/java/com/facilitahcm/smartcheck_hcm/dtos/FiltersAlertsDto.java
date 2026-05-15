package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoAlerta;
import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;

import java.time.LocalDateTime;

public record FiltersAlertsDto (
        Long employeeId,
        TipoAlerta tipoAlerta,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        TipoTimePunch tipoTimePunch,
        Boolean resolvido
) { }
