package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Empleado empleado;

    @Column(nullable = false)
    private String nombre;

    private String cif;
    private String direccion;
    private String telefono;

    @Column(nullable = false)
    private Boolean sigueAhi = false;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    // Nula si sigueAhi = true
    private LocalDate fechaFin;
}
