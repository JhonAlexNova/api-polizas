package com.pruebatecnica.polizas.service;

import com.pruebatecnica.polizas.dto.PolizaResponse;
import com.pruebatecnica.polizas.dto.RiesgoResponse;
import com.pruebatecnica.polizas.dto.SolicitudRenovacion;
import com.pruebatecnica.polizas.dto.SolicitudRiesgo;
import com.pruebatecnica.polizas.exception.RecursoNoEncontradoException;
import com.pruebatecnica.polizas.exception.ReglaNegocioException;
import com.pruebatecnica.polizas.model.EstadoPoliza;
import com.pruebatecnica.polizas.model.EstadoRiesgo;
import com.pruebatecnica.polizas.model.Poliza;
import com.pruebatecnica.polizas.model.Riesgo;
import com.pruebatecnica.polizas.model.TipoPoliza;
import com.pruebatecnica.polizas.repository.PolizaRepository;
import com.pruebatecnica.polizas.repository.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class PolizaService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final CoreMockService coreMockService;

    public PolizaService(PolizaRepository polizaRepository, RiesgoRepository riesgoRepository,
                         CoreMockService coreMockService) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.coreMockService = coreMockService;
    }

    @Transactional(readOnly = true)
    public List<PolizaResponse> consultarPolizas(TipoPoliza tipo, EstadoPoliza estado) {
        List<Poliza> polizas;
        if (tipo != null && estado != null) {
            polizas = polizaRepository.findAllByTipoAndEstadoOrderByIdAsc(tipo, estado);
        } else if (tipo != null) {
            polizas = polizaRepository.findAllByTipoOrderByIdAsc(tipo);
        } else if (estado != null) {
            polizas = polizaRepository.findAllByEstadoOrderByIdAsc(estado);
        } else {
            polizas = polizaRepository.findAllByOrderByIdAsc();
        }
        return polizas.stream().map(PolizaResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public List<RiesgoResponse> obtenerRiesgos(Long polizaId) {
        obtenerPoliza(polizaId);
        return riesgoRepository.findAllByPolizaIdOrderByIdAsc(polizaId).stream()
                .map(RiesgoResponse::desde)
                .toList();
    }

    @Transactional
    public PolizaResponse renovarPoliza(Long polizaId, SolicitudRenovacion solicitud) {
        Poliza poliza = obtenerPoliza(polizaId);
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("No se puede renovar una póliza cancelada");
        }

        BigDecimal factorIpc = BigDecimal.ONE.add(solicitud.ipc().divide(CIEN, 10, RoundingMode.HALF_UP));
        BigDecimal nuevoCanon = poliza.getCanonMensual().multiply(factorIpc).setScale(2, RoundingMode.HALF_UP);
        BigDecimal nuevaPrima = nuevoCanon.multiply(BigDecimal.valueOf(poliza.getMesesVigencia()))
                .setScale(2, RoundingMode.HALF_UP);
        LocalDate nuevaFechaInicio = poliza.getFechaFin().plusDays(1);
        LocalDate nuevaFechaFin = nuevaFechaInicio.plusMonths(poliza.getMesesVigencia()).minusDays(1);

        poliza.actualizarRenovacion(nuevaFechaInicio, nuevaFechaFin, nuevoCanon, nuevaPrima);
        Poliza polizaGuardada = polizaRepository.save(poliza);
        coreMockService.registrarEvento("ACTUALIZACION", polizaId);
        return PolizaResponse.desde(polizaGuardada);
    }

    @Transactional
    public PolizaResponse cancelarPoliza(Long polizaId) {
        Poliza poliza = obtenerPoliza(polizaId);
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("La póliza ya se encuentra cancelada");
        }
        poliza.cancelar();
        Poliza polizaGuardada = polizaRepository.save(poliza);
        coreMockService.registrarEvento("ACTUALIZACION", polizaId);
        return PolizaResponse.desde(polizaGuardada);
    }

    @Transactional
    public RiesgoResponse agregarRiesgo(Long polizaId, SolicitudRiesgo solicitud) {
        Poliza poliza = obtenerPoliza(polizaId);
        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new ReglaNegocioException("Solo se pueden agregar riesgos a pólizas colectivas");
        }
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new ReglaNegocioException("No se pueden agregar riesgos a una póliza cancelada");
        }

        Riesgo riesgo = new Riesgo(solicitud.inmueble().trim(), EstadoRiesgo.ACTIVO);
        poliza.agregarRiesgo(riesgo);
        polizaRepository.save(poliza);
        coreMockService.registrarEvento("ACTUALIZACION", polizaId);
        return RiesgoResponse.desde(riesgo);
    }

    private Poliza obtenerPoliza(Long polizaId) {
        return polizaRepository.findById(polizaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la póliza " + polizaId));
    }
}
