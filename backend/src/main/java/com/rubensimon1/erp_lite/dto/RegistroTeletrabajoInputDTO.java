package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistroTeletrabajoInputDTO {

    @NotNull
    @Min(1)
    @Max(12)
    private Integer mes;

    @NotNull
    private Integer anio;

    @NotNull(message = "Indica los dias teletrabajados")
    @Min(value = 0, message = "No puede ser negativo")
    @Max(value = 31, message = "No puede superar los dias del mes")
    private Integer diasTeletrabajados;
}
