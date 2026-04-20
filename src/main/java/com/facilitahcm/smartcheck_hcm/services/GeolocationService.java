package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.NominatimResponseReverseDTO;
import com.facilitahcm.smartcheck_hcm.dtos.NominatimResponseSearchDTO;
import com.facilitahcm.smartcheck_hcm.exceptions.ExternalServiceException;
import com.facilitahcm.smartcheck_hcm.exceptions.ResourceNotFoundException;
import com.facilitahcm.smartcheck_hcm.utils.GeoUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class GeolocationService {
    private final RestTemplate restTemplate;
    private static final String MENSAGEM_FALHA_GEO = "Falha ao consultar serviço de geolocalização.";

    public GeolocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NominatimResponseSearchDTO obterCoordenadas(String endereco) {
        URI url = UriComponentsBuilder
                .fromUriString("https://nominatim.openstreetmap.org/search")
                .queryParam("q", endereco)
                .queryParam("format", "json")
                .queryParam("limit", 1)
                .build()
                .encode()
                .toUri();

        try {
            ResponseEntity<NominatimResponseSearchDTO[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    NominatimResponseSearchDTO[].class
            );

            if (response.getBody() != null && response.getBody().length > 0) {
                return response.getBody()[0];
            }

            throw new ResourceNotFoundException("Endereço não encontrado.");
        } catch (RestClientException ex) {
            throw new ExternalServiceException(MENSAGEM_FALHA_GEO, ex);
        }
    }

    public NominatimResponseReverseDTO buscarEnderecoPorCoordenadas (double latitude, double longitude) {
        URI url = UriComponentsBuilder
                .fromUriString("https://nominatim.openstreetmap.org/reverse")
                .queryParam("lat", latitude)
                .queryParam("lon", longitude)
                .queryParam("format", "json")
                .build()
                .encode()
                .toUri();

        try {
            ResponseEntity<NominatimResponseReverseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    NominatimResponseReverseDTO.class
            );

            if (response.getBody() != null) {
                return response.getBody();
            }

            throw new ResourceNotFoundException("Endereço não encontrado para as coordenadas informadas.");
        } catch (RestClientException ex) {
            throw new ExternalServiceException(MENSAGEM_FALHA_GEO, ex);
        }
    }

    public boolean isForaDoRaio(Double latitudeworkPlace, Double longitudeWorkPlace, Double latitudeTimePunch, Double longitudeTimePunch, Double raioMetros) {
        double distanciaCalculada = GeoUtils.calcularDistancia(latitudeworkPlace, longitudeWorkPlace, latitudeTimePunch, longitudeTimePunch);
        return distanciaCalculada > raioMetros;
    }
}
