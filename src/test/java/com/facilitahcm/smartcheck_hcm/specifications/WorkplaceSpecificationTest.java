package com.facilitahcm.smartcheck_hcm.specifications;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersWorkplaceDto;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.WorkplaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class WorkplaceSpecificationTest {

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Test
    void deveFiltrarPorNomeCidadeEEstado() {
        workplaceRepository.save(criarWorkplace(null, "Matriz", "Rua A", "Sao Paulo", "SP", -23.55, -46.63, 150.0));
        workplaceRepository.save(criarWorkplace(null, "Filial", "Rua B", "Campinas", "SP", -22.90, -47.06, 100.0));

        Page<Workplace> page = workplaceRepository.findAll(
                WorkplaceSpecification.comFiltros(new FiltersWorkplaceDto(null, "mat", "sao", "sp")),
                PageRequest.of(0, 10, Sort.by("id").ascending())
        );

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals("Matriz", page.getContent().get(0).getNome());
        assertTrue(page.getContent().get(0).getCidade().equalsIgnoreCase("Sao Paulo"));
    }

    @Test
    void deveRespeitarPaginacaoComOrdenacaoPorId() {
        workplaceRepository.save(criarWorkplace(null, "Matriz", "Rua A", "Sao Paulo", "SP", -23.55, -46.63, 150.0));
        workplaceRepository.save(criarWorkplace(null, "Filial", "Rua B", "Campinas", "SP", -22.90, -47.06, 100.0));

        Page<Workplace> page = workplaceRepository.findAll(
                WorkplaceSpecification.comFiltros(new FiltersWorkplaceDto(null, null, null, null)),
                PageRequest.of(1, 1, Sort.by("id").ascending())
        );

        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals("Filial", page.getContent().get(0).getNome());
    }

    private Workplace criarWorkplace(Long id, String nome, String logradouro, String cidade, String estado, Double latitude, Double longitude, Double raioMetros) {
        return Workplace.builder()
                .id(id)
                .nome(nome)
                .logradouro(logradouro)
                .cidade(cidade)
                .estado(estado)
                .latitude(latitude)
                .longitude(longitude)
                .raioMetros(raioMetros)
                .build();
    }
}


