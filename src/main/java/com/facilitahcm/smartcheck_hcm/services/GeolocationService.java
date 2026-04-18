package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.utils.GeoUtils;
import org.springframework.stereotype.Service;

@Service
public class GeolocationService {
    public boolean isForaDoRaio(Double latitudeworkPlace, Double longitudeWorkPlace, Double latitudeTimePunch, Double longitudeTimePunch, Double raioMetros) {
        double distanciaCalculada = GeoUtils.calcularDistancia(latitudeworkPlace, longitudeWorkPlace, latitudeTimePunch, longitudeTimePunch);
        return distanciaCalculada > raioMetros;
    }
}
