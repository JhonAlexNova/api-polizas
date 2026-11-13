package com.pruebatecnica.polizas.config;

import com.pruebatecnica.polizas.model.EstadoPoliza;
import com.pruebatecnica.polizas.model.EstadoRiesgo;
import com.pruebatecnica.polizas.model.Poliza;
import com.pruebatecnica.polizas.model.Riesgo;
import com.pruebatecnica.polizas.model.TipoPoliza;
import com.pruebatecnica.polizas.repository.PolizaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DatosInicialesConfig {

    @Bean
    CommandLineRunner cargarDatosIniciales(PolizaRepository polizaRepository) {
        return args -> {
            if (polizaRepository.count() > 0) {
                return;
            }

            Poliza individual = new Poliza(TipoPoliza.INDIVIDUAL, EstadoPoliza.ACTIVA,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 12,
                    BigDecimal.valueOf(450000), BigDecimal.valueOf(5400000));
            individual.agregarRiesgo(new Riesgo("Casa 101", EstadoRiesgo.ACTIVO));

            Poliza colectiva = new Poliza(TipoPoliza.COLECTIVA, EstadoPoliza.ACTIVA,
                    LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30), 6,
                    BigDecimal.valueOf(300000), BigDecimal.valueOf(1800000));
            colectiva.agregarRiesgo(new Riesgo("Apartamento 201", EstadoRiesgo.ACTIVO));
            colectiva.agregarRiesgo(new Riesgo("Apartamento 302", EstadoRiesgo.ACTIVO));

            Poliza cancelada = new Poliza(TipoPoliza.COLECTIVA, EstadoPoliza.CANCELADA,
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), 12,
                    BigDecimal.valueOf(250000), BigDecimal.valueOf(3000000));
            cancelada.agregarRiesgo(new Riesgo("Local 12", EstadoRiesgo.CANCELADO));

            polizaRepository.save(individual);
            polizaRepository.save(colectiva);
            polizaRepository.save(cancelada);
        };
    }
}
