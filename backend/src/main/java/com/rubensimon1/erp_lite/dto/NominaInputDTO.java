package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NominaInputDTO {

    @NotNull
    @Min(1)
    @Max(12)
    private Integer mes;

    @NotNull
    private Integer anio;

    @NotNull(message = "Indica el salario bruto")
    private Double salarioBruto;

    @NotNull(message = "Indica las deducciones")
    private Double deducciones;

    @NotNull(message = "Indica el salario neto")
    private Double salarioNeto;

    @NotNull(message = "Indica la fecha de pago")
    private LocalDate fechaPago;
}
