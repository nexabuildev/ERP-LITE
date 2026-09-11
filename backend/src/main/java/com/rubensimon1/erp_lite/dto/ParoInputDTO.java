package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ParoInputDTO {

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    // Fecha fin prevista o real; nula si sigue en curso
    private LocalDate fechaFin;

    private Double dineroRecibido;

    private String notas;
}
