package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.EmployeeRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.EmployeeResponseDTO;
import com.facilitahcm.smartcheck_hcm.exceptions.BusinessException;
import com.facilitahcm.smartcheck_hcm.exceptions.ResourceNotFoundException;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.EmployeeRepository;
import com.facilitahcm.smartcheck_hcm.repositories.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final WorkplaceRepository workplaceRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, WorkplaceRepository workplaceRepository){
        this.employeeRepository = employeeRepository;
        this.workplaceRepository = workplaceRepository;
    }

    public EmployeeResponseDTO criarEmployee(EmployeeRequestDTO dto){
        if (employeeRepository.existsByMatricula(dto.matricula())) {
            throw new BusinessException("Matrícula " + dto.matricula() + "já existente.");
        }

        Workplace workplace = workplaceRepository.findById(dto.workplaceId())
            .orElseThrow(() -> new ResourceNotFoundException("Workplace " + dto.workplaceId() + "não encontrado."));

        Employee employee = Employee.builder()
            .nome(dto.nome())
            .matricula(dto.matricula())
            .cargo(dto.cargo())
            .workplace(workplace)
            .tipoTrabalho(dto.tipoTrabalho())
            .build();

        Employee saved = employeeRepository.save(employee);

        return converterParaResponseDTO(saved);
    }

    public List<EmployeeResponseDTO> buscarEmployees() {
        List<Employee> employees = employeeRepository.findAllWithWorkplace();

        List<EmployeeResponseDTO> responseList = employees.stream()
            .map(this::converterParaResponseDTO)
            .toList();

        return responseList;
    }

    public List<EmployeeResponseDTO> buscarEmployeesPorUnidade(Long workplaceId) {
        List<Employee> employees = employeeRepository.findByWorkplaceId(workplaceId);

        List<EmployeeResponseDTO> responseList = employees.stream()
            .map(this::converterParaResponseDTO)
            .toList();

        return responseList;
    }

    // Usado em TimePunchService
    public Employee buscarEmployeePorId(Long id) {
        Employee employee = employeeRepository.findByIdWithWorkplace(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee " + id + " não encontrado."));

        return employee;
    }


    private EmployeeResponseDTO converterParaResponseDTO(Employee employee){
        return new EmployeeResponseDTO(
            employee.getId(),
            employee.getNome(),
            employee.getMatricula(),
            employee.getCargo(),
            employee.getWorkplace().getId(),
            employee.getWorkplace().getNome(),
            employee.getTipoTrabalho()
        );
    }
}
