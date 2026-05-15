package com.facilitahcm.smartcheck_hcm.specifications;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersEmployeeDto;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {

    public static Specification<Employee> comFiltros(FiltersEmployeeDto filters) {
        return Specification
                .where(fetchWorkplaces())
                .and(porId(filters.id()))
                .and(porNome(filters.nome()))
                .and(porCargo(filters.cargo()))
                .and(porWorkplaceId(filters.workplaceId()))
                .and(porTipoTrabalho(filters.tipoTrabalho()));
    }

    private static Specification<Employee> fetchWorkplaces() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("workplace", JoinType.INNER);
            }
            return null;
        };
    }

    private static Specification<Employee> porId(Long id) {
        return (root, query, cb) -> id == null ? null
                : cb.equal(root.get("id"), id);
    }

    private static Specification<Employee> porNome(String nome) {
        return (root, query, cb) -> nome == null ? null
                : cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    private static Specification<Employee> porCargo(String cargo) {
        return (root, query, cb) -> cargo == null ? null
                : cb.like(cb.lower(root.get("cargo")), "%" + cargo.toLowerCase() + "%");
    }

    private static Specification<Employee> porWorkplaceId(Long workplaceId) {
        return (root, query, cb) -> workplaceId == null ? null
                : cb.equal(root.get("workplace").get("id"), workplaceId);
    }

    private static Specification<Employee> porTipoTrabalho(TipoTrabalho tipoTrabalho) {
        return (root, query, cb) -> tipoTrabalho == null ? null
                : cb.equal(root.get("tipoTrabalho"), tipoTrabalho);
    }
}
