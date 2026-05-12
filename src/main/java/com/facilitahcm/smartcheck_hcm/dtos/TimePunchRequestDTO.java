package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TimePunchRequestDTO(
    @NotNull(message = "O tipo de batida é obrigatório")
    TipoTimePunch tipoTimePunch,

    @NotNull(message = "A latitude é obrigatória")
    @DecimalMin(value = "-90.0", message = "A latitude deve ser maior ou igual a -90")
    @DecimalMax(value = "90.0", message = "A latitude deve ser menor ou igual a 90")
    Double latitude,

    @NotNull(message = "A longitude é obrigatória")
    @DecimalMin(value = "-180.0", message = "A longitude deve ser maior ou igual a -180")
    @DecimalMax(value = "180.0", message = "A longitude deve ser menor ou igual a 180")
    Double longitude
) { }
