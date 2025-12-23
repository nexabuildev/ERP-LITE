package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmpleadoInputDTO {

    /*
     * Reglas de validacion:
     * 1. nombre: No puede estar vacio
     * 2. email: No puede estar vacio y debe ser un email valido
     * 3. departamentoId: No puede estar vacio
     */
    @NotBlank(message = "El nombre es obligatorio") // Regla 1
    private String nombre;

    @NotBlank
    @Email(message = "El formato del email es invalido") // Regla 2
    private String email;

    @NotNull(message = "Debes especificar un departamento") // Regla 3
    private Long departamentoId; // Solo el ID es necesario para vincular

    // Getters y Setters (generados automaticamente por Lombok)
}
