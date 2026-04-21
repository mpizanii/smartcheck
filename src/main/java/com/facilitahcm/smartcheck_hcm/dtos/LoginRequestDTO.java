package com.facilitahcm.smartcheck_hcm.dtos;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "Parâmetro login é obrigatório")
        String login,

        @NotBlank(message = "Senha obrigatória")
        String password
) { }
