package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.CategoriaCuenta;
import com.rubensimon1.erp_lite.entity.TipoCuenta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CuentaBancariaInputDTO {

    @NotBlank(message = "El alias es obligatorio")
    private String alias;

    @NotNull(message = "Indica si es una cuenta de banco o efectivo")
    private CategoriaCuenta categoria;

    @NotNull(message = "Indica el tipo de cuenta")
    private TipoCuenta tipoCuenta;

    // Solo obligatorio si categoria = BANCO
    private String iban;
    private String banco;

    private Double saldoActual;
}
