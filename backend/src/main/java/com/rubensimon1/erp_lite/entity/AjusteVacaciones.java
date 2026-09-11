package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "ajustes_vacaciones")
@Getter
@Setter
@NoArgsConstructor
public class AjusteVacaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private Integer dias; // positivo (suma dias) o negativo (resta dias)

    @Column(nullable = false)
    private String concepto;

    @Column(nullable = false)
    private LocalDate fecha;
}
