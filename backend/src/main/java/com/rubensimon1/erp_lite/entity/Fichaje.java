package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "fichajes")
@Getter
@Setter
@NoArgsConstructor
public class Fichaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private LocalDateTime fechaHoraEntrada;

    // Null mientras el empleado sigue fichado (no ha marcado salida)
    private LocalDateTime fechaHoraSalida;
}
