package com.facilitahcm.smartcheck_hcm.specifications;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersTimePunchDto;
import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class TimePunchSpecification {

    public static Specification<TimePunch> comFiltros(FiltersTimePunchDto filters) {
        return Specification
                .where(porEmployee(filters.employeeId()))
                .and(porDataHoraInicio(filters.dataHoraInicio()))
                .and(porDataHoraFim(filters.dataHoraFim()));
    }

    private static Specification<TimePunch> porEmployee(Long employeeId) {
        return (root, query, cb) -> employeeId == null ? null
                : cb.equal(root.get("employee").get("id"), employeeId);
    }

    private static Specification<TimePunch> porDataHoraInicio(LocalDateTime dataHoraInicio) {
        return (root, query, cb) -> dataHoraInicio == null ? null
                : cb.greaterThanOrEqualTo(root.get("dataHora"), dataHoraInicio);
    }

    private static Specification<TimePunch> porDataHoraFim(LocalDateTime dataHoraFim) {
        return (root, query, cb) -> dataHoraFim == null ? null
                : cb.lessThanOrEqualTo(root.get("dataHora"), dataHoraFim);
    }

}
