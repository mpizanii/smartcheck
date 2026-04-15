package com.facilitahcm.smartcheck_hcm.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WorkplaceResponseDTO(
        Long id,
        String nome,
        Double latitude,
        Double longitude,
        @JsonProperty("raio_metros")
        Integer raioMetros

) { }
