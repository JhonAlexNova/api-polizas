package com.pruebatecnica.polizas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "riesgos")
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poliza_id", nullable = false)
    private Poliza poliza;

    @Column(nullable = false, length = 255)
    private String inmueble;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoRiesgo estado;

    protected Riesgo() {
    }

    public Riesgo(String inmueble, EstadoRiesgo estado) {
        this.inmueble = inmueble;
        this.estado = estado;
    }

    public void asignarPoliza(Poliza poliza) {
        this.poliza = poliza;
    }

    public void cancelar() {
        this.estado = EstadoRiesgo.CANCELADO;
    }

    public Long getId() {
        return id;
    }

    public Poliza getPoliza() {
        return poliza;
    }

    public String getInmueble() {
        return inmueble;
    }

    public EstadoRiesgo getEstado() {
        return estado;
    }
}
