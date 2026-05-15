package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.EmployeeRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.EmployeeResponseDTO;
import com.facilitahcm.smartcheck_hcm.dtos.FiltersEmployeeDto;
import com.facilitahcm.smartcheck_hcm.dtos.RegisterRequestDTO;
import com.facilitahcm.smartcheck_hcm.exceptions.BusinessException;
import com.facilitahcm.smartcheck_hcm.exceptions.ResourceNotFoundException;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.models.Users;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.EmployeeRepository;
import com.facilitahcm.smartcheck_hcm.repositories.WorkplaceRepository;
import com.facilitahcm.smartcheck_hcm.specifications.EmployeeSpecification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final WorkplaceRepository workplaceRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, WorkplaceRepository workplaceRepository, AuthenticationService authenticationService){
        this.employeeRepository = employeeRepository;
        this.workplaceRepository = workplaceRepository;
        this.authenticationService = authenticationService;
    }

    @Transactional
    public EmployeeResponseDTO cadastrarEmployee(EmployeeRequestDTO dto){
        Workplace workplace = workplaceRepository.findById(dto.workplaceId())
            .orElseThrow(() -> new ResourceNotFoundException("Workplace " + dto.workplaceId() + "não encontrado."));

        RegisterRequestDTO userData = new RegisterRequestDTO(dto.login(), dto.password(), dto.tipoUsuario());

        Users user = authenticationService.cadastrarUsuario(userData);

        Employee employee = Employee.builder()
            .nome(dto.nome())
            .cargo(dto.cargo())
            .workplace(workplace)
            .tipoTrabalho(dto.tipoTrabalho())
            .user(user)
            .build();

        Employee saved = employeeRepository.save(employee);

        return converterParaResponseDTO(saved);
    }

    public Page<EmployeeResponseDTO> buscarEmployees(FiltersEmployeeDto filtersEmployeeDto, Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(EmployeeSpecification.comFiltros(filtersEmployeeDto), pageable);

        return employees.map(this::converterParaResponseDTO);
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

    public Employee buscarEmployeePorLogin(String login) {
        Employee employee = employeeRepository.findByUserLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Employee com login " + login + " não encontrado."));

        return employee;
    }


    private EmployeeResponseDTO converterParaResponseDTO(Employee employee){
        return new EmployeeResponseDTO(
            employee.getId(),
            employee.getNome(),
            employee.getCargo(),
            employee.getWorkplace().getId(),
            employee.getWorkplace().getNome(),
            employee.getTipoTrabalho(),
            employee.getUser().getLogin(),
            employee.getUser().getTipoUsuario()
        );
    }
}
