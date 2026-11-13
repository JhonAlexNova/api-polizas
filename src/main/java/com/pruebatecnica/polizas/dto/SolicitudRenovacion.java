package com.pruebatecnica.polizas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SolicitudRenovacion(
        @NotNull(message = "El IPC es obligatorio")
        @DecimalMin(value = "0.0", message = "El IPC no puede ser negativo")
        BigDecimal ipc
) {
}
