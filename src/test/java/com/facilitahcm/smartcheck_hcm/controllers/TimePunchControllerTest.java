package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersTimePunchDto;
import com.facilitahcm.smartcheck_hcm.dtos.TimePunchResponseDTO;
import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;
import com.facilitahcm.smartcheck_hcm.services.TimePunchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TimePunchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TimePunchService timePunchService;

    @BeforeEach
    void setup() {
        TimePunchController controller = new TimePunchController(timePunchService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @WithMockUser
    void consultarPontos_comPaginacaoPadrao_deveDelegarAoService() throws Exception {
        TimePunchResponseDTO dto = new TimePunchResponseDTO(
                1L,
                2L,
                "João Silva",
                TipoTimePunch.CHECK_IN,
                LocalDateTime.of(2026, 5, 12, 10, 0, 0),
                -23.5,
                -46.6
        );

        Page<TimePunchResponseDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataHora")), 1);
        when(timePunchService.buscarPontos(any(FiltersTimePunchDto.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/time-punches")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].employeeId").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));

        ArgumentCaptor<FiltersTimePunchDto> filtersCaptor = ArgumentCaptor.forClass(FiltersTimePunchDto.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(timePunchService).buscarPontos(filtersCaptor.capture(), pageableCaptor.capture());

        FiltersTimePunchDto filters = filtersCaptor.getValue();
        assertNull(filters.employeeId());
        assertNull(filters.dataHoraInicio());
        assertNull(filters.dataHoraFim());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "dataHora"), pageableCaptor.getValue().getSort());
    }

    @Test
    @WithMockUser
    void consultarPontos_dataInvalida_retornaBadRequest() throws Exception {
        mockMvc.perform(get("/api/time-punches")
                        .param("dataHoraInicio", "data-invalida")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

