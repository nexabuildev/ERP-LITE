package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IncrementoGastoDTO {
    private String concepto;
    private LocalDate fechaAnterior;
    private Double importeAnterior;
    private LocalDate fechaActual;
    private Double importeActual;
    private Double incrementoPorcentaje;
}
