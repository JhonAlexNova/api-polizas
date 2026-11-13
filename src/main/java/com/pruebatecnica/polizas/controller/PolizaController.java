package com.pruebatecnica.polizas.controller;

import com.pruebatecnica.polizas.dto.PolizaResponse;
import com.pruebatecnica.polizas.dto.RiesgoResponse;
import com.pruebatecnica.polizas.dto.SolicitudRenovacion;
import com.pruebatecnica.polizas.dto.SolicitudRiesgo;
import com.pruebatecnica.polizas.model.EstadoPoliza;
import com.pruebatecnica.polizas.model.TipoPoliza;
import com.pruebatecnica.polizas.service.PolizaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @GetMapping("/polizas")
    public List<PolizaResponse> consultarPolizas(@RequestParam(required = false) TipoPoliza tipo,
                                                 @RequestParam(required = false) EstadoPoliza estado) {
        return polizaService.consultarPolizas(tipo, estado);
    }

    @GetMapping("/polizas/{id}/riesgos")
    public List<RiesgoResponse> obtenerRiesgos(@PathVariable Long id) {
        return polizaService.obtenerRiesgos(id);
    }

    @PostMapping("/polizas/{id}/renovar")
    public PolizaResponse renovarPoliza(@PathVariable Long id,
                                       @Valid @RequestBody SolicitudRenovacion solicitud) {
        return polizaService.renovarPoliza(id, solicitud);
    }

    @PostMapping("/polizas/{id}/cancelar")
    public PolizaResponse cancelarPoliza(@PathVariable Long id) {
        return polizaService.cancelarPoliza(id);
    }

    @PostMapping("/polizas/{id}/riesgos")
    public ResponseEntity<RiesgoResponse> agregarRiesgo(@PathVariable Long id,
                                                       @Valid @RequestBody SolicitudRiesgo solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(polizaService.agregarRiesgo(id, solicitud));
    }
}
