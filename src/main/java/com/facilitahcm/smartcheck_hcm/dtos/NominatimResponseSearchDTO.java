package com.facilitahcm.smartcheck_hcm.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimResponseSearchDTO(
        String lat,
        String lon,
        String name,
        String display_name
) { }

/*
    Exemplo de resposta:
    [
      {
        "place_id": 12141524,
        "licence": "Data © OpenStreetMap contributors, ODbL 1.0. http://osm.org/copyright",
        "osm_type": "way",
        "osm_id": 25615465,
        "lat": "-15.8491324",
        "lon": "-48.0423766",
        "class": "highway",
        "type": "secondary",
        "place_rank": 26,
        "importance": 0.0533846395056077,
        "addresstype": "road",
        "name": "Marginal Oeste da DF-001",
        "display_name": "Marginal Oeste da DF-001, Setor D Sul, Taguatintga Sul, Taguatinga, Região Geográfica Intermediária do Distrito Federal, Federal District, Central-West Region, 71953-000, Brazil, Região Geográfica Imediata do Distrito Federal",
        "boundingbox": [
          "-15.8508795",
          "-15.8474052",
          "-48.0433282",
          "-48.0414614"
        ]
      }
     ]
 */
