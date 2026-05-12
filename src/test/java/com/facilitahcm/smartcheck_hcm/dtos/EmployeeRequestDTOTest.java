package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoTrabalho;
import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.validation.ValidationTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class EmployeeRequestDTOTest extends ValidationTestSupport {
    private static final String NOME = "João Silva";
    private static final String CARGO = "Analista";
    private static final Long WORKPLACE_ID = 1L;
    private static final TipoTrabalho TIPO_TRABALHO = TipoTrabalho.PRESENCIAL;
    private static final TipoUsuario TIPO_USUARIO = TipoUsuario.EMPLOYEE;
    private static final String LOGIN_VALIDO = "joao.silva@empresa.com";
    private static final String SENHA_VALIDA = "SenhaForte1!";

    @Test
    void deveSerValido_quandoTodosOsCamposEstiveremCorretos() {
        assertNoViolations(dtoValido(LOGIN_VALIDO, SENHA_VALIDA));
    }

    @ParameterizedTest(name = "{0} vazio deve gerar a mensagem correta")
    @MethodSource("camposTextoInvalidos")
    void deveRejeitarCamposDeTextoVazios(String campo, String mensagemEsperada, EmployeeRequestDTO dto) {
        assertHasViolation(dto, campo, mensagemEsperada);
    }

    @Test
    void deveRejeitarLoginQuandoNaoForEmailValido() {
        EmployeeRequestDTO dto = dtoValido("usuario-sem-arroba", SENHA_VALIDA);
        assertHasViolation(dto, "login", "O login deve ser um endereço de email válido");
    }

    @ParameterizedTest(name = "{0} nulo deve gerar a mensagem correta")
    @MethodSource("camposNulos")
    void deveRejeitarCamposObrigatoriosNulos(String campo, String mensagemEsperada, EmployeeRequestDTO dto) {
        assertHasViolation(dto, campo, mensagemEsperada);
    }

    @Test
    void deveAceitarSenhaNoLimiteMinimoEMaximo() {
        assertNoViolations(dtoValido(LOGIN_VALIDO, "Aa1!bcde"));
        assertNoViolations(dtoValido(LOGIN_VALIDO, senhaComTamanhoExato(50)));
    }

    @ParameterizedTest(name = "senha inválida: {0}")
    @MethodSource("senhasInvalidas")
    void deveRejeitarSenhasQueNaoAtendemARegra(String senha, String mensagemEsperada) {
        EmployeeRequestDTO dto = dtoValido(LOGIN_VALIDO, senha);
        assertHasViolation(dto, "password", mensagemEsperada);
    }

    private EmployeeRequestDTO dtoValido(String login, String password) {
        return new EmployeeRequestDTO(NOME, CARGO, WORKPLACE_ID, TIPO_TRABALHO, login, password, TIPO_USUARIO);
    }

    private static Stream<Arguments> camposTextoInvalidos() {
        return Stream.of(
                arguments("nome", "O nome não pode ser vazio",
                        new EmployeeRequestDTO("", CARGO, WORKPLACE_ID, TIPO_TRABALHO, LOGIN_VALIDO, SENHA_VALIDA, TIPO_USUARIO)),
                arguments("cargo", "O cargo é obrigatório",
                        new EmployeeRequestDTO(NOME, "", WORKPLACE_ID, TIPO_TRABALHO, LOGIN_VALIDO, SENHA_VALIDA, TIPO_USUARIO)),
                arguments("login", "O parâmetro login é obrigatório",
                        new EmployeeRequestDTO(NOME, CARGO, WORKPLACE_ID, TIPO_TRABALHO, "", SENHA_VALIDA, TIPO_USUARIO)),
                arguments("password", "A senha é obrigatória",
                        new EmployeeRequestDTO(NOME, CARGO, WORKPLACE_ID, TIPO_TRABALHO, LOGIN_VALIDO, "", TIPO_USUARIO))
        );
    }

    private static Stream<Arguments> camposNulos() {
        return Stream.of(
                arguments("workplaceId", "Workplace ID é obrigatório",
                        new EmployeeRequestDTO(NOME, CARGO, null, TIPO_TRABALHO, LOGIN_VALIDO, SENHA_VALIDA, TIPO_USUARIO)),
                arguments("tipoTrabalho", "O tipo de trabalho é obrigatório",
                        new EmployeeRequestDTO(NOME, CARGO, WORKPLACE_ID, null, LOGIN_VALIDO, SENHA_VALIDA, TIPO_USUARIO)),
                arguments("tipoUsuario", "O tipo do usuário é obrigatório",
                        new EmployeeRequestDTO(NOME, CARGO, WORKPLACE_ID, TIPO_TRABALHO, LOGIN_VALIDO, SENHA_VALIDA, null))
        );
    }

    private static Stream<Arguments> senhasInvalidas() {
        return Stream.of(
                arguments("Aa1!bcd", "A senha deve ter entre 8 e 50 caracteres"),
                arguments("senha123!", "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial"),
                arguments("SENHA123!", "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial"),
                arguments("SenhaForte!", "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial"),
                arguments("SenhaForte1", "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial"),
                arguments("SenhaForte1!" + "a".repeat(40), "A senha deve ter entre 8 e 50 caracteres")
        );
    }
}
