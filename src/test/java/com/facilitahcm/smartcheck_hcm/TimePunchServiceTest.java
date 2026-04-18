package com.facilitahcm.smartcheck_hcm;

import com.facilitahcm.smartcheck_hcm.dtos.TimePunchRequestDTO;
import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.TimePunchRepository;
import com.facilitahcm.smartcheck_hcm.services.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimePunchServiceTest {

    @Mock
    private TimePunchRepository timePunchRepository;
    @Mock
    private AlertService alertService;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private DiariaService diariaService;
    @Mock
    private GeolocationService geolocationService;

    @InjectMocks
    private TimePunchService timePunchService;

    @Captor
    private ArgumentCaptor<TimePunch> timePunchCaptor;

    @Test
    void deveSalvarPontoSemAlertas_quandoNaoDuplicadoENaoForaDoRaio() {
        Long employeeId = 1L;
        Employee employee = criarEmployee(employeeId);

        TimePunchRequestDTO request = new TimePunchRequestDTO(
          employeeId, TipoTimePunch.CHECK_IN, -23.5505, -46.633
        );

        when(timePunchRepository.findTopByEmployee_IdOrderByDataHoraDesc(employeeId)).thenReturn(Optional.empty());
        when(employeeService.buscarEmployeePorId(employeeId)).thenReturn(employee);
        when(diariaService.deveEstarNoEscritorio(eq(employee), any())).thenReturn(false);

        when(timePunchRepository.save(any(TimePunch.class))).thenAnswer(invocation -> {
            TimePunch tp = invocation.getArgument(0);
            tp.setId(10L);
            return tp;
        });

        var response = timePunchService.baterPonto(request);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(employeeId, response.employeeId());
        assertEquals(TipoTimePunch.CHECK_IN, response.tipoTimePunch());

        verify(timePunchRepository).save(timePunchCaptor.capture());
        TimePunch salvo = timePunchCaptor.getValue();
        assertEquals(employee, salvo.getEmployee());
        assertNotNull(salvo.getDataHora());

        verify(alertService, never()).criarAlertaDuplicado(any(), any());
        verify(alertService, never()).criarAlertaDistanciaIncompativel(any());
    }


    @Test
    void deveGerarAlertaDuplicado_quandoUltimoPontoMenosde5Minutos() {
        Long employeeId = 1L;
        Employee employee = criarEmployee(employeeId);

        TimePunch ultimoPonto = TimePunch.builder()
                .id(99L)
                .employee(employee)
                .tipoTimePunch(TipoTimePunch.CHECK_IN)
                .dataHora(LocalDateTime.now().minusMinutes(2))
                .latitude(-23.5505)
                .longitude(-46.6333)
                .build();

        TimePunchRequestDTO request = new TimePunchRequestDTO(
                employeeId, TipoTimePunch.CHECK_IN, -23.5505, -46.633
        );

        when(timePunchRepository.findTopByEmployee_IdOrderByDataHoraDesc(employeeId)).thenReturn(Optional.of(ultimoPonto));
        when(employeeService.buscarEmployeePorId(employeeId)).thenReturn(employee);
        when(diariaService.deveEstarNoEscritorio(eq(employee), any())).thenReturn(false);

        when(timePunchRepository.save(any(TimePunch.class))).thenAnswer(invocation -> {
            TimePunch tp = invocation.getArgument(0);
            tp.setId(11L);
            return tp;
        });

        timePunchService.baterPonto(request);

        verify(alertService).criarAlertaDuplicado(any(TimePunch.class), eq(ultimoPonto));
    }

    @Test
    void deveGerarAlertaOutOfRange_quandoDeveEstarNoEscritorioEForaDoRaio() {
        Long employeeId = 1L;
        Employee employee = criarEmployee(employeeId);

        TimePunchRequestDTO request = new TimePunchRequestDTO(
                employeeId, TipoTimePunch.CHECK_IN, -23.5600, -46.6400
        );

        when(timePunchRepository.findTopByEmployee_IdOrderByDataHoraDesc(employeeId)).thenReturn(Optional.empty());
        when(employeeService.buscarEmployeePorId(employeeId)).thenReturn(employee);
        when(diariaService.deveEstarNoEscritorio(eq(employee), any())).thenReturn(true);
        when(geolocationService.isForaDoRaio(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(true);

        when(timePunchRepository.save(any(TimePunch.class))).thenAnswer(invocation -> {
            TimePunch tp = invocation.getArgument(0);
            tp.setId(12L);
            return tp;
        });

        timePunchService.baterPonto(request);

        verify(alertService).criarAlertaDistanciaIncompativel(any(TimePunch.class));
    }

    @Test
    void deveSalvarPontoSemAlertas_quandoUltimoPontoMaior5Minutos() {
        Long employeeId = 1L;
        Employee employee = criarEmployee(employeeId);

        TimePunch ultimoPonto = TimePunch.builder()
                .id(99L)
                .employee(employee)
                .tipoTimePunch(TipoTimePunch.CHECK_IN)
                .dataHora(LocalDateTime.now().minusMinutes(6))
                .latitude(-23.5505)
                .longitude(-46.6333)
                .build();

        TimePunchRequestDTO request = new TimePunchRequestDTO(
                employeeId, TipoTimePunch.CHECK_IN, -23.5505, -46.633
        );

        when(timePunchRepository.findTopByEmployee_IdOrderByDataHoraDesc(employeeId)).thenReturn(Optional.of(ultimoPonto));
        when(employeeService.buscarEmployeePorId(employeeId)).thenReturn(employee);
        when(diariaService.deveEstarNoEscritorio(eq(employee), any())).thenReturn(false);

        when(timePunchRepository.save(any(TimePunch.class))).thenAnswer(invocation -> {
            TimePunch tp = invocation.getArgument(0);
            tp.setId(13L);
            return tp;
        });

        timePunchService.baterPonto(request);

        verify(alertService, never()).criarAlertaDuplicado(any(TimePunch.class), eq(ultimoPonto));
    }

    @Test
    void naoDeveGerarAlertaDuplicado_quandoUltimoPontoExatamente5Minutos() {
        Long employeeId = 1L;
        Employee employee = criarEmployee(employeeId);

        TimePunch ultimoPonto = TimePunch.builder()
                .id(99L)
                .employee(employee)
                .tipoTimePunch(TipoTimePunch.CHECK_IN)
                .dataHora(LocalDateTime.now().minusMinutes(5))
                .latitude(-23.5505)
                .longitude(-46.6333)
                .build();

        TimePunchRequestDTO request = new TimePunchRequestDTO(
                employeeId, TipoTimePunch.CHECK_OUT, -23.5505, -46.633
        );

        when(timePunchRepository.findTopByEmployee_IdOrderByDataHoraDesc(employeeId)).thenReturn(Optional.of(ultimoPonto));
        when(employeeService.buscarEmployeePorId(employeeId)).thenReturn(employee);
        when(diariaService.deveEstarNoEscritorio(eq(employee), any())).thenReturn(false);
        when(timePunchRepository.save(any(TimePunch.class))).thenAnswer(invocation -> {
            TimePunch tp = invocation.getArgument(0);
            tp.setId(16L);
            return tp;
        });

        timePunchService.baterPonto(request);

        verify(alertService, never()).criarAlertaDuplicado(any(TimePunch.class), any(TimePunch.class));
    }

    @Test
    void deveSalvarPontoSemAlertas_quandoDeveEstarNoEscritorioEDentroDoRaio () {
        Long employeeId = 1L;
        Employee employee = criarEmployee(employeeId);

        TimePunchRequestDTO request = new TimePunchRequestDTO(
                employeeId, TipoTimePunch.CHECK_IN, -23.5505, -46.6333
        );

        when(timePunchRepository.findTopByEmployee_IdOrderByDataHoraDesc(employeeId)).thenReturn(Optional.empty());
        when(employeeService.buscarEmployeePorId(employeeId)).thenReturn(employee);
        when(diariaService.deveEstarNoEscritorio(eq(employee), any())).thenReturn(true);
        when(geolocationService.isForaDoRaio(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(false);

        when(timePunchRepository.save(any(TimePunch.class))).thenAnswer(invocation -> {
            TimePunch tp = invocation.getArgument(0);
            tp.setId(14L);
            return tp;
        });

        timePunchService.baterPonto(request);

        verify(alertService, never()).criarAlertaDistanciaIncompativel(any(TimePunch.class));
    }

    @Test
    void deveSalvarPontoSemAlertas_quandoEmployeeModoTrabalhoHibridoEDeveEstarNoEscritorioEDentroDoRaio () {
        Long employeeId = 1L;
        Employee employee = criarEmployeeModoTrabalhoHibrido(employeeId);

        TimePunchRequestDTO request = new TimePunchRequestDTO(
                employeeId, TipoTimePunch.CHECK_IN, -23.5505, -46.6333
        );

        when(timePunchRepository.findTopByEmployee_IdOrderByDataHoraDesc(employeeId)).thenReturn(Optional.empty());
        when(employeeService.buscarEmployeePorId(employeeId)).thenReturn(employee);
        when(diariaService.deveEstarNoEscritorio(eq(employee), any())).thenReturn(true);
        when(geolocationService.isForaDoRaio(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(false);

        when(timePunchRepository.save(any(TimePunch.class))).thenAnswer(invocation -> {
            TimePunch tp = invocation.getArgument(0);
            tp.setId(15L);
            return tp;
        });

        timePunchService.baterPonto(request);

        verify(alertService, never()).criarAlertaDistanciaIncompativel(any(TimePunch.class));
    }

    @Test
    void deveSalvarPontoSemAlertas_quandoEmployeeModoTrabalhoHibridoENaoDeveEstarNoEscritorio () {
        Long employeeId = 1L;
        Employee employee = criarEmployeeModoTrabalhoHibrido(employeeId);

        TimePunchRequestDTO request = new TimePunchRequestDTO(
                employeeId, TipoTimePunch.CHECK_IN, -50.906847, -87.172896
        );

        when(timePunchRepository.findTopByEmployee_IdOrderByDataHoraDesc(employeeId)).thenReturn(Optional.empty());
        when(employeeService.buscarEmployeePorId(employeeId)).thenReturn(employee);
        when(diariaService.deveEstarNoEscritorio(eq(employee), any())).thenReturn(false);

        when(timePunchRepository.save(any(TimePunch.class))).thenAnswer(invocation -> {
            TimePunch tp = invocation.getArgument(0);
            tp.setId(15L);
            return tp;
        });

        timePunchService.baterPonto(request);

        verify(alertService, never()).criarAlertaDistanciaIncompativel(any(TimePunch.class));
        verify(geolocationService, never()).isForaDoRaio(anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    private Employee criarEmployee(Long id) {
        Workplace workplace = Workplace.builder()
                .id(100L)
                .nome("Escritório Matriz")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .raioMetros(150.0)
                .build();

        return Employee.builder()
                .id(id)
                .nome("Matheus")
                .matricula("MAT-001")
                .cargo("Desenvolvedor Java")
                .workplace(workplace)
                .tipoTrabalho(TipoTrabalho.PRESENCIAL)
                .build();
    }

    private Employee criarEmployeeModoTrabalhoHibrido(Long id) {
        Workplace workplace = Workplace.builder()
                .id(101L)
                .nome("Escrritório Hibrido")
                .latitude(-23.5505)
                .longitude(-46.6333)
                .raioMetros(150.0)
                .build();

        return Employee.builder()
                .id(id)
                .nome("Matheus")
                .matricula("MAT-001")
                .cargo("Desenvolvedor Java")
                .workplace(workplace)
                .tipoTrabalho(TipoTrabalho.HIBRIDO)
                .build();
    }

}
