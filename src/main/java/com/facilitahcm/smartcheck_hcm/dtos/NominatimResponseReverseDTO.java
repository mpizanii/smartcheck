package com.facilitahcm.smartcheck_hcm.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimResponseReverseDTO(
    String lat,
    String lon,
    AddressDTO address
) { }

/*
    Exemplo de resposta:
        {
          "place_id": 8541920,
          "licence": "Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright",
          "osm_type": "node",
          "osm_id": 4311073828,
          "lat": "-23.5504428",
          "lon": "-46.6334463",
          "class": "railway",
          "type": "station",
          "place_rank": 30,
          "importance": 0.457493413983199,
          "addresstype": "railway",
          "name": "Sé",
          "display_name": "Sé, Rua Santa Teresa, Glicério, Sé, São Paulo, Southeast Region, 01016-020, Brazil",
          "address": {
            "railway": "Sé",
            "road": "Rua Santa Teresa",
            "suburb": "Glicério",
            "city": "São Paulo",
            "state": "São Paulo",
            "ISO3166-2-lvl4": "BR-SP",
            "region": "Southeast Region",
            "postcode": "01016-020",
            "country": "Brazil",
            "country_code": "br"
          },
          "boundingbox": [
            "-23.5554428",
            "-23.5454428",
            "-46.6384463",
            "-46.6284463"
          ]
        }
 */
