package com.pruebatecnica.polizas;

import com.pruebatecnica.polizas.model.EstadoPoliza;
import com.pruebatecnica.polizas.model.EstadoRiesgo;
import com.pruebatecnica.polizas.model.Poliza;
import com.pruebatecnica.polizas.model.Riesgo;
import com.pruebatecnica.polizas.model.TipoPoliza;
import com.pruebatecnica.polizas.repository.PolizaRepository;
import com.pruebatecnica.polizas.repository.RiesgoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PolizasApiIntegrationTests {

    private static final String API_KEY = "123456";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PolizaRepository polizaRepository;

    @Autowired
    private RiesgoRepository riesgoRepository;

    private Long polizaIndividualId;
    private Long polizaColectivaId;
    private Long polizaCanceladaId;
    private Long riesgoIndividualId;
    private Long riesgoColectivoId;

    @BeforeEach
    void prepararDatos() {
        riesgoRepository.deleteAll();
        polizaRepository.deleteAll();

        Poliza individual = new Poliza(TipoPoliza.INDIVIDUAL, EstadoPoliza.ACTIVA,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31), 12,
                BigDecimal.valueOf(450000), BigDecimal.valueOf(5400000));
        Riesgo riesgoIndividual = new Riesgo("Casa 101", EstadoRiesgo.ACTIVO);
        individual.agregarRiesgo(riesgoIndividual);

        Poliza colectiva = new Poliza(TipoPoliza.COLECTIVA, EstadoPoliza.ACTIVA,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 30), 6,
                BigDecimal.valueOf(300000), BigDecimal.valueOf(1800000));
        Riesgo riesgoColectivo = new Riesgo("Apartamento 201", EstadoRiesgo.ACTIVO);
        colectiva.agregarRiesgo(riesgoColectivo);
        colectiva.agregarRiesgo(new Riesgo("Apartamento 302", EstadoRiesgo.ACTIVO));

        Poliza cancelada = new Poliza(TipoPoliza.COLECTIVA, EstadoPoliza.CANCELADA,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), 12,
                BigDecimal.valueOf(250000), BigDecimal.valueOf(3000000));
        cancelada.agregarRiesgo(new Riesgo("Local 12", EstadoRiesgo.CANCELADO));

        Poliza individualGuardada = polizaRepository.saveAndFlush(individual);
        Poliza colectivaGuardada = polizaRepository.saveAndFlush(colectiva);
        Poliza canceladaGuardada = polizaRepository.saveAndFlush(cancelada);

        polizaIndividualId = individualGuardada.getId();
        polizaColectivaId = colectivaGuardada.getId();
        polizaCanceladaId = canceladaGuardada.getId();
        riesgoIndividualId = riesgoRepository.findAllByPolizaIdOrderByIdAsc(polizaIndividualId).getFirst().getId();
        riesgoColectivoId = riesgoRepository.findAllByPolizaIdOrderByIdAsc(polizaColectivaId).getFirst().getId();
    }

    @Test
    void consultaPolizasYFiltraPorTipoYEstado() throws Exception {
        mockMvc.perform(get("/polizas").header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/polizas").param("tipo", "COLECTIVA").param("estado", "ACTIVA")
                        .header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipo", is("COLECTIVA")))
                .andExpect(jsonPath("$[0].estado", is("ACTIVA")));
    }

    @Test
    void consultaRiesgosDeUnaPoliza() throws Exception {
        mockMvc.perform(get("/polizas/{id}/riesgos", polizaColectivaId).header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].inmueble", is("Apartamento 201")));
    }

    @Test
    void renuevaConIpcYRecalculaValores() throws Exception {
        mockMvc.perform(post("/polizas/{id}/renovar", polizaIndividualId)
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ipc\":5.2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("RENOVADA")))
                .andExpect(jsonPath("$.canonMensual", is(473400.0)))
                .andExpect(jsonPath("$.prima", is(5680800.0)))
                .andExpect(jsonPath("$.fechaInicio", is("2027-01-01")))
                .andExpect(jsonPath("$.fechaFin", is("2027-12-31")));
    }

    @Test
    void rechazaRenovacionDePolizaCancelada() throws Exception {
        mockMvc.perform(post("/polizas/{id}/renovar", polizaCanceladaId)
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ipc\":5.2}"))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelaPolizaYTodosSusRiesgos() throws Exception {
        mockMvc.perform(post("/polizas/{id}/cancelar", polizaColectivaId)
                        .header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CANCELADA")))
                .andExpect(jsonPath("$.riesgos[0].estado", is("CANCELADO")))
                .andExpect(jsonPath("$.riesgos[1].estado", is("CANCELADO")));

        mockMvc.perform(post("/polizas/{id}/cancelar", polizaColectivaId)
                        .header("x-api-key", API_KEY))
                .andExpect(status().isConflict());
    }

    @Test
    void creaRiesgosSoloEnPolizasColectivas() throws Exception {
        mockMvc.perform(post("/polizas/{id}/riesgos", polizaColectivaId)
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inmueble\":\"Apartamento 404\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.inmueble", is("Apartamento 404")));

        mockMvc.perform(post("/polizas/{id}/riesgos", polizaIndividualId)
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inmueble\":\"Casa 202\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelaRiesgoColectivoYProtegeRiesgoUnicoIndividual() throws Exception {
        mockMvc.perform(post("/riesgos/{id}/cancelar", riesgoColectivoId)
                        .header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("CANCELADO")));

        mockMvc.perform(post("/riesgos/{id}/cancelar", riesgoColectivoId)
                        .header("x-api-key", API_KEY))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/riesgos/{id}/cancelar", riesgoIndividualId)
                        .header("x-api-key", API_KEY))
                .andExpect(status().isConflict());
    }

    @Test
    void validaApiKeyEnTodosLosEndpoints() throws Exception {
        mockMvc.perform(get("/polizas"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/polizas").header("x-api-key", "incorrecta"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registraEventoEnCoreMock() throws Exception {
        mockMvc.perform(post("/core-mock/evento")
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"evento\":\"ACTUALIZACION\",\"polizaId\":555}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.registrado", is(true)))
                .andExpect(jsonPath("$.evento", is("ACTUALIZACION")))
                .andExpect(jsonPath("$.polizaId", is(555)));
    }

    @Test
    void respondeErroresParaRecursosInexistentesYDatosInvalidos() throws Exception {
        mockMvc.perform(get("/polizas/999999/riesgos")
                        .header("x-api-key", API_KEY))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/riesgos/999999/cancelar")
                        .header("x-api-key", API_KEY))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/polizas/{id}/riesgos", polizaColectivaId)
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"inmueble\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
