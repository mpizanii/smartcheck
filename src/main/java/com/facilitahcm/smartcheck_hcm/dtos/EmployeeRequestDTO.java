package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmployeeRequestDTO(
        @NotBlank(message = "O nome não pode ser vazio")
        String nome,

        @NotBlank(message = "A matrícula é obrigatória")
        @Size(min = 3, max = 30, message = "A matrícula deve ter entre 3 e 30 caracteres")
        String matricula,

        @NotBlank(message = "O cargo é obrigatório")
        String cargo,

        @NotNull(message = "Workplace ID é obrigatório")
        Long workplaceId,

        @NotNull(message = "O tipo de trabalho é obrigatório")
        TipoTrabalho tipoTrabalho
) {}
