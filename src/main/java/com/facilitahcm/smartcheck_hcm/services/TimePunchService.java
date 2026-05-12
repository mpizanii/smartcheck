package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.TimePunchRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.TimePunchResponseDTO;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import com.facilitahcm.smartcheck_hcm.repositories.TimePunchRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TimePunchService {
    private final TimePunchRepository timePunchRepository;
    private final AlertService alertService;
    private final EmployeeService employeeService;
    private final DiariaService diariaService;
    private final GeolocationService geolocationService;

    @Autowired
    public TimePunchService(TimePunchRepository timePunchRepository, AlertService alertService, EmployeeService employeeService, DiariaService diariaService, GeolocationService geolocationService) {
        this.timePunchRepository = timePunchRepository;
        this.alertService = alertService;
        this.employeeService = employeeService;
        this.diariaService = diariaService;
        this.geolocationService = geolocationService;
    }

    @Transactional
    public TimePunchResponseDTO baterPonto(TimePunchRequestDTO timePunchRequest) {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();

        Employee employee = employeeService.buscarEmployeePorLogin(login);
        Optional<TimePunch> ultimoPonto = timePunchRepository.findTopByEmployee_IdOrderByDataHoraDesc(employee.getId());
        LocalDateTime dataHoraAtual = LocalDateTime.now();

        TimePunch timePunch = TimePunch.builder()
            .employee(employee)
            .tipoTimePunch(timePunchRequest.tipoTimePunch())
            .dataHora(dataHoraAtual)
            .latitude(timePunchRequest.latitude())
            .longitude(timePunchRequest.longitude())
            .build();

        TimePunch saved = timePunchRepository.save(timePunch);

        if (ultimoPonto.isPresent() && ultimoPonto.get().getDataHora().isAfter(timePunch.getDataHora().minusMinutes(5))) {
            alertService.criarAlertaDuplicado(timePunch ,ultimoPonto.get());
        }

        if (diariaService.deveEstarNoEscritorio(employee, dataHoraAtual.toLocalDate())) {
            if (geolocationService.isForaDoRaio(employee.getWorkplace().getLatitude(), employee.getWorkplace().getLongitude(), timePunch.getLatitude(), timePunch.getLongitude(), employee.getWorkplace().getRaioMetros())) {
                alertService.criarAlertaDistanciaIncompativel(timePunch);
            }
        }

        return converterParaDto(saved);
    }

    public Page<TimePunchResponseDTO> buscarPontos(
            Long employeeId,
            LocalDateTime dataHoraInicio,
            LocalDateTime dataHoraFim,
            Pageable pageable
    ) {
        Page<TimePunch> timePunches = timePunchRepository.buscarPontos(employeeId, dataHoraInicio, dataHoraFim, pageable);

        return timePunches.map(this::converterParaDto);
    }

    private TimePunchResponseDTO converterParaDto(TimePunch timePunch) {
        return new TimePunchResponseDTO(
            timePunch.getId(),
            timePunch.getEmployee().getId(),
            timePunch.getEmployee().getNome(),
            timePunch.getTipoTimePunch(),
            timePunch.getDataHora(),
            timePunch.getLatitude(),
            timePunch.getLongitude()
        );
    }
}
