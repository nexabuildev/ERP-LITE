package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SimulacionNominaInputDTO {

    @NotNull
    @Positive(message = "El salario debe ser mayor que cero")
    private Double nuevoSalarioBrutoAnual;
}
