package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;

import java.time.LocalDateTime;

public record TimePunchResponseDTO(
        Long id,
        Long employeeId,
        String employeeName,
        TipoTimePunch tipoTimePunch,
        LocalDateTime dataHora,
        Double latitude,
        Double longitude
) { }
