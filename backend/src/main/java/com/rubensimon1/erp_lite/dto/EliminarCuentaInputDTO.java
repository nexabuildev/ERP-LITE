package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EliminarCuentaInputDTO {

    @NotBlank(message = "Debes escribir la palabra de confirmación")
    private String confirmacion;

    @NotBlank(message = "Debes indicar tu contraseña actual")
    private String passwordActual;
}
