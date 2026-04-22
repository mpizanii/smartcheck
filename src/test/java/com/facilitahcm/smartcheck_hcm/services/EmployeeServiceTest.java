package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.EmployeeRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.EmployeeResponseDTO;
import com.facilitahcm.smartcheck_hcm.dtos.RegisterRequestDTO;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.exceptions.ResourceNotFoundException;
import com.facilitahcm.smartcheck_hcm.exceptions.UsernameExistsException;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.models.Users;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.EmployeeRepository;
import com.facilitahcm.smartcheck_hcm.repositories.WorkplaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private WorkplaceRepository workplaceRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void deveCadastrarEmployeeComUsuario_quandoDadosValidos() {
        Workplace workplace = criarWorkplace();
        Users user = criarUser();
        EmployeeRequestDTO request = new EmployeeRequestDTO(
                "Matheus",
                "Desenvolvedor Java",
                1L,
                TipoTrabalho.PRESENCIAL,
                "matheus",
                "123456",
                TipoUsuario.EMPLOYEE
        );

        when(workplaceRepository.findById(1L)).thenReturn(Optional.of(workplace));
        when(authenticationService.cadastrarUsuario(any(RegisterRequestDTO.class))).thenReturn(user);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee employee = invocation.getArgument(0);
            employee.setId(99L);
            return employee;
        });

        EmployeeResponseDTO response = employeeService.cadastrarEmployee(request);

        assertNotNull(response);
        assertEquals(99L, response.id());
        assertEquals("Matheus", response.nome());
        assertEquals("matheus", response.login());
        assertEquals(TipoUsuario.EMPLOYEE, response.tipoUsuario());

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee salvo = employeeCaptor.getValue();
        assertEquals(user, salvo.getUser());
        assertEquals(workplace, salvo.getWorkplace());
        assertEquals(TipoTrabalho.PRESENCIAL, salvo.getTipoTrabalho());

        verify(authenticationService).cadastrarUsuario(any(RegisterRequestDTO.class));
    }

    @Test
    void deveLancarResourceNotFound_quandoWorkplaceNaoExistir() {
        EmployeeRequestDTO request = new EmployeeRequestDTO(
                "Matheus",
                "Desenvolvedor Java",
                1L,
                TipoTrabalho.PRESENCIAL,
                "matheus",
                "123456",
                TipoUsuario.EMPLOYEE
        );

        when(workplaceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.cadastrarEmployee(request));
        verifyNoInteractions(authenticationService);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void devePropagarErro_quandoCadastroDeUsuarioFalhar() {
        Workplace workplace = criarWorkplace();
        EmployeeRequestDTO request = new EmployeeRequestDTO(
                "Matheus",
                "Desenvolvedor Java",
                1L,
                TipoTrabalho.PRESENCIAL,
                "matheus",
                "123456",
                TipoUsuario.EMPLOYEE
        );

        when(workplaceRepository.findById(1L)).thenReturn(Optional.of(workplace));
        when(authenticationService.cadastrarUsuario(any(RegisterRequestDTO.class)))
                .thenThrow(new UsernameExistsException("Usuário com esse login já existe"));

        assertThrows(UsernameExistsException.class, () -> employeeService.cadastrarEmployee(request));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void deveBuscarEmployeePorLogin_quandoExistir() {
        Employee employee = Employee.builder()
                .id(1L)
                .nome("Matheus")
                .build();

        when(employeeRepository.findByUserLogin("matheus")).thenReturn(Optional.of(employee));

        Employee resultado = employeeService.buscarEmployeePorLogin("matheus");

        assertEquals(employee, resultado);
    }

    @Test
    void deveLancarResourceNotFound_quandoBuscarEmployeePorLoginInexistente() {
        when(employeeRepository.findByUserLogin("inexistente")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.buscarEmployeePorLogin("inexistente"));
    }

    private Workplace criarWorkplace() {
        return Workplace.builder()
                .id(1L)
                .nome("Matriz")
                .logradouro("Rua A")
                .cidade("Sao Paulo")
                .estado("SP")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .raioMetros(150.0)
                .build();
    }

    private Users criarUser() {
        return Users.builder()
                .id(10L)
                .login("matheus")
                .password("hash")
                .tipoUsuario(TipoUsuario.EMPLOYEE)
                .build();
    }
}

