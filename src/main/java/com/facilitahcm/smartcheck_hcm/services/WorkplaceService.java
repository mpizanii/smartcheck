package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.WorkplaceRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.WorkplaceResponseDTO;
import com.facilitahcm.smartcheck_hcm.exceptions.BusinessException;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.WorkplaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        double latitude = Double.parseDouble(dto.latitude());
        double longitude = Double.parseDouble(dto.longitude());

        Workplace workplace = Workplace.builder()
                .nome(dto.nome())
                .logradouro(dto.logradouro())
                .cidade(dto.cidade())
                .estado(dto.estado())
                .latitude(latitude)
                .longitude(longitude)
                .raioMetros(dto.raio())
                .build();

        Workplace saved = workplaceRepository.save(workplace);

        return converterParaResponseDTO(saved);
    }

    public List<WorkplaceResponseDTO> buscarUnidades(){
        List<Workplace> workplaces = workplaceRepository.findAll();

        List<WorkplaceResponseDTO> responseDTOs = workplaces.stream()
            .map(this::converterParaResponseDTO)
            .toList();

        return responseDTOs;
    }

    public WorkplaceResponseDTO buscarUnidadePorId(Long id) {
        Workplace workplace = workplaceRepository.findWorkplaceById(id)
            .orElseThrow(() -> new BusinessException("Unidade com id " + id + " não encontrada."));

        return converterParaResponseDTO(workplace);
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
