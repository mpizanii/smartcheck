package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.AlertResponseDTO;
import com.facilitahcm.smartcheck_hcm.enums.TipoAlerta;
import com.facilitahcm.smartcheck_hcm.models.Alert;
import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import com.facilitahcm.smartcheck_hcm.repositories.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public AlertResponseDTO criarAlertaDuplicado(TimePunch timePunch, TimePunch ultimoPonto) {
        String message = "Ponto duplicado entre o ponto" + timePunch.getId() + " e o ponto" + ultimoPonto.getId() + "do funcionário " + timePunch.getEmployee().getNome();

        Alert alert = Alert.builder()
                .timePunch(timePunch)
                .tipoAlerta(TipoAlerta.DUPLICATE)
                .mensagem(message)
                .resolvido(false)
                .dataHora(timePunch.getDataHora())
                .build();

        Alert saved = alertRepository.save(alert);

        return converterParaDto(saved);
    }

    public AlertResponseDTO criarAlertaDistanciaIncompativel(TimePunch timePunch) {
        String message = "Ponto fora do raio permitido para o funcionário " + timePunch.getEmployee().getNome();

        Alert alert = Alert.builder()
                .timePunch(timePunch)
                .tipoAlerta(TipoAlerta.OUT_OF_RANGE)
                .mensagem(message)
                .resolvido(false)
                .dataHora(timePunch.getDataHora())
                .build();

        Alert saved = alertRepository.save(alert);

        return converterParaDto(saved);
    }

    public List<AlertResponseDTO> buscarAlertas() {
        List<Alert> alerts = alertRepository.findAll();

        List<AlertResponseDTO> responseDTOS = alerts.stream()
                .map(this::converterParaDto)
                .toList();

        return responseDTOS;
    }

    private AlertResponseDTO converterParaDto(Alert alert) {
        return new AlertResponseDTO(
                alert.getId(),
                alert.getTipoAlerta(),
                alert.getMensagem(),
                alert.getTimePunch().getDataHora(),
                alert.getTimePunch().getId(),
                alert.getTimePunch().getTipoTimePunch(),
                alert.getTimePunch().getEmployee().getId(),
                alert.getTimePunch().getEmployee().getNome()
        );
    }
}
