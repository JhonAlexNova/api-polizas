package com.pruebatecnica.polizas.controller;

import com.pruebatecnica.polizas.dto.SolicitudCoreEvento;
import com.pruebatecnica.polizas.service.CoreMockService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CoreMockController {

    private final CoreMockService coreMockService;

    public CoreMockController(CoreMockService coreMockService) {
        this.coreMockService = coreMockService;
    }

    @PostMapping("/core-mock/evento")
    public ResponseEntity<Map<String, Object>> registrarEvento(@Valid @RequestBody SolicitudCoreEvento solicitud) {
        coreMockService.registrarEvento(solicitud.evento(), solicitud.polizaId());
        return ResponseEntity.ok(Map.of("registrado", true, "evento", solicitud.evento(),
                "polizaId", solicitud.polizaId()));
    }
}
