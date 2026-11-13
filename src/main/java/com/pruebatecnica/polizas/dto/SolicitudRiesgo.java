package com.pruebatecnica.polizas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SolicitudRiesgo(
        @NotBlank(message = "El inmueble es obligatorio")
        @Size(max = 255, message = "El inmueble no puede superar 255 caracteres")
        String inmueble
) {
}
