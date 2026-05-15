package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.EmployeeResponseDTO;
import com.facilitahcm.smartcheck_hcm.dtos.FiltersEmployeeDto;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmployeeService employeeService;

    @BeforeEach
    void setup() {
        EmployeeController controller = new EmployeeController(employeeService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void buscarFuncionarios_comFiltrosEPaginacao_deveDelegarAoService() throws Exception {
        EmployeeResponseDTO dto = new EmployeeResponseDTO(
                7L, "Matheus", "Desenvolvedor", 2L, "Matriz",
                TipoTrabalho.PRESENCIAL, "matheus", TipoUsuario.EMPLOYEE
        );
        Page<EmployeeResponseDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 3), 1);

        when(employeeService.buscarEmployees(any(FiltersEmployeeDto.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/employees")
                        .param("nome", "Math")
                        .param("cargo", "Dev")
                        .param("workplaceId", "2")
                        .param("tipoTrabalho", "PRESENCIAL")
                        .param("page", "0")
                        .param("size", "3")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(7))
                .andExpect(jsonPath("$.content[0].nome").value("Matheus"))
                .andExpect(jsonPath("$.content[0].workplaceId").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(3))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(employeeService).buscarEmployees(any(FiltersEmployeeDto.class), any(Pageable.class));
    }
}


