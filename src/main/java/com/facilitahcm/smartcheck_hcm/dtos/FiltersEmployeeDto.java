package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;

public record FiltersEmployeeDto(
    Long id,
    String nome,
    String cargo,
    Long workplaceId,
    TipoTrabalho tipoTrabalho
) { }
