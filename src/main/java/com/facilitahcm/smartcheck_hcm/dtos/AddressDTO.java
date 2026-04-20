package com.facilitahcm.smartcheck_hcm.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressDTO(
        String road,
        String quarter,
        String city,
        String state
) { }
