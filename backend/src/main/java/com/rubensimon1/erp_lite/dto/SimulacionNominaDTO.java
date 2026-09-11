package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimulacionNominaDTO {
    private Double salarioActualBrutoAnual;
    private Double salarioActualNetoMensual;
    private Double nuevoSalarioBrutoAnual;
    private Double nuevoSalarioNetoMensual;
    private Double diferenciaMensual;
    private Double diferenciaPorcentaje;
}
