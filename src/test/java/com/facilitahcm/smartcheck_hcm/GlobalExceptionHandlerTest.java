package com.facilitahcm.smartcheck_hcm;

import com.facilitahcm.smartcheck_hcm.exceptions.BusinessException;
import com.facilitahcm.smartcheck_hcm.exceptions.ExternalServiceException;
import com.facilitahcm.smartcheck_hcm.exceptions.GlobalExceptionHandler;
import com.facilitahcm.smartcheck_hcm.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveRetornar404_quandoResourceNotFoundException() {
        ResponseEntity<String> response = handler.handleNotFound(new ResourceNotFoundException("Nao encontrado"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Nao encontrado", response.getBody());
    }

    @Test
    void deveRetornar400_quandoBusinessException() {
        ResponseEntity<String> response = handler.handleBusiness(new BusinessException("Regra de negocio"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Regra de negocio", response.getBody());
    }

    @Test
    void deveRetornar503_quandoExternalServiceException() {
        ResponseEntity<String> response = handler.handleExternalService(new ExternalServiceException("Servico externo indisponivel"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Servico externo indisponivel", response.getBody());
    }

    @Test
    void deveRetornar500_quandoErroGenerico() {
        ResponseEntity<String> response = handler.handleGeneral(new RuntimeException("falha inesperada"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Erro interno no servidor"));
    }

    @Test
    void deveRetornarListaDeErros_quandoValidacaoFalhar() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "timePunchRequestDTO");
        bindingResult.addError(new FieldError("timePunchRequestDTO", "employeeId", "O ID do funcionario e obrigatorio"));

        Method method = DummyValidationTarget.class.getDeclaredMethod("dummy", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<List<String>> response = handler.handleValidation(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertTrue(response.getBody().get(0).contains("employeeId"));
    }

    static class DummyValidationTarget {
        public void dummy(String payload) {
        }
    }
}

