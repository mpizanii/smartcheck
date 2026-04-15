package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.EmployeeRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.EmployeeResponseDTO;
import com.facilitahcm.smartcheck_hcm.services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> cadastrarFuncionario(@Valid @RequestBody EmployeeRequestDTO employeeRequest){
        EmployeeResponseDTO response = employeeService.criarEmployee(employeeRequest);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> buscarFuncionarios(){
        List<EmployeeResponseDTO> response = employeeService.buscarEmployees();
        return ResponseEntity.ok(response);
    }
}
