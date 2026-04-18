package com.facilitahcm.smartcheck_hcm;

import com.facilitahcm.smartcheck_hcm.services.GeolocationService;
import com.facilitahcm.smartcheck_hcm.utils.GeoUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeoLocationServiceTest {
    private final GeolocationService geolocationService = new GeolocationService();

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
