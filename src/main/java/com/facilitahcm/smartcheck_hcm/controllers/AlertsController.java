package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.AlertResponseDTO;
import com.facilitahcm.smartcheck_hcm.models.Alert;
import com.facilitahcm.smartcheck_hcm.services.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertsController {
    private final AlertService alertService;

    public AlertsController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping()
    public ResponseEntity<List<AlertResponseDTO>> buscarAlertas(){
        List<AlertResponseDTO> alerts = alertService.buscarAlertas();

        return ResponseEntity.ok(alerts);
    }
}
