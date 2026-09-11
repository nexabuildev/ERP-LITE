package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "metas_ahorro")
@Getter
@Setter
@NoArgsConstructor
public class MetaAhorro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Empleado empleado;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double montoObjetivo;

    @Column(nullable = false)
    private Double montoActual;

    private LocalDate fechaObjetivo;

    // Cuenta (o efectivo) de la que sale/entra el dinero al aportar/retirar
    @ManyToOne
    @JoinColumn(name = "cuenta_origen_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private CuentaBancaria cuentaOrigen;
}
