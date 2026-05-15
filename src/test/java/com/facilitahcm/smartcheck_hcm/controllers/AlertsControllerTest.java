package com.facilitahcm.smartcheck_hcm.controllers;

import com.facilitahcm.smartcheck_hcm.dtos.AlertResponseDTO;
import com.facilitahcm.smartcheck_hcm.dtos.FiltersAlertsDto;
import com.facilitahcm.smartcheck_hcm.enums.TipoAlerta;
import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;
import com.facilitahcm.smartcheck_hcm.services.AlertService;
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
class AlertsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AlertService alertService;

    @BeforeEach
    void setup() {
        AlertsController controller = new AlertsController(alertService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void buscarAlertas_comFiltros_deveDelegarAoService() throws Exception {
        AlertResponseDTO dto = new AlertResponseDTO(
                2L, TipoAlerta.DUPLICATE, "Ponto duplicado",
                LocalDateTime.of(2026, 4, 22, 8, 52, 39),
                35L, TipoTimePunch.CHECK_IN, false, null, null, 9L, "Teste"
        );
        Page<AlertResponseDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dataHora")), 1);

        when(alertService.buscarAlertas(any(FiltersAlertsDto.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/alerts")
                        .param("employeeId", "9")
                        .param("tipoAlerta", "DUPLICATE")
                        .param("tipoTimePunch", "CHECK_IN")
                        .param("dataHoraInicio", "2026-04-22T00:00:00")
                        .param("dataHoraFim", "2026-04-23T00:00:00")
                        .param("page", "0")  // ✅ página 0
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].tipoAlerta").value("DUPLICATE"))
                .andExpect(jsonPath("$.content[0].timePunchId").value(35))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(1));

        ArgumentCaptor<FiltersAlertsDto> filtersCaptor = ArgumentCaptor.forClass(FiltersAlertsDto.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(alertService).buscarAlertas(filtersCaptor.capture(), pageableCaptor.capture());

        FiltersAlertsDto filters = filtersCaptor.getValue();
        assertEquals(9L, filters.employeeId());
        assertEquals(TipoAlerta.DUPLICATE, filters.tipoAlerta());
        assertEquals(TipoTimePunch.CHECK_IN, filters.tipoTimePunch());
        assertEquals(LocalDateTime.of(2026, 4, 22, 0, 0), filters.dataHoraInicio());
        assertEquals(LocalDateTime.of(2026, 4, 23, 0, 0), filters.dataHoraFim());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(5, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "dataHora"), pageableCaptor.getValue().getSort());
    }

    @Test
    void buscarAlertas_semParametros_deveUsarPageableDefault() throws Exception {
        AlertResponseDTO dto = new AlertResponseDTO(
                1L, TipoAlerta.OUT_OF_RANGE, "Ponto fora do raio permitido",
                LocalDateTime.of(2026, 4, 22, 9, 0, 0),
                34L, TipoTimePunch.CHECK_OUT, false, null, null,9L, "Teste"
        );
        Page<AlertResponseDTO> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataHora")), 1);

        when(alertService.buscarAlertas(any(FiltersAlertsDto.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/alerts")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));

        ArgumentCaptor<FiltersAlertsDto> filtersCaptor = ArgumentCaptor.forClass(FiltersAlertsDto.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(alertService).buscarAlertas(filtersCaptor.capture(), pageableCaptor.capture());

        FiltersAlertsDto filters = filtersCaptor.getValue();
        assertNull(filters.employeeId());
        assertNull(filters.tipoAlerta());
        assertNull(filters.dataHoraInicio());
        assertNull(filters.dataHoraFim());
        assertNull(filters.tipoTimePunch());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(10, pageableCaptor.getValue().getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "dataHora"), pageableCaptor.getValue().getSort());
    }
}

