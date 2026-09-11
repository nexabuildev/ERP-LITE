package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DeclaracionPresentadaInputDTO {

    @NotBlank(message = "Indica el modelo (Ej: 130, 303, Renta)")
    private String modelo;

    @NotBlank(message = "Indica el periodo (Ej: 2026 - T2)")
    private String periodo;

    @NotNull(message = "La fecha de presentacion es obligatoria")
    private LocalDate fechaPresentacion;

    private Double importe;
    private String notas;
}
