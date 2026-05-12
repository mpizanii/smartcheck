package com.facilitahcm.smartcheck_hcm.dtos;

import jakarta.validation.constraints.*;

public record WorkplaceRequestDTO(
        @NotBlank(message = "O nome da unidade não pode ser vazio")
        String nome,

        @NotBlank(message = "O logradouro é obrigatório")
        String logradouro,

        @NotBlank(message = "A cidade é obrigatório")
        String cidade,

        @NotBlank(message = "O estado é obrigatório")
        String estado,

        @NotNull(message = "A latitude é obrigatória")
        @DecimalMin(value = "-90.0", message = "A latitude deve ser maior ou igual a -90")
        @DecimalMax(value = "90.0", message = "A latitude deve ser menor ou igual a 90")
        Double latitude,

        @NotNull(message = "A longitude é obrigatória")
        @DecimalMin(value = "-180.0", message = "A longitude deve ser maior ou igual a -180")
        @DecimalMax(value = "180.0", message = "A longitude deve ser menor ou igual a 180")
        Double longitude,

        @NotNull(message = "O raio de distância é obrigatório")
        @Positive(message = "O raio deve ser um valor maior que zero")
        Double raio
) { }
