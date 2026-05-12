package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.validation.ValidationTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class RegisterRequestDTOTest extends ValidationTestSupport {
    private static final String LOGIN_VALIDO = "joao.silva@empresa.com";

    @Test
    void deveSerValido_quandoLoginSenhaETipoUsuarioForemCorretos() {
        assertNoViolations(new RegisterRequestDTO(LOGIN_VALIDO, senhaComTamanhoExato(8), TipoUsuario.EMPLOYEE));
    }

    @Test
    void deveAceitarSenhaNoLimiteMaximo() {
        assertNoViolations(new RegisterRequestDTO(LOGIN_VALIDO, senhaComTamanhoExato(50), TipoUsuario.ADMIN));
    }

    @ParameterizedTest(name = "{0} vazio deve gerar a mensagem correta")
    @MethodSource("camposTextoInvalidos")
    void deveRejeitarCamposDeTextoVazios(String campo, String mensagemEsperada, RegisterRequestDTO dto) {
        assertHasViolation(dto, campo, mensagemEsperada);
    }

    @Test
    void deveRejeitarLoginQuandoNaoForEmailValido() {
        RegisterRequestDTO dto = new RegisterRequestDTO("usuario-sem-arroba", senhaComTamanhoExato(8), TipoUsuario.EMPLOYEE);
        assertHasViolation(dto, "login", "O login deve ser um endereço de email válido");
    }

    @ParameterizedTest(name = "{0} nulo deve gerar a mensagem correta")
    @MethodSource("camposNulos")
    void deveRejeitarCamposObrigatoriosNulos(String campo, String mensagemEsperada, RegisterRequestDTO dto) {
        assertHasViolation(dto, campo, mensagemEsperada);
    }

    @ParameterizedTest(name = "senha inválida: {0}")
    @MethodSource("senhasInvalidas")
    void deveRejeitarSenhasQueNaoAtendemARegra(String senha, String mensagemEsperada) {
        RegisterRequestDTO dto = new RegisterRequestDTO(LOGIN_VALIDO, senha, TipoUsuario.EMPLOYEE);
        assertHasViolation(dto, "password", mensagemEsperada);
    }

    private static Stream<Arguments> camposTextoInvalidos() {
        return Stream.of(
                arguments("login", "O parâmetro login é obrigatório",
                        new RegisterRequestDTO("", senhaComTamanhoExato(8), TipoUsuario.EMPLOYEE)),
                arguments("password", "A senha é obrigatória",
                        new RegisterRequestDTO(LOGIN_VALIDO, "", TipoUsuario.EMPLOYEE))
        );
    }

    private static Stream<Arguments> camposNulos() {
        return Stream.of(
                arguments("tipoUsuario", "O tipo do usuário é obrigatório",
                        new RegisterRequestDTO(LOGIN_VALIDO, senhaComTamanhoExato(8), null))
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
