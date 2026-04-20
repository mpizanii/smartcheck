package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.*;
import com.facilitahcm.smartcheck_hcm.services.GeolocationService;
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
    private final GeolocationService geolocationService;

    @Autowired
    public WorkplaceController(WorkplaceService workplaceService, GeolocationService geolocationService){
        this.workplaceService = workplaceService;
        this.geolocationService = geolocationService;
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

    @GetMapping("/preview-address")
    public ResponseEntity<NominatimResponseSearchDTO> previewEndereco(@RequestParam String endereco) {
        var sugestao = geolocationService.obterCoordenadas(endereco);

        return ResponseEntity.ok(sugestao);
    }

    @GetMapping("/reverse-geocoding")
    public ResponseEntity<NominatimResponseReverseDTO> reverseGeocoding(@RequestParam Double latitude, @RequestParam Double longitude) {
        var endereco = geolocationService.buscarEnderecoPorCoordenadas(latitude, longitude);

        return ResponseEntity.ok(endereco);
    }

    @GetMapping
    public ResponseEntity<List<WorkplaceResponseDTO>> buscarUnidades(){
        List<WorkplaceResponseDTO> response = workplaceService.buscarUnidades();
        return ResponseEntity.ok(response);
    }
}
