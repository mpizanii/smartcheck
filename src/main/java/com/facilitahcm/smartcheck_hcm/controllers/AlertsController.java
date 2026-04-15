package com.facilitahcm.smartcheck_hcm.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlertsController {
    @GetMapping("/api/alerts")
    public void BuscarAlertas(){ }

    @GetMapping("/api/alerts/{timePunchId}")
    public void BuscarAlertasPorPonto(Long TimePunchId){ }
}
