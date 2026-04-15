package com.facilitahcm.smartcheck_hcm.dtos;

import jakarta.validation.constraints.*;

public record WorkplaceRequestDTO(
        @NotBlank(message = "O nome da unidade não pode ser vazio")
        String nome,

        @NotNull(message = "A latitude é obrigatória")
        @DecimalMin(value = "-90.0", message = "Latitude deve ser entre -90 e 90")
        @DecimalMax(value = "90.0", message = "Latitude deve ser entre -90 e 90")
        Double latitude,

        @NotNull(message = "A longitude é obrigatória")
        @DecimalMin(value = "-180.0", message = "Longitude deve ser entre -180 e 180")
        @DecimalMax(value = "180.0", message = "Longitude deve ser entre -180 e 180")
        Double longitude,

        @NotNull(message = "O raio de distância é obrigatório")
        @Positive(message = "O raio deve ser um valor maior que zero")
        Integer raio
) { }
