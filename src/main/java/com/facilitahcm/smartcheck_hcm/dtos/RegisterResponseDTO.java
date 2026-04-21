package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;

public record RegisterResponseDTO (
        String login,
        TipoUsuario tipoUsuario
) { }
