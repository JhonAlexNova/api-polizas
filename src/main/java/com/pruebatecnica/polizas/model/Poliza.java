package com.pruebatecnica.polizas.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPoliza tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPoliza estado;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Integer mesesVigencia;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal canonMensual;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal prima;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = false)
    private final List<Riesgo> riesgos = new ArrayList<>();

    protected Poliza() {
    }

    public Poliza(TipoPoliza tipo, EstadoPoliza estado, LocalDate fechaInicio, LocalDate fechaFin,
                  Integer mesesVigencia, BigDecimal canonMensual, BigDecimal prima) {
        this.tipo = tipo;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.mesesVigencia = mesesVigencia;
        this.canonMensual = canonMensual;
        this.prima = prima;
    }

    public void agregarRiesgo(Riesgo riesgo) {
        riesgos.add(riesgo);
        riesgo.asignarPoliza(this);
    }

    public Long getId() {
        return id;
    }

    public TipoPoliza getTipo() {
        return tipo;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public Integer getMesesVigencia() {
        return mesesVigencia;
    }

    public BigDecimal getCanonMensual() {
        return canonMensual;
    }

    public BigDecimal getPrima() {
        return prima;
    }

    public List<Riesgo> getRiesgos() {
        return riesgos;
    }

    public void actualizarRenovacion(LocalDate nuevaFechaInicio, LocalDate nuevaFechaFin,
                                     BigDecimal nuevoCanonMensual, BigDecimal nuevaPrima) {
        this.fechaInicio = nuevaFechaInicio;
        this.fechaFin = nuevaFechaFin;
        this.canonMensual = nuevoCanonMensual;
        this.prima = nuevaPrima;
        this.estado = EstadoPoliza.RENOVADA;
    }

    public void cancelar() {
        this.estado = EstadoPoliza.CANCELADA;
        riesgos.forEach(Riesgo::cancelar);
    }
}
