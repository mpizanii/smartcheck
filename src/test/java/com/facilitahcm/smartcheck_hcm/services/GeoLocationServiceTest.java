package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.NominatimResponseReverseDTO;
import com.facilitahcm.smartcheck_hcm.dtos.NominatimResponseSearchDTO;
import com.facilitahcm.smartcheck_hcm.exceptions.ExternalServiceException;
import com.facilitahcm.smartcheck_hcm.exceptions.ResourceNotFoundException;
import com.facilitahcm.smartcheck_hcm.utils.GeoUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeoLocationServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GeolocationService geolocationService;

    @Test
    void deveRetornarCoordenadas_quandoNominatimResponderComSucesso() {
        NominatimResponseSearchDTO esperado = new NominatimResponseSearchDTO("-23.5", "-46.6", "Paulista", "Avenida Paulista");

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(NominatimResponseSearchDTO[].class)
        )).thenReturn(ResponseEntity.ok(new NominatimResponseSearchDTO[]{esperado}));

        NominatimResponseSearchDTO resultado = geolocationService.obterCoordenadas("Avenida Paulista, 1000");

        assertEquals(esperado, resultado);
    }

    @Test
    void deveLancarResourceNotFound_quandoNominatimRetornarListaVazia() {
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(NominatimResponseSearchDTO[].class)
        )).thenReturn(ResponseEntity.ok(new NominatimResponseSearchDTO[]{}));

        assertThrows(ResourceNotFoundException.class,
                () -> geolocationService.obterCoordenadas("Endereco inexistente"));
    }

    @Test
    void deveLancarExternalServiceException_quandoFalharConsultaNoNominatim() {
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(NominatimResponseSearchDTO[].class)
        )).thenThrow(new RestClientException("timeout"));

        assertThrows(ExternalServiceException.class,
                () -> geolocationService.obterCoordenadas("Avenida Paulista"));
    }

    @Test
    void deveLancarResourceNotFound_quandoReverseRetornarBodyNulo() {
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(NominatimResponseReverseDTO.class)
        )).thenReturn(ResponseEntity.ok(null));

        assertThrows(ResourceNotFoundException.class,
                () -> geolocationService.buscarEnderecoPorCoordenadas(-23.55, -46.63));
    }

    @Test
    void deveRetornarFalse_quandoMesmoPonto() {
        boolean foraDoRaio = geolocationService.isForaDoRaio(
                -23.55052,
                -46.633308,
                -23.55052,
                -46.633308,
                100.0
        );

        assertFalse(foraDoRaio);
    }

    @Test
    void deveRetornarFalse_quandoDentroDoRaio() {
        boolean foraDoRaio = geolocationService.isForaDoRaio(
                -23.55052,
                -46.633308,
                -23.55080,
                -46.633500,
                100.0
        );

        assertFalse(foraDoRaio);
    }

    @Test
    void deveRetornarTrue_quandoForaDoRaio() {
        boolean foraDoRaio = geolocationService.isForaDoRaio(
                -23.55052,
                -46.633308,
                -23.56000,
                -46.650000,
                100.0
        );

        assertTrue(foraDoRaio);
    }

    @Test
    void deveRetornarFalse_quandoDistanciaIgualAoRaio() {
        double latWorkplace = -23.55052;
        double longWorkplace = -46.633308;
        double latEmployee = -23.55080;
        double lonEmployee = -46.633500;

        double distancia = GeoUtils.calcularDistancia(latWorkplace, longWorkplace, latEmployee, lonEmployee);

        boolean foraDoRaio = geolocationService.isForaDoRaio(
                latWorkplace,
                longWorkplace,
                latEmployee,
                lonEmployee,
                distancia
        );

        assertFalse(foraDoRaio);
    }
}
