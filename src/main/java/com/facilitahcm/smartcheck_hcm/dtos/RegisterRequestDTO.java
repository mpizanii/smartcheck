package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(
        @NotBlank(message = "O parâmetro login é obrigatório")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        String password,

        @NotNull(message = "O tipo do usuário é obrigatório")
        TipoUsuario tipoUsuario
) { }
