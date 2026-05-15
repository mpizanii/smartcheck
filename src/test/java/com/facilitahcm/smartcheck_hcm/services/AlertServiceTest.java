package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.AlertEditRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.AlertResponseDTO;
import com.facilitahcm.smartcheck_hcm.dtos.FiltersAlertsDto;
import com.facilitahcm.smartcheck_hcm.enums.TipoAlerta;
import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.exceptions.ResourceNotFoundException;
import com.facilitahcm.smartcheck_hcm.models.Alert;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import com.facilitahcm.smartcheck_hcm.models.Users;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    void deveBuscarAlertasQuandoExistirem() {
        TimePunch timePunch = criarTimePunch(35L, TipoTimePunch.CHECK_IN);
        Alert alerta = Alert.builder()
                .id(1L)
                .version(1L)
                .timePunch(timePunch)
                .tipoAlerta(TipoAlerta.DUPLICATE)
                .mensagem("Mensagem")
                .resolvido(false)
                .observacaoAdmin(null)
                .resolvidoEm(null)
                .dataHora(timePunch.getDataHora())
                .build();
        Page<Alert> page = new PageImpl<>(List.of(alerta), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dataHora")), 1);

        when(alertRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Alert>>any(), any(Pageable.class))).thenReturn(page);

        Page<AlertResponseDTO> response = alertService.buscarAlertas(new FiltersAlertsDto(null, null, null, null, null, null), PageRequest.of(0, 10));

        assertEquals(1, response.getTotalElements());
        assertEquals(1L, response.getContent().get(0).id());
        assertEquals(TipoAlerta.DUPLICATE, response.getContent().get(0).tipoAlerta());
    }

    @Test
    void deveCriarAlertaDuplicado_quandoTimePunchForDuplicado() {
        TimePunch timePunch = criarTimePunch(35L, TipoTimePunch.CHECK_IN);
        TimePunch ultimoPonto = criarTimePunch(34L, TipoTimePunch.CHECK_IN);

        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> {
            Alert alert = invocation.getArgument(0);
            alert.setId(1L);
            return alert;
        });

        AlertResponseDTO response = alertService.criarAlertaDuplicado(timePunch, ultimoPonto);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(TipoAlerta.DUPLICATE, response.tipoAlerta());
        assertEquals(TipoTimePunch.CHECK_IN, response.tipoTimePunch());
        assertEquals(35L, response.timePunchId());
        assertEquals(7L, response.employeeId());
        assertEquals("Matheus", response.employeeName());
        assertFalse(response.resolvido());
        assertNull(response.observacaoAdmin());
        assertNull(response.resolvidoEm());
        assertTrue(response.message().contains("ponto 35"));
        assertTrue(response.message().contains("ponto 34"));

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());
        Alert salvo = captor.getValue();
        assertEquals(TipoAlerta.DUPLICATE, salvo.getTipoAlerta());
        assertEquals(timePunch, salvo.getTimePunch());
        assertEquals(Boolean.FALSE, salvo.getResolvido());
        assertEquals(timePunch.getDataHora(), salvo.getDataHora());
    }

    @Test
    void deveCriarAlertaDistanciaIncompativel_quandoForaDoRaio() {
        TimePunch timePunch = criarTimePunch(40L, TipoTimePunch.CHECK_OUT);

        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> {
            Alert alert = invocation.getArgument(0);
            alert.setId(2L);
            return alert;
        });

        AlertResponseDTO response = alertService.criarAlertaDistanciaIncompativel(timePunch);

        assertNotNull(response);
        assertEquals(2L, response.id());
        assertEquals(TipoAlerta.OUT_OF_RANGE, response.tipoAlerta());
        assertEquals(TipoTimePunch.CHECK_OUT, response.tipoTimePunch());
        assertEquals(40L, response.timePunchId());
        assertEquals(7L, response.employeeId());
        assertEquals("Matheus", response.employeeName());
        assertFalse(response.resolvido());
        assertNull(response.observacaoAdmin());
        assertNull(response.resolvidoEm());
        assertTrue(response.message().contains("fora do raio permitido"));

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());
        Alert salvo = captor.getValue();
        assertEquals(TipoAlerta.OUT_OF_RANGE, salvo.getTipoAlerta());
        assertEquals(timePunch, salvo.getTimePunch());
        assertEquals(Boolean.FALSE, salvo.getResolvido());
    }

    @Test
    void deveEditarAlerta_MarcandoComoResolvido() {
        TimePunch timePunch = criarTimePunch(35L, TipoTimePunch.CHECK_IN);
        Alert existente = criarAlertComTimePunch(10L, timePunch);
        AlertEditRequestDTO request = new AlertEditRequestDTO(true, null);

        when(alertRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AlertResponseDTO response = alertService.editarAlerta(request, 10L);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertTrue(response.resolvido());
        assertNotNull(response.resolvidoEm());
        assertNull(response.observacaoAdmin());
        assertEquals(35L, response.timePunchId());
        assertEquals(7L, response.employeeId());
        assertEquals("Matheus", response.employeeName());

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());
        Alert salvo = captor.getValue();
        assertEquals(Boolean.TRUE, salvo.getResolvido());
        assertNotNull(salvo.getResolvidoEm());
    }

    @Test
    void deveEditarAlerta_ApenasAtualizandoObservacao() {
        TimePunch timePunch = criarTimePunch(35L, TipoTimePunch.CHECK_IN);
        Alert existente = criarAlertComTimePunch(11L, timePunch);
        AlertEditRequestDTO request = new AlertEditRequestDTO(null, "Revisado pelo admin");

        when(alertRepository.findById(11L)).thenReturn(Optional.of(existente));
        when(alertRepository.save(any(Alert.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AlertResponseDTO response = alertService.editarAlerta(request, 11L);

        assertNotNull(response);
        assertEquals(11L, response.id());
        assertFalse(response.resolvido());
        assertEquals("Revisado pelo admin", response.observacaoAdmin());
        assertNull(response.resolvidoEm());

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());
        Alert salvo = captor.getValue();
        assertEquals("Revisado pelo admin", salvo.getObservacaoAdmin());
        assertEquals(Boolean.FALSE, salvo.getResolvido());
        assertNull(salvo.getResolvidoEm());
    }

    @Test
    void deveLancarBadRequest_quandoRequestNaoTiverCamposParaAtualizar() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> alertService.editarAlerta(new AlertEditRequestDTO(null, null), 1L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Nenhum campo para atualizar", exception.getReason());
        verifyNoInteractions(alertRepository);
    }

    @Test
    void deveLancarResourceNotFound_quandoAlertaNaoExistir() {
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> alertService.editarAlerta(new AlertEditRequestDTO(true, "ok"), 99L));

        verify(alertRepository).findById(99L);
        verify(alertRepository, never()).save(any());
    }

    private Alert criarAlertComTimePunch(Long id, TimePunch timePunch) {
        return Alert.builder()
                .id(id)
                .version(1L)
                .timePunch(timePunch)
                .tipoAlerta(TipoAlerta.DUPLICATE)
                .mensagem("Mensagem")
                .resolvido(false)
                .observacaoAdmin(null)
                .resolvidoEm(null)
                .dataHora(timePunch.getDataHora())
                .build();
    }

    private TimePunch criarTimePunch(Long id, TipoTimePunch tipoTimePunch) {
        Workplace workplace = Workplace.builder()
                .id(1L)
                .nome("Matriz")
                .logradouro("Rua A")
                .cidade("São Paulo")
                .estado("SP")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .raioMetros(150.0)
                .build();

        Users user = Users.builder()
                .id(7L)
                .login("matheus")
                .password("hash")
                .tipoUsuario(com.facilitahcm.smartcheck_hcm.enums.TipoUsuario.EMPLOYEE)
                .build();

        Employee employee = Employee.builder()
                .id(7L)
                .nome("Matheus")
                .cargo("Desenvolvedor")
                .workplace(workplace)
                .user(user)
                .tipoTrabalho(TipoTrabalho.PRESENCIAL)
                .build();

        return TimePunch.builder()
                .id(id)
                .employee(employee)
                .tipoTimePunch(tipoTimePunch)
                .dataHora(LocalDateTime.of(2026, 5, 15, 8, 30))
                .latitude(-23.5505)
                .longitude(-46.6333)
                .build();
    }
}




