package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.WorkplaceRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.WorkplaceResponseDTO;
import com.facilitahcm.smartcheck_hcm.exceptions.BusinessException;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .raioMetros(dto.raio())
                .build();

        Workplace saved = workplaceRepository.save(workplace);

        return converterParaResponseDTO(saved);
    }

    private WorkplaceResponseDTO converterParaResponseDTO(Workplace workplace){
        return new WorkplaceResponseDTO(
            workplace.getId(),
            workplace.getNome(),
            workplace.getLatitude(),
            workplace.getLongitude(),
            workplace.getRaioMetros()
        );
    }
}
