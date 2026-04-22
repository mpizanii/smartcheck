package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployeeRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio")
        String nome,

        @NotBlank(message = "O cargo é obrigatório")
        String cargo,

        @NotNull(message = "Workplace ID é obrigatório")
        Long workplaceId,

        @NotNull(message = "O tipo de trabalho é obrigatório")
        TipoTrabalho tipoTrabalho,

        @NotBlank(message = "O parâmetro login é obrigatório")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        String password,

        @NotNull(message = "O tipo do usuário é obrigatório")
        TipoUsuario tipoUsuario
) {}
