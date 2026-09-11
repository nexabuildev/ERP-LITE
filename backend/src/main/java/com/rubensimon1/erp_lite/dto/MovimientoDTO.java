package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.TipoMovimiento;
import com.rubensimon1.erp_lite.entity.TipoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoDTO {
    private Long id;
    private String concepto;
    private Double importe;
    private TipoMovimiento tipo;
    private TipoPago medioPago;
    private LocalDate fecha;
    private Long cuentaBancariaId;
    private String cuentaBancariaAlias;
    private Long metodoPagoId;
    private String metodoPagoAlias;
}
