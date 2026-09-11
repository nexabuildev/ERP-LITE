package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.CategoriaCuenta;
import com.rubensimon1.erp_lite.entity.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CuentaBancariaDTO {
    private Long id;
    private String alias;
    private CategoriaCuenta categoria;
    private TipoCuenta tipoCuenta;
    private String iban;
    private String banco;
    private Double saldoActual;
}
