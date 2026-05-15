package com.facilitahcm.smartcheck_hcm.specifications;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersTimePunchDto;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import com.facilitahcm.smartcheck_hcm.models.Users;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.EmployeeRepository;
import com.facilitahcm.smartcheck_hcm.repositories.TimePunchRepository;
import com.facilitahcm.smartcheck_hcm.repositories.UsersRepository;
import com.facilitahcm.smartcheck_hcm.repositories.WorkplaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class TimePunchSpecificationTest {

    @Autowired
    private TimePunchRepository timePunchRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    void deveFiltrarPorEmployeeIdEIntervaloDeDataHora() {
        Employee employee1 = criarEmployee("matheus", "Matheus");
        Employee employee2 = criarEmployee("maria", "Maria");

        timePunchRepository.save(criarTimePunch(null, employee1, TipoTimePunch.CHECK_IN, LocalDateTime.of(2026, 4, 22, 8, 0), -23.55, -46.63));
        timePunchRepository.save(criarTimePunch(null, employee1, TipoTimePunch.CHECK_OUT, LocalDateTime.of(2026, 4, 22, 17, 0), -23.55, -46.63));
        timePunchRepository.save(criarTimePunch(null, employee2, TipoTimePunch.CHECK_IN, LocalDateTime.of(2026, 4, 22, 9, 0), -23.60, -46.60));

        Page<TimePunch> page = timePunchRepository.findAll(
                TimePunchSpecification.comFiltros(new FiltersTimePunchDto(employee1.getId(), LocalDateTime.of(2026, 4, 22, 7, 0), LocalDateTime.of(2026, 4, 22, 12, 0))),
                PageRequest.of(0, 10, Sort.by("dataHora").ascending())
        );

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals(employee1.getId(), page.getContent().get(0).getEmployee().getId());
        assertEquals(TipoTimePunch.CHECK_IN, page.getContent().get(0).getTipoTimePunch());
    }

    @Test
    void deveRespeitarPaginacaoComFiltroNulo() {
        Employee employee = criarEmployee("matheus", "Matheus");

        timePunchRepository.save(criarTimePunch(null, employee, TipoTimePunch.CHECK_IN, LocalDateTime.of(2026, 4, 22, 8, 0), -23.55, -46.63));
        timePunchRepository.save(criarTimePunch(null, employee, TipoTimePunch.CHECK_OUT, LocalDateTime.of(2026, 4, 22, 17, 0), -23.55, -46.63));

        Page<TimePunch> page = timePunchRepository.findAll(
                TimePunchSpecification.comFiltros(new FiltersTimePunchDto(null, null, null)),
                PageRequest.of(1, 1, Sort.by("id").ascending())
        );

        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals(TipoTimePunch.CHECK_OUT, page.getContent().get(0).getTipoTimePunch());
    }

    private Employee criarEmployee(String login, String nome) {
        Workplace workplace = workplaceRepository.save(Workplace.builder()
                .nome("Matriz " + nome)
                .logradouro("Rua A")
                .cidade("Sao Paulo")
                .estado("SP")
                .latitude(-23.55)
                .longitude(-46.63)
                .raioMetros(150.0)
                .build());

        Users user = usersRepository.save(Users.builder()
                .login(login)
                .password("123456")
                .tipoUsuario(TipoUsuario.EMPLOYEE)
                .build());

        return employeeRepository.save(Employee.builder()
                .nome(nome)
                .cargo("Desenvolvedor")
                .workplace(workplace)
                .user(user)
                .tipoTrabalho(TipoTrabalho.PRESENCIAL)
                .build());
    }

    private TimePunch criarTimePunch(Long id, Employee employee, TipoTimePunch tipoTimePunch, LocalDateTime dataHora, Double latitude, Double longitude) {
        return TimePunch.builder()
                .id(id)
                .employee(employee)
                .tipoTimePunch(tipoTimePunch)
                .dataHora(dataHora)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}


