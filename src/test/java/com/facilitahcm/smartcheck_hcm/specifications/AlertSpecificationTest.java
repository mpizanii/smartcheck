package com.facilitahcm.smartcheck_hcm.specifications;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersAlertsDto;
import com.facilitahcm.smartcheck_hcm.enums.TipoAlerta;
import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.models.Alert;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.models.TimePunch;
import com.facilitahcm.smartcheck_hcm.models.Users;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.AlertRepository;
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
class AlertSpecificationTest {

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private TimePunchRepository timePunchRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    void deveFiltrarPorEmployeeETipoTimePunch() {
        Employee employee1 = criarEmployee("matheus", "Matheus");
        Employee employee2 = criarEmployee("maria", "Maria");

        TimePunch timePunch1 = timePunchRepository.save(criarTimePunch(employee1, TipoTimePunch.CHECK_IN, LocalDateTime.of(2026, 4, 22, 8, 0), -23.55, -46.63));
        TimePunch timePunch2 = timePunchRepository.save(criarTimePunch(employee1, TipoTimePunch.CHECK_OUT, LocalDateTime.of(2026, 4, 22, 17, 0), -23.55, -46.63));
        TimePunch timePunch3 = timePunchRepository.save(criarTimePunch(employee2, TipoTimePunch.CHECK_IN, LocalDateTime.of(2026, 4, 22, 9, 0), -23.60, -46.60));

        alertRepository.save(criarAlert(timePunch1, TipoAlerta.DUPLICATE, LocalDateTime.of(2026, 4, 22, 8, 5), false, "Alerta 1"));
        alertRepository.save(criarAlert(timePunch2, TipoAlerta.OUT_OF_RANGE, LocalDateTime.of(2026, 4, 22, 17, 5), false, "Alerta 2"));
        alertRepository.save(criarAlert(timePunch3, TipoAlerta.DUPLICATE, LocalDateTime.of(2026, 4, 22, 9, 5), false, "Alerta 3"));

        Page<Alert> page = alertRepository.findAll(
                AlertSpecification.comFiltros(new FiltersAlertsDto(employee1.getId(), null, null, null, TipoTimePunch.CHECK_OUT, false)),
                PageRequest.of(0, 10, Sort.by("id").ascending())
        );

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals(TipoTimePunch.CHECK_OUT, page.getContent().get(0).getTimePunch().getTipoTimePunch());
        assertEquals(employee1.getId(), page.getContent().get(0).getTimePunch().getEmployee().getId());
    }

    @Test
    void deveFiltrarPorPeriodoEPaginar() {
        Employee employee = criarEmployee("matheus", "Matheus");

        TimePunch timePunch1 = timePunchRepository.save(criarTimePunch(employee, TipoTimePunch.CHECK_IN, LocalDateTime.of(2026, 4, 22, 8, 0), -23.55, -46.63));
        TimePunch timePunch2 = timePunchRepository.save(criarTimePunch(employee, TipoTimePunch.CHECK_OUT, LocalDateTime.of(2026, 4, 22, 17, 0), -23.55, -46.63));

        alertRepository.save(criarAlert(timePunch1, TipoAlerta.DUPLICATE, LocalDateTime.of(2026, 4, 22, 8, 5), false, "Alerta 1"));
        alertRepository.save(criarAlert(timePunch2, TipoAlerta.OUT_OF_RANGE, LocalDateTime.of(2026, 4, 22, 17, 5), false, "Alerta 2"));

        Page<Alert> page = alertRepository.findAll(
                AlertSpecification.comFiltros(new FiltersAlertsDto(null, null, LocalDateTime.of(2026, 4, 22, 8, 0), LocalDateTime.of(2026, 4, 22, 18, 0), null, false)),
                PageRequest.of(1, 1, Sort.by("id").ascending())
        );

        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals("Alerta 2", page.getContent().get(0).getMensagem());
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

    private TimePunch criarTimePunch(Employee employee, TipoTimePunch tipoTimePunch, LocalDateTime dataHora, Double latitude, Double longitude) {
        return TimePunch.builder()
                .employee(employee)
                .tipoTimePunch(tipoTimePunch)
                .dataHora(dataHora)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    private Alert criarAlert(TimePunch timePunch, TipoAlerta tipoAlerta, LocalDateTime dataHora, Boolean resolvido, String mensagem) {
        return Alert.builder()
                .timePunch(timePunch)
                .tipoAlerta(tipoAlerta)
                .dataHora(dataHora)
                .resolvido(resolvido)
                .mensagem(mensagem)
                .build();
    }
}


