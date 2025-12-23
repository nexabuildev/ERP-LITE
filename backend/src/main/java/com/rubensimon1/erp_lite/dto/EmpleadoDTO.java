package com.rubensimon1.erp_lite.dto;

import lombok.Data;

@Data // Lombok genera getters/setters
public class EmpleadoDTO {
    /*
     * Reglas de validacion:
     * 1. id: No puede estar vacio
     * 2. nombre: No puede estar vacio
     * 3. email: No puede estar vacio y debe ser un email valido
     * 4. departamentoId: No puede estar vacio
     */
    private Long id;
    private String nombre;
    private String email;

    /*
     * Truco: En vez de devolver el objeto Departamento entero...
     */
    private Long departamentoId;
    private String nombreDepartamento;
}
