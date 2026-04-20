package com.facilitahcm.smartcheck_hcm.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WorkplaceResponseDTO(
        Long id,
        String nome,
        Double latitude,
        Double longitude,
        String cidade,
        String logradouro,
        String estado,
        @JsonProperty("raio_metros")
        Double raioMetros
) { }
