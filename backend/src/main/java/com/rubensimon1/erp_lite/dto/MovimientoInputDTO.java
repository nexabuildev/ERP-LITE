package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.TipoMovimiento;
import com.rubensimon1.erp_lite.entity.TipoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MovimientoInputDTO {

    @NotBlank(message = "El concepto es obligatorio")
    private String concepto;

    @NotNull
    @Positive(message = "El importe debe ser mayor que cero")
    private Double importe;

    @NotNull
    private TipoMovimiento tipo;

    @NotNull
    private TipoPago medioPago;

    @NotNull
    private LocalDate fecha;
}
