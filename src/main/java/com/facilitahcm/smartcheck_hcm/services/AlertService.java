package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.*;
import com.facilitahcm.smartcheck_hcm.enums.TipoAlerta;
import com.facilitahcm.smartcheck_hcm.exceptions.ResourceNotFoundException;
import com.facilitahcm.smartcheck_hcm.models.Alert;
import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import com.facilitahcm.smartcheck_hcm.repositories.AlertRepository;
import com.facilitahcm.smartcheck_hcm.specifications.AlertSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlertService {
    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public AlertResponseDTO criarAlertaDuplicado(TimePunch timePunch, TimePunch ultimoPonto) {
        String message = "Ponto duplicado entre o ponto " + timePunch.getId() + " e o ponto " + ultimoPonto.getId() + " do funcionário " + timePunch.getEmployee().getNome();

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

    public Page<AlertResponseDTO> buscarAlertas(FiltersAlertsDto filters, Pageable pageable) {
        Page<Alert> alerts = alertRepository.findAll(AlertSpecification.comFiltros(filters), pageable);

        return alerts.map(this::converterParaDto);
    }

    @Transactional
    public AlertResponseDTO editarAlerta(AlertEditRequestDTO alertEditRequestDTO, Long id) {
        if (alertEditRequestDTO.resolvido() == null && alertEditRequestDTO.observacao() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhum campo para atualizar");
        }

        Alert alert = alertRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado com id: " + id));

        if (alertEditRequestDTO.resolvido() != null && alertEditRequestDTO.resolvido()) {
            alert.setResolvido(true);
            alert.setResolvidoEm(LocalDateTime.now());
        } else if (alertEditRequestDTO.resolvido() != null && !alertEditRequestDTO.resolvido()) {
            alert.setResolvido(false);
            alert.setResolvidoEm(null);
        }
        if (alertEditRequestDTO.observacao() != null) {
            alert.setObservacaoAdmin(alertEditRequestDTO.observacao());
        }

        Alert updated = alertRepository.save(alert);

        return converterParaDto(updated);
    }

    @Transactional
    public AlertBatchEditResponseDTO editarAlertasLote(AlertBatchEditRequestDTO alertBatchEditRequestDTO) {
        List<Long> ids = alertBatchEditRequestDTO.ids().stream().distinct().toList();
        List<Alert> alerts = alertRepository.findAllById(ids);

        HashMap<Long, String> erros = new HashMap<>();
        Set<Long> idsEncontrados = alerts.stream().map(Alert::getId).collect(Collectors.toSet());
        String mensagemAlertaNãoEncontrado = "Alerta não encontrado";

        for (Long id : ids) {
            if (!idsEncontrados.contains(id)) {
                erros.put(id, mensagemAlertaNãoEncontrado);
            }
        }

        Boolean novoEstado = Boolean.TRUE.equals(alertBatchEditRequestDTO.resolvido());
        String mensagemAlertaEstadoIgual = novoEstado
                ? "Alerta já estava como resolvido"
                : "Alerta já estava como não resolvido";

        LocalDateTime now = novoEstado ? LocalDateTime.now() : null;

        Integer totalAtualizados = 0;
        List<Alert> alertasParaSalvar = new ArrayList<>();

        for (Alert alert : alerts) {
            Boolean estadoAtual = Boolean.TRUE.equals(alert.getResolvido());

            if (estadoAtual == novoEstado) {
                erros.put(alert.getId(), mensagemAlertaEstadoIgual);
                continue;
            }

            alert.setResolvido(novoEstado);
            alert.setObservacaoAdmin(alertBatchEditRequestDTO.observacao());
            alert.setResolvidoEm(now);

            alertasParaSalvar.add(alert);
            totalAtualizados++;
        }

        if (!alertasParaSalvar.isEmpty()) {
            alertRepository.saveAll(alertasParaSalvar);
        }

        return new AlertBatchEditResponseDTO(
                ids.size(),
                totalAtualizados,
                erros
        );

    }

    private AlertResponseDTO converterParaDto(Alert alert) {
        return new AlertResponseDTO(
                alert.getId(),
                alert.getTipoAlerta(),
                alert.getMensagem(),
                alert.getTimePunch().getDataHora(),
                alert.getTimePunch().getId(),
                alert.getTimePunch().getTipoTimePunch(),
                alert.getResolvido(),
                alert.getObservacaoAdmin(),
                alert.getResolvidoEm(),
                alert.getTimePunch().getEmployee().getId(),
                alert.getTimePunch().getEmployee().getNome()
        );
    }
}
