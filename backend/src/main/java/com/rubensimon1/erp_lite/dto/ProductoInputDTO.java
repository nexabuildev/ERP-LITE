package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductoInputDTO {
    /*
     * Reglas de validacion:
     * 1. nombre: No puede estar vacio
     * 2. descripcion: No puede estar vacio
     * 3. precio: No puede estar vacio
     * 4. stock: No puede estar vacio
     * 5. departamentoId: No puede estar vacio
     */
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio tiene que ser entre 0 y 999.999")
    private Double precio;

    @NotNull(message = "El stock debe esatr entre 1 a 999")
    private Integer stock;

    @NotNull(message = "Debes especificar un departamento")
    private Long departamentoId;
}
