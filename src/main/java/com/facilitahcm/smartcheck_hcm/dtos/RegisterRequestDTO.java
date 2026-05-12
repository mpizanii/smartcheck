package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import jakarta.validation.constraints.*;

public record RegisterRequestDTO(
        @NotBlank(message = "O parâmetro login é obrigatório")
        @Email(message = "O login deve ser um endereço de email válido")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 50, message = "A senha deve ter entre 8 e 50 caracteres")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{8,50}$",
                message = "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial"
        )
        String password,

        @NotNull(message = "O tipo do usuário é obrigatório")
        TipoUsuario tipoUsuario
) { }
