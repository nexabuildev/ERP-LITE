package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WrappedDTO {
    private String mesEtiqueta;
    private Double ingresos;
    private Double gastos;
    private Double balance;
    private Double variacionBalancePorcentaje; // vs. mes anterior
    private String categoriaTopGasto;
    private Double importeCategoriaTopGasto;
    private Integer diasVacacionesDisfrutados;
    private Integer horasTrabajadas;
    private Integer diasTeletrabajados;
}
