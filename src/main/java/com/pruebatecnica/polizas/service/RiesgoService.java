package com.pruebatecnica.polizas.service;

import com.pruebatecnica.polizas.dto.RiesgoResponse;
import com.pruebatecnica.polizas.exception.RecursoNoEncontradoException;
import com.pruebatecnica.polizas.exception.ReglaNegocioException;
import com.pruebatecnica.polizas.model.EstadoPoliza;
import com.pruebatecnica.polizas.model.EstadoRiesgo;
import com.pruebatecnica.polizas.model.Riesgo;
import com.pruebatecnica.polizas.model.TipoPoliza;
import com.pruebatecnica.polizas.repository.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final CoreMockService coreMockService;

    public RiesgoService(RiesgoRepository riesgoRepository, CoreMockService coreMockService) {
        this.riesgoRepository = riesgoRepository;
        this.coreMockService = coreMockService;
    }

    @Transactional
    public RiesgoResponse cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el riesgo " + riesgoId));
        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new ReglaNegocioException("El riesgo ya se encuentra cancelado");
        }

        if (riesgo.getPoliza().getTipo() == TipoPoliza.INDIVIDUAL
                && riesgo.getPoliza().getEstado() != EstadoPoliza.CANCELADA
                && riesgoRepository.countByPolizaIdAndEstado(riesgo.getPoliza().getId(), EstadoRiesgo.ACTIVO) <= 1) {
            throw new ReglaNegocioException("Una póliza individual activa debe conservar un riesgo activo");
        }

        riesgo.cancelar();
        Riesgo riesgoGuardado = riesgoRepository.save(riesgo);
        coreMockService.registrarEvento("ACTUALIZACION", riesgo.getPoliza().getId());
        return RiesgoResponse.desde(riesgoGuardado);
    }
}
