package com.pruebatecnica.polizas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CoreMockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreMockService.class);

    public void registrarEvento(String evento, Long polizaId) {
        LOGGER.info("Evento CORE simulado: evento={}, polizaId={}", evento, polizaId);
    }
}
