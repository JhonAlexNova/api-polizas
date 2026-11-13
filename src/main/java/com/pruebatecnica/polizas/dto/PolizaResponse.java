package com.pruebatecnica.polizas.dto;

import com.pruebatecnica.polizas.model.EstadoPoliza;
import com.pruebatecnica.polizas.model.Poliza;
import com.pruebatecnica.polizas.model.TipoPoliza;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PolizaResponse(Long id, TipoPoliza tipo, EstadoPoliza estado, LocalDate fechaInicio,
                             LocalDate fechaFin, Integer mesesVigencia, BigDecimal canonMensual,
                             BigDecimal prima, List<RiesgoResponse> riesgos) {

    public static PolizaResponse desde(Poliza poliza) {
        List<RiesgoResponse> riesgos = poliza.getRiesgos().stream()
                .map(RiesgoResponse::desde)
                .toList();
        return new PolizaResponse(poliza.getId(), poliza.getTipo(), poliza.getEstado(),
                poliza.getFechaInicio(), poliza.getFechaFin(), poliza.getMesesVigencia(),
                poliza.getCanonMensual(), poliza.getPrima(), riesgos);
    }
}
