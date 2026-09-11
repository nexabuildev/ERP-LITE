package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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
}
