package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.WorkplaceRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.WorkplaceResponseDTO;
import com.facilitahcm.smartcheck_hcm.services.WorkplaceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/workplaces")
public class WorkplaceController {
    private final WorkplaceService workplaceService;

    @Autowired
    public WorkplaceController(WorkplaceService workplaceService){
        this.workplaceService = workplaceService;
    }

    @PostMapping
    public ResponseEntity<WorkplaceResponseDTO> cadastrarUnidade(@Valid @RequestBody WorkplaceRequestDTO dto){
        WorkplaceResponseDTO response = workplaceService.criarUnidade(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkplaceResponseDTO>> buscarUnidades(){
        List<WorkplaceResponseDTO> response = workplaceService.buscarUnidades();
        return ResponseEntity.ok(response);
    }
}
