package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.enums.ModoDiaria;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.models.Diaria;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.repositories.DiariaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DiariaService {
    private final DiariaRepository diariaRepository;

    public DiariaService(DiariaRepository diariaRepository) {
        this.diariaRepository = diariaRepository;
    }

    public boolean deveEstarNoEscritorio(Employee employee, LocalDate data) {
        if (employee.getTipoTrabalho() == TipoTrabalho.PRESENCIAL) {;
            return true;
        } else if (employee.getTipoTrabalho() == TipoTrabalho.HIBRIDO) {
            Optional<Diaria> diaria = diariaRepository.findDiariaByDataAndEmployee_Id(data, employee.getId());
            System.out.println("Diaria encontrada: " + diaria);
            if (diaria.isPresent()) {
                return diaria.get().getModoDiaria() == ModoDiaria.ESCRITORIO;
            }
        }

        return false;
    }
}
