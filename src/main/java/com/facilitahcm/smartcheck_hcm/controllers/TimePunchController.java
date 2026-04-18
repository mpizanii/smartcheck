package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.TimePunchRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.TimePunchResponseDTO;
import com.facilitahcm.smartcheck_hcm.services.TimePunchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/time-punches")
public class TimePunchController {
    private final TimePunchService timePunchService;

    @Autowired
    public TimePunchController(TimePunchService timePunchService) {
        this.timePunchService = timePunchService;
    }

    @PostMapping
    public ResponseEntity<TimePunchResponseDTO> baterPonto(@Valid @RequestBody TimePunchRequestDTO timePunchRequest){
        TimePunchResponseDTO response = timePunchService.baterPonto(timePunchRequest);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{employeeId}")
    public void consultarPonto(@PathVariable Long employeeId){ }

    @GetMapping("/{dataInicio}/{dataFim}")
    public void consultarPontoPorData(@PathVariable LocalDateTime dataInicio, @PathVariable LocalDateTime dataFim){ }
}
