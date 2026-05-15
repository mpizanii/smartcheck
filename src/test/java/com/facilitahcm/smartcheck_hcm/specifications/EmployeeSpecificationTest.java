package com.facilitahcm.smartcheck_hcm.specifications;

import com.facilitahcm.smartcheck_hcm.dtos.FiltersEmployeeDto;
import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.models.Employee;
import com.facilitahcm.smartcheck_hcm.models.Users;
import com.facilitahcm.smartcheck_hcm.models.Workplace;
import com.facilitahcm.smartcheck_hcm.repositories.EmployeeRepository;
import com.facilitahcm.smartcheck_hcm.repositories.UsersRepository;
import com.facilitahcm.smartcheck_hcm.repositories.WorkplaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class EmployeeSpecificationTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkplaceRepository workplaceRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    void deveFiltrarPorWorkplaceETipoTrabalho() {
        Workplace matriz = workplaceRepository.save(criarWorkplace(null, "Matriz"));
        Workplace filial = workplaceRepository.save(criarWorkplace(null, "Filial"));

        Users user1 = usersRepository.save(criarUser(null, "matheus", "123456", TipoUsuario.EMPLOYEE));
        Users user2 = usersRepository.save(criarUser(null, "maria", "123456", TipoUsuario.EMPLOYEE));

        employeeRepository.save(criarEmployee(null, "Matheus", "Desenvolvedor", matriz, user1, TipoTrabalho.PRESENCIAL));
        employeeRepository.save(criarEmployee(null, "Maria", "Analista", filial, user2, TipoTrabalho.HOME_OFFICE));

        Page<Employee> page = employeeRepository.findAll(
                EmployeeSpecification.comFiltros(new FiltersEmployeeDto(null, null, null, matriz.getId(), TipoTrabalho.PRESENCIAL)),
                PageRequest.of(0, 10, Sort.by("id").ascending())
        );

        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals("Matheus", page.getContent().get(0).getNome());
        assertEquals(matriz.getId(), page.getContent().get(0).getWorkplace().getId());
    }

    @Test
    void deveRespeitarPaginacaoComFiltroNulo() {
        Workplace matriz = workplaceRepository.save(criarWorkplace(null, "Matriz"));
        Workplace filial = workplaceRepository.save(criarWorkplace(null, "Filial"));

        Users user1 = usersRepository.save(criarUser(null, "matheus", "123456", TipoUsuario.EMPLOYEE));
        Users user2 = usersRepository.save(criarUser(null, "maria", "123456", TipoUsuario.EMPLOYEE));

        employeeRepository.save(criarEmployee(null, "Matheus", "Desenvolvedor", matriz, user1, TipoTrabalho.PRESENCIAL));
        employeeRepository.save(criarEmployee(null, "Maria", "Analista", filial, user2, TipoTrabalho.HOME_OFFICE));

        Page<Employee> page = employeeRepository.findAll(
                EmployeeSpecification.comFiltros(new FiltersEmployeeDto(null, null, null, null, null)),
                PageRequest.of(1, 1, Sort.by("id").ascending())
        );

        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getContent().size());
        assertEquals("Maria", page.getContent().get(0).getNome());
    }

    private Workplace criarWorkplace(Long id, String nome) {
        return Workplace.builder()
                .id(id)
                .nome(nome)
                .logradouro("Rua " + nome)
                .cidade("Sao Paulo")
                .estado("SP")
                .latitude(-23.55)
                .longitude(-46.63)
                .raioMetros(150.0)
                .build();
    }

    private Users criarUser(Long id, String login, String password, TipoUsuario tipoUsuario) {
        return Users.builder()
                .id(id)
                .login(login)
                .password(password)
                .tipoUsuario(tipoUsuario)
                .build();
    }

    private Employee criarEmployee(Long id, String nome, String cargo, Workplace workplace, Users user, TipoTrabalho tipoTrabalho) {
        return Employee.builder()
                .id(id)
                .nome(nome)
                .cargo(cargo)
                .workplace(workplace)
                .user(user)
                .tipoTrabalho(tipoTrabalho)
                .build();
    }
}


