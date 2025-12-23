package com.rubensimon1.erp_lite.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    /*
     * Reglas de validacion:
     * 1. id: No puede estar vacio
     * 2. nombre: No puede estar vacio
     * 3. descripcion: No puede estar vacio
     * 4. precio: No puede estar vacio
     * 5. stock: No puede estar vacio
     * 6. departamentoId: No puede estar vacio
     */
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;

    /*
     * Truco: En vez de devolver el objeto Departamento entero...
     */
    private Long departamentoId;
    private String nombreDepartamento;
}
