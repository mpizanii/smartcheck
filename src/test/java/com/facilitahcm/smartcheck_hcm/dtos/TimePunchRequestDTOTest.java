package com.facilitahcm.smartcheck_hcm.dtos;

import com.facilitahcm.smartcheck_hcm.enums.TipoTimePunch;
import com.facilitahcm.smartcheck_hcm.validation.ValidationTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class TimePunchRequestDTOTest extends ValidationTestSupport {
    @Test
    void deveSerValido_quandoTodosOsCamposEstiveremCorretos() {
        assertNoViolations(new TimePunchRequestDTO(TipoTimePunch.CHECK_IN, -23.5505, -46.6333));
    }

    @Test
    void deveAceitarCoordenadasNosLimitesDoRange() {
        assertNoViolations(new TimePunchRequestDTO(TipoTimePunch.CHECK_OUT, -90.0, -180.0));
        assertNoViolations(new TimePunchRequestDTO(TipoTimePunch.CHECK_OUT, 90.0, 180.0));
    }

    @ParameterizedTest(name = "{0} nulo deve gerar a mensagem correta")
    @MethodSource("camposNulos")
    void deveRejeitarCamposObrigatoriosNulos(String campo, String mensagemEsperada, TimePunchRequestDTO dto) {
        assertHasViolation(dto, campo, mensagemEsperada);
    }

    @ParameterizedTest(name = "latitude {0} fora do range deve falhar")
    @MethodSource("latitudesInvalidas")
    void deveRejeitarLatitudeForaDoRange(Double latitude, String mensagemEsperada) {
        TimePunchRequestDTO dto = new TimePunchRequestDTO(TipoTimePunch.CHECK_IN, latitude, -46.6333);
        assertHasViolation(dto, "latitude", mensagemEsperada);
    }

    @ParameterizedTest(name = "longitude {0} fora do range deve falhar")
    @MethodSource("longitudesInvalidas")
    void deveRejeitarLongitudeForaDoRange(Double longitude, String mensagemEsperada) {
        TimePunchRequestDTO dto = new TimePunchRequestDTO(TipoTimePunch.CHECK_IN, -23.5505, longitude);
        assertHasViolation(dto, "longitude", mensagemEsperada);
    }

    private static Stream<Arguments> camposNulos() {
        return Stream.of(
                arguments("tipoTimePunch", "O tipo de batida é obrigatório",
                        new TimePunchRequestDTO(null, -23.5505, -46.6333)),
                arguments("latitude", "A latitude é obrigatória",
                        new TimePunchRequestDTO(TipoTimePunch.CHECK_IN, null, -46.6333)),
                arguments("longitude", "A longitude é obrigatória",
                        new TimePunchRequestDTO(TipoTimePunch.CHECK_IN, -23.5505, null))
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
}

