package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PuntoHistorialDTO {
    private String periodo; // "2026-08"
    private String etiqueta; // "Ago 2026"
    private double ingresos;
    private double gastos;
    private double neto;
    private double saldoAcumulado;
}
