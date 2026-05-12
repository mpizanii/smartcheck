package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.validation.ValidationTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class WorkplaceRequestDTOTest extends ValidationTestSupport {
    private static final String NOME = "Unidade Central";
    private static final String LOGRADOURO = "Rua A, 123";
    private static final String CIDADE = "São Paulo";
    private static final String ESTADO = "SP";
    private static final double LATITUDE_VALIDA = -23.5505;
    private static final double LONGITUDE_VALIDA = -46.6333;
    private static final double RAIO_VALIDO = 150.0;

    @Test
    void deveSerValido_quandoTodosOsCamposEstiveremCorretos() {
        assertNoViolations(dtoValido());
    }

    @Test
    void deveAceitarCoordenadasNosLimitesDoRange() {
        assertNoViolations(new WorkplaceRequestDTO(NOME, LOGRADOURO, CIDADE, ESTADO, -90.0, -180.0, RAIO_VALIDO));
        assertNoViolations(new WorkplaceRequestDTO(NOME, LOGRADOURO, CIDADE, ESTADO, 90.0, 180.0, RAIO_VALIDO));
    }

    @ParameterizedTest(name = "{0} vazio deve gerar a mensagem correta")
    @MethodSource("camposTextoInvalidos")
    void deveRejeitarCamposDeTextoVazios(String campo, String mensagemEsperada, WorkplaceRequestDTO dto) {
        assertHasViolation(dto, campo, mensagemEsperada);
    }

    @ParameterizedTest(name = "latitude {0} fora do range deve falhar")
    @MethodSource("latitudesInvalidas")
    void deveRejeitarLatitudeForaDoRange(Double latitude, String mensagemEsperada) {
        WorkplaceRequestDTO dto = new WorkplaceRequestDTO(NOME, LOGRADOURO, CIDADE, ESTADO, latitude, LONGITUDE_VALIDA, RAIO_VALIDO);
        assertHasViolation(dto, "latitude", mensagemEsperada);
    }

    @ParameterizedTest(name = "longitude {0} fora do range deve falhar")
    @MethodSource("longitudesInvalidas")
    void deveRejeitarLongitudeForaDoRange(Double longitude, String mensagemEsperada) {
        WorkplaceRequestDTO dto = new WorkplaceRequestDTO(NOME, LOGRADOURO, CIDADE, ESTADO, LATITUDE_VALIDA, longitude, RAIO_VALIDO);
        assertHasViolation(dto, "longitude", mensagemEsperada);
    }

    @ParameterizedTest(name = "campo numérico nulo {0} deve falhar")
    @MethodSource("camposNumericosNulos")
    void deveRejeitarCamposNumericosNulos(String campo, String mensagemEsperada, WorkplaceRequestDTO dto) {
        assertHasViolation(dto, campo, mensagemEsperada);
    }

    @ParameterizedTest(name = "raio {0} deve falhar")
    @MethodSource("raiosInvalidos")
    void deveRejeitarRaioNaoPositivo(Double raio, String mensagemEsperada) {
        WorkplaceRequestDTO dto = new WorkplaceRequestDTO(NOME, LOGRADOURO, CIDADE, ESTADO, LATITUDE_VALIDA, LONGITUDE_VALIDA, raio);
        assertHasViolation(dto, "raio", mensagemEsperada);
    }

    private WorkplaceRequestDTO dtoValido() {
        return new WorkplaceRequestDTO(NOME, LOGRADOURO, CIDADE, ESTADO, LATITUDE_VALIDA, LONGITUDE_VALIDA, RAIO_VALIDO);
    }

    private static Stream<Arguments> camposTextoInvalidos() {
        return Stream.of(
                arguments("nome", "O nome da unidade não pode ser vazio",
                        new WorkplaceRequestDTO("", LOGRADOURO, CIDADE, ESTADO, LATITUDE_VALIDA, LONGITUDE_VALIDA, RAIO_VALIDO)),
                arguments("logradouro", "O logradouro é obrigatório",
                        new WorkplaceRequestDTO(NOME, "", CIDADE, ESTADO, LATITUDE_VALIDA, LONGITUDE_VALIDA, RAIO_VALIDO)),
                arguments("cidade", "A cidade é obrigatório",
                        new WorkplaceRequestDTO(NOME, LOGRADOURO, "", ESTADO, LATITUDE_VALIDA, LONGITUDE_VALIDA, RAIO_VALIDO)),
                arguments("estado", "O estado é obrigatório",
                        new WorkplaceRequestDTO(NOME, LOGRADOURO, CIDADE, "", LATITUDE_VALIDA, LONGITUDE_VALIDA, RAIO_VALIDO))
        );
    }

    private static Stream<Arguments> latitudesInvalidas() {
        return Stream.of(
                arguments(-90.1, "A latitude deve ser maior ou igual a -90"),
                arguments(90.1, "A latitude deve ser menor ou igual a 90")
        );
    }

    private static Stream<Arguments> longitudesInvalidas() {
        return Stream.of(
                arguments(-180.1, "A longitude deve ser maior ou igual a -180"),
                arguments(180.1, "A longitude deve ser menor ou igual a 180")
        );
    }

    private static Stream<Arguments> camposNumericosNulos() {
        return Stream.of(
                arguments("latitude", "A latitude é obrigatória",
                        new WorkplaceRequestDTO(NOME, LOGRADOURO, CIDADE, ESTADO, null, LONGITUDE_VALIDA, RAIO_VALIDO)),
                arguments("longitude", "A longitude é obrigatória",
                        new WorkplaceRequestDTO(NOME, LOGRADOURO, CIDADE, ESTADO, LATITUDE_VALIDA, null, RAIO_VALIDO)),
                arguments("raio", "O raio de distância é obrigatório",
                        new WorkplaceRequestDTO(NOME, LOGRADOURO, CIDADE, ESTADO, LATITUDE_VALIDA, LONGITUDE_VALIDA, null))
        );
    }

    private static Stream<Arguments> raiosInvalidos() {
        return Stream.of(
                arguments(0.0, "O raio deve ser um valor maior que zero"),
                arguments(-10.0, "O raio deve ser um valor maior que zero")
        );
    }
}

