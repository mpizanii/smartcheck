package com.facilitahcm.smartcheck_hcm.specifications;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersWorkplaceDto;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import org.springframework.data.jpa.domain.Specification;

public class WorkplaceSpecification {

    public static Specification<Workplace> comFiltros(FiltersWorkplaceDto filters) {
        return Specification
                .where(porId(filters.id()))
                .and(porNome(filters.nome()))
                .and(porCidade(filters.cidade()))
                .and(porEstado(filters.estado()));
    }

    private static Specification<Workplace> porId(Long id) {
        return (root, query, cb) -> id == null ? null
                : cb.equal(root.get("id"), id);
    }

    private static Specification<Workplace> porNome(String nome) {
        return (root, query, cb) -> nome == null ? null
                : cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%");
    }

    private static Specification<Workplace> porCidade(String cidade) {
        return (root, query, cb) -> cidade == null ? null
                : cb.like(cb.lower(root.get("cidade")), "%" + cidade.toLowerCase() + "%");
    }

    private static Specification<Workplace> porEstado(String estado) {
        return (root, query, cb) -> estado == null ? null
                : cb.like(cb.lower(root.get("estado")), "%" + estado.toLowerCase() + "%");
    }
}
