package com.rubensimon1.erp_lite.entity;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // <--- Indica que esta clase es una entidad
@Table(name = "departamentos") // <--- Indica el nombre de la tabla en la base de datos
@Getter // <--- Genera los getters
@Setter // <--- Genera los setters
@NoArgsConstructor // <--- Genera el constructor por defecto
public class Departamento {
    /*
     * Atributos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String codigo;

    /*
     * Relacion con Empleado
     */
    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL) // <--- Esto crea la columna FK en la tabla SQL
    @JsonIgnore
    private List<Empleado> empleados;

    /*
     * Relacion con Producto
     */
    @OneToMany(mappedBy = "departamento")
    @JsonIgnore
    private List<Producto> productos;
}
