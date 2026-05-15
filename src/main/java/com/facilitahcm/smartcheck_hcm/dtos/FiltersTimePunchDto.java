package com.facilitahcm.smartcheck_hcm.dtos;

import java.time.LocalDateTime;

public record FiltersTimePunchDto (
    Long employeeId,
    LocalDateTime dataHoraInicio,
    LocalDateTime dataHoraFim
) { }
