package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;

public record EmployeeResponseDTO(
        Long id,
        String nome,
        String cargo,
        Long workplaceId,
        String workplaceName,
        TipoTrabalho tipoTrabalho,
        String login,
        TipoUsuario tipoUsuario
) { }