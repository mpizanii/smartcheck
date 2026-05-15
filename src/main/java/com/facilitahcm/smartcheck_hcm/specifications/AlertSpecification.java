package com.facilitahcm.smartcheck_hcm.specifications;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersAlertsDto;
import com.facilitahcm.smartcheck_hcm.enums.TipoAlerta;
import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;
import com.facilitahcm.smartcheck_hcm.models.Alert;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AlertSpecification {

    public static Specification<Alert> comFiltros(FiltersAlertsDto filters) {
        return Specification
                .where(porEmployee(filters.employeeId()))
                .and(porTipoAlerta(filters.tipoAlerta()))
                .and(porDataHoraInicio(filters.dataHoraInicio()))
                .and(porDataHoraFim(filters.dataHoraFim()))
                .and(porTipoTimePunch(filters.tipoTimePunch()))
                .and(porStatus(filters.resolvido()));
    }

    private static Specification<Alert> porEmployee(Long employeeId) {
        return (root, query, cb) -> employeeId == null ? null
                : cb.equal(root.get("timePunch").get("employee").get("id"), employeeId);
    }

    private static Specification<Alert> porTipoAlerta(TipoAlerta tipoAlerta) {
        return (root, query, cb) -> tipoAlerta == null ? null
                : cb.equal(root.get("tipoAlerta"), tipoAlerta);
    }

    private static Specification<Alert> porDataHoraInicio(LocalDateTime dataHoraInicio) {
        return (root, query, cb) -> dataHoraInicio == null ? null
                : cb.greaterThanOrEqualTo(root.get("dataHora"), dataHoraInicio);
    }

    private static Specification<Alert> porDataHoraFim(LocalDateTime dataHoraFim) {
        return (root, query, cb) -> dataHoraFim == null ? null
                : cb.lessThanOrEqualTo(root.get("dataHora"), dataHoraFim);
    }

    private static Specification<Alert> porTipoTimePunch(TipoTimePunch tipoTimePunch) {
        return (root, query, cb) -> tipoTimePunch == null ? null
                : cb.equal(root.get("timePunch").get("tipoTimePunch"), tipoTimePunch);
    }

    private static Specification<Alert> porStatus(Boolean status) {
        return (root, query, cb) -> status == null ? null
                : (status ? cb.isTrue(root.get("resolvido")) : cb.isFalse(root.get("resolvido")));
    }
}
