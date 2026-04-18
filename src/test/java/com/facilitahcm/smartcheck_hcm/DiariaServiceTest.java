package com.facilitahcm.smartcheck_hcm;

import com.facilitahcm.smartcheck_hcm.enums.ModoDiaria;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.models.Diaria;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.repositories.DiariaRepository;
import com.facilitahcm.smartcheck_hcm.services.DiariaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiariaServiceTest {

    @Mock
    private DiariaRepository diariaRepository;

    @InjectMocks
    private DiariaService diariaService;

    @Test
    void deveRetornarTrue_quandoTipoTrabalhoPresencial() {
        Employee employee = Employee.builder().id(1L).tipoTrabalho(TipoTrabalho.PRESENCIAL).build();

        boolean resultado = diariaService.deveEstarNoEscritorio(employee, LocalDate.now());

        assertTrue(resultado);
        verifyNoInteractions(diariaRepository);
    }

    @Test
    void deveRetornarFalse_quandoTipoTrabalhoHomeOffice() {
        Employee employee = Employee.builder().id(2L).tipoTrabalho(TipoTrabalho.HOME_OFFICE).build();

        boolean resultado = diariaService.deveEstarNoEscritorio(employee, LocalDate.now());

        assertFalse(resultado);
        verifyNoInteractions(diariaRepository);
    }

    @Test
    void deveRetornarTrue_quandoHibridoComDiariaEscritorio() {
        Employee employee = Employee.builder().id(3L).tipoTrabalho(TipoTrabalho.HIBRIDO).build();
        LocalDate data = LocalDate.now();
        Diaria diaria = Diaria.builder().modoDiaria(ModoDiaria.ESCRITORIO).build();

        when(diariaRepository.findDiariaByDataAndEmployee_Id(data, 3L)).thenReturn(Optional.of(diaria));

        boolean resultado = diariaService.deveEstarNoEscritorio(employee, data);

        assertTrue(resultado);
        verify(diariaRepository).findDiariaByDataAndEmployee_Id(data, 3L);
    }

    @Test
    void deveRetornarFalse_quandoHibridoComDiariaRemota() {
        Employee employee = Employee.builder().id(4L).tipoTrabalho(TipoTrabalho.HIBRIDO).build();
        LocalDate data = LocalDate.now();
        Diaria diaria = Diaria.builder().modoDiaria(ModoDiaria.REMOTO).build();

        when(diariaRepository.findDiariaByDataAndEmployee_Id(data, 4L)).thenReturn(Optional.of(diaria));

        boolean resultado = diariaService.deveEstarNoEscritorio(employee, data);

        assertFalse(resultado);
        verify(diariaRepository).findDiariaByDataAndEmployee_Id(data, 4L);
    }

    @Test
    void deveRetornarFalse_quandoHibridoSemDiaria() {
        Employee employee = Employee.builder().id(5L).tipoTrabalho(TipoTrabalho.HIBRIDO).build();
        LocalDate data = LocalDate.now();

        when(diariaRepository.findDiariaByDataAndEmployee_Id(data, 5L)).thenReturn(Optional.empty());

        boolean resultado = diariaService.deveEstarNoEscritorio(employee, data);

        assertFalse(resultado);
        verify(diariaRepository).findDiariaByDataAndEmployee_Id(data, 5L);
    }
}

