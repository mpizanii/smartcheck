package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersWorkplaceDto;
import com.facilitahcm.smartcheck_hcm.dtos.WorkplaceRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.WorkplaceResponseDTO;
import com.facilitahcm.smartcheck_hcm.exceptions.BusinessException;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.WorkplaceRepository;
import com.facilitahcm.smartcheck_hcm.specifications.WorkplaceSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkplaceService {
    private final WorkplaceRepository workplaceRepository;

    @Autowired
    public WorkplaceService(WorkplaceRepository workplaceRepository){
        this.workplaceRepository = workplaceRepository;
    }

    public WorkplaceResponseDTO criarUnidade(WorkplaceRequestDTO dto){
        if (workplaceRepository.existsByNome(dto.nome())) {
            throw new BusinessException("Já existe uma unidade com o nome: " + dto.nome());
        }

        Workplace workplace = Workplace.builder()
                .nome(dto.nome())
                .logradouro(dto.logradouro())
                .cidade(dto.cidade())
                .estado(dto.estado())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .raioMetros(dto.raio())
                .build();

        Workplace saved = workplaceRepository.save(workplace);

        return converterParaResponseDTO(saved);
    }

    public Page<WorkplaceResponseDTO> buscarUnidades(FiltersWorkplaceDto filters, Pageable pageable){
        Page<Workplace> workplaces = workplaceRepository.findAll(WorkplaceSpecification.comFiltros(filters), pageable);

        return workplaces.map(this::converterParaResponseDTO);
    }

    private WorkplaceResponseDTO converterParaResponseDTO(Workplace workplace){
        return new WorkplaceResponseDTO(
            workplace.getId(),
            workplace.getNome(),
            workplace.getLatitude(),
            workplace.getLongitude(),
            workplace.getCidade(),
            workplace.getLogradouro(),
            workplace.getEstado(),
            workplace.getRaioMetros()
        );
    }
}
