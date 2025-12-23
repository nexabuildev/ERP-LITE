package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Clase Producto
 */
@Entity // Indica que esta clase es una entidad (Tabla en la BBDD)
@Table(name = "productos") // Indica el nombre de la tabla en la BBDD
@Getter // Genera los getters
@Setter // Genera los setters
@NoArgsConstructor // Genera el constructor por defecto
public class Producto {
    /*
     * Atributos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Relacion con departamento
     */
    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    /*
     * Atributos
     */
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
}
