package com.pruebatecnica.polizas.repository;

import com.pruebatecnica.polizas.model.EstadoRiesgo;
import com.pruebatecnica.polizas.model.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {

    List<Riesgo> findAllByPolizaIdOrderByIdAsc(Long polizaId);

    long countByPolizaIdAndEstado(Long polizaId, EstadoRiesgo estado);
}
