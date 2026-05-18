package com.facilitahcm.smartcheck_hcm.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlertEditRequestDTO (
        @NotNull(message = "O campo 'resolvido' é obrigatório")
        Boolean resolvido,

        @Size(max = 255)
        String observacao
) { }
