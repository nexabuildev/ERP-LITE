package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CambiarCredencialesDTO {

    @NotBlank(message = "Debes indicar tu contraseña actual")
    private String passwordActual;

    // Opcionales: solo se actualiza lo que venga relleno
    private String nuevoEmail;
    private String nuevaPassword;
}
