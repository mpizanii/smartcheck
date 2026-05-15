package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersWorkplaceDto;
import com.facilitahcm.smartcheck_hcm.dtos.WorkplaceResponseDTO;
import com.facilitahcm.smartcheck_hcm.services.WorkplaceService;
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
class WorkplaceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private WorkplaceService workplaceService;

    @BeforeEach
    void setup() {
        WorkplaceController controller = new WorkplaceController(workplaceService, null);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void buscarUnidades_comFiltrosEPaginacao_deveDelegarAoService() throws Exception {
        WorkplaceResponseDTO dto = new WorkplaceResponseDTO(
                10L, "Matriz", -23.5505, -46.6333, "Sao Paulo", "Rua A", "SP", 150.0
        );
        Page<WorkplaceResponseDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 5), 1);

        when(workplaceService.buscarUnidades(any(FiltersWorkplaceDto.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/workplaces")
                        .param("nome", "Mat")
                        .param("cidade", "Sao")
                        .param("estado", "SP")
                        .param("page", "0")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].nome").value("Matriz"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(5));

        verify(workplaceService).buscarUnidades(any(FiltersWorkplaceDto.class), any(Pageable.class));
    }
}

