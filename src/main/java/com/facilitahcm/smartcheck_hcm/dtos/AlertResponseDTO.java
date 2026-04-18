package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoAlerta;
import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;

import java.time.LocalDateTime;

public record AlertResponseDTO(
        Long id,
        TipoAlerta tipoAlerta,
        String message,
        LocalDateTime dataHora,
        Long timePunchId,
        TipoTimePunch tipoTimePunch,
        Long employeeId,
        String employeeName
) { }
