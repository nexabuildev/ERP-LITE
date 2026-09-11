package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "nominas")
@Getter
@Setter
@NoArgsConstructor
public class Nomina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Empleado empleado;

    @Column(nullable = false)
    private Integer mes; // 1-12

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false)
    private Double salarioBruto;

    @Column(nullable = false)
    private Double deducciones;

    @Column(nullable = false)
    private Double salarioNeto;

    @Column(nullable = false)
    private LocalDate fechaPago;

    // --- Archivo adjunto (PDF/imagen de la nomina real), opcional ---
    private String archivoNombre;
    private String archivoTipo; // content-type, ej: application/pdf

    private byte[] archivoContenido;
}
