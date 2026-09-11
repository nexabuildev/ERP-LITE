package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AjusteVacacionesInputDTO {

    @NotNull(message = "Indica los dias (positivo para sumar, negativo para restar)")
    private Integer dias;

    @NotBlank(message = "El concepto es obligatorio")
    private String concepto;
}
