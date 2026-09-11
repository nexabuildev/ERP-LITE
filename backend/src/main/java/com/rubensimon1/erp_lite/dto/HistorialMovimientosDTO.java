package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HistorialMovimientosDTO {
    private List<PuntoHistorialDTO> puntos;
    private Double variacionPorcentaje; // neto del ultimo mes vs el anterior; null si no hay suficientes datos
}
