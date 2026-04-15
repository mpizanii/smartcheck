package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;

public record EmployeeResponseDTO(
        Long id,
        String nome,
        String matricula,
        String cargo,
        Long workplaceId,
        String workplaceName,
        TipoTrabalho tipoTrabalho
) { }