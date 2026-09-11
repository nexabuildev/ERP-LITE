package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MetaAhorroInputDTO {

    @NotBlank(message = "El nombre de la meta es obligatorio")
    private String nombre;

    @NotNull
    @Positive(message = "El objetivo debe ser mayor que cero")
    private Double montoObjetivo;

    private LocalDate fechaObjetivo;
}
