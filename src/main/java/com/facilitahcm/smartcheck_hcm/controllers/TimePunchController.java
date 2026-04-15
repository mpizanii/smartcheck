package com.facilitahcm.smartcheck_hcm.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimePunchController {
    @PostMapping("/api/time-punch")
    public void BaterPonto(){ }

    @GetMapping("/api/time-punch")
    public void ConsultarPonto(){ }

    @GetMapping("/api/time-punch/{dataInicio}/{dataFim}")
    public void ConsultarPontoPorData(){ }
}
