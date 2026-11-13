package com.pruebatecnica.polizas.dto;

import com.pruebatecnica.polizas.model.EstadoRiesgo;
import com.pruebatecnica.polizas.model.Riesgo;

public record RiesgoResponse(Long id, String inmueble, EstadoRiesgo estado) {

    public static RiesgoResponse desde(Riesgo riesgo) {
        return new RiesgoResponse(riesgo.getId(), riesgo.getInmueble(), riesgo.getEstado());
    }
}
