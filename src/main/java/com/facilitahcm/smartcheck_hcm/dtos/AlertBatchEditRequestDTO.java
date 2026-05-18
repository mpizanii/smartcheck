package com.facilitahcm.smartcheck_hcm.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AlertBatchEditRequestDTO(
        @NotEmpty(message = "A lista de IDs não pode estar vazia")
        List<Long> ids,

        @Size(max = 255)
        String observacao,

        @NotNull(message = "O campo 'resolvido' é obrigatório")
        Boolean resolvido
) { }
