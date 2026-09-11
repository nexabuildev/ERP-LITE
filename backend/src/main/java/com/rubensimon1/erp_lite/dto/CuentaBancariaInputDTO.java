package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CuentaBancariaInputDTO {

    @NotBlank(message = "El alias es obligatorio")
    private String alias;

    @NotBlank(message = "El IBAN es obligatorio")
    private String iban;

    private String banco;
    private boolean principal;
}
