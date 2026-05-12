package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.TimePunchRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.TimePunchResponseDTO;
import com.facilitahcm.smartcheck_hcm.services.TimePunchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping
    public ResponseEntity<Page<TimePunchResponseDTO>> consultarPontos(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) LocalDateTime dataHoraInicio,
            @RequestParam(required = false) LocalDateTime dataHoraFim,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable
            ) {
        Page<TimePunchResponseDTO> timePunches = timePunchService.buscarPontos(employeeId, dataHoraInicio, dataHoraFim, pageable);

        return ResponseEntity.ok(timePunches);
    }

}
