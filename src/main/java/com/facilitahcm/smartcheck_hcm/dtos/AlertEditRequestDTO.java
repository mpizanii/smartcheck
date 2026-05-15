package com.facilitahcm.smartcheck_hcm.dtos;

import jakarta.validation.constraints.Size;

public record AlertEditRequestDTO (
        Boolean resolvido,
        @Size(max = 255) String observacao
) { }
