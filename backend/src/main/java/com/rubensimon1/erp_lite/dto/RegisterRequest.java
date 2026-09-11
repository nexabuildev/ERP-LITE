package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank
    @Email(message = "El formato del email es invalido")
    private String email;

    @NotBlank
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String password;
}
