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

        @NotBlank(message = "A latitude é obrigatória")
        String latitude,

        @NotBlank(message = "A longitude é obrigatória")
        String longitude,

        @NotNull(message = "O raio de distância é obrigatório")
        @Positive(message = "O raio deve ser um valor maior que zero")
        Double raio
) { }
