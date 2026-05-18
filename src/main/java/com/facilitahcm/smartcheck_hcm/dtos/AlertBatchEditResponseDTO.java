package com.facilitahcm.smartcheck_hcm.dtos;

import java.util.HashMap;

public record AlertBatchEditResponseDTO (
        Integer totalSolicitados,
        Integer totalResolvidos,
        HashMap<Long, String> erros
) { }
