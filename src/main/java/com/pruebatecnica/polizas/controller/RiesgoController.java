package com.pruebatecnica.polizas.controller;

import com.pruebatecnica.polizas.dto.RiesgoResponse;
import com.pruebatecnica.polizas.service.RiesgoService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RiesgoController {

    private final RiesgoService riesgoService;

    public RiesgoController(RiesgoService riesgoService) {
        this.riesgoService = riesgoService;
    }

    @PostMapping("/riesgos/{id}/cancelar")
    public RiesgoResponse cancelarRiesgo(@PathVariable Long id) {
        return riesgoService.cancelarRiesgo(id);
    }
}
