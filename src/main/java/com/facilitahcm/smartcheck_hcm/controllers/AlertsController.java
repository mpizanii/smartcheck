package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.*;
import com.facilitahcm.smartcheck_hcm.services.AlertService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts")
public class AlertsController {
    private final AlertService alertService;

    public AlertsController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping()
    public ResponseEntity<Page<AlertResponseDTO>> buscarAlertas(FiltersAlertsDto filters, @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable){
        Page<AlertResponseDTO> alerts = alertService.buscarAlertas(filters, pageable);

        return ResponseEntity.ok(alerts);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlertResponseDTO> editarAlerta(@RequestBody @Valid AlertEditRequestDTO alertEditRequestDTO, @PathVariable Long id) {
        AlertResponseDTO alertResponseDTO = alertService.editarAlerta(alertEditRequestDTO, id);

        return ResponseEntity.ok(alertResponseDTO);
    }

    @PatchMapping
    public ResponseEntity<AlertBatchEditResponseDTO> editarAlertasLote(@RequestBody @Valid AlertBatchEditRequestDTO alertBatchEditRequestDTO) {
        AlertBatchEditResponseDTO alertBatchEditResponseDTO = alertService.editarAlertasLote(alertBatchEditRequestDTO);

        return ResponseEntity.ok(alertBatchEditResponseDTO);
    }

}
