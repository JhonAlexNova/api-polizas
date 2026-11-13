package com.pruebatecnica.polizas.repository;

import com.pruebatecnica.polizas.model.EstadoPoliza;
import com.pruebatecnica.polizas.model.Poliza;
import com.pruebatecnica.polizas.model.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    List<Poliza> findAllByOrderByIdAsc();

    List<Poliza> findAllByTipoOrderByIdAsc(TipoPoliza tipo);

    List<Poliza> findAllByEstadoOrderByIdAsc(EstadoPoliza estado);

    List<Poliza> findAllByTipoAndEstadoOrderByIdAsc(TipoPoliza tipo, EstadoPoliza estado);
}
