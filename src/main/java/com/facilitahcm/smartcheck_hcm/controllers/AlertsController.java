package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.AlertResponseDTO;
import com.facilitahcm.smartcheck_hcm.dtos.FiltersAlertsDto;
import com.facilitahcm.smartcheck_hcm.services.AlertService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alerts")
public class AlertsController {
    private final AlertService alertService;

    public AlertsController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping()
    public ResponseEntity<Page<AlertResponseDTO>> buscarAlertas(
            FiltersAlertsDto filters,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable
    ){
        Page<AlertResponseDTO> alerts = alertService.buscarAlertas(filters, pageable);

        return ResponseEntity.ok(alerts);
    }
}
