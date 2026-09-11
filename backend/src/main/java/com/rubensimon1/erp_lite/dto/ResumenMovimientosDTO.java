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
public class ResumenMovimientosDTO {
    private double saldo;
    private double totalIngresos;
    private double totalGastos;
    private List<MovimientoDTO> movimientos;
}
