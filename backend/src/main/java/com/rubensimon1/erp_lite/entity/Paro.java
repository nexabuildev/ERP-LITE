package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "periodos_paro")
@Getter
@Setter
@NoArgsConstructor
public class Paro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Empleado empleado;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    // Nula si sigue en curso (fecha fin prevista aún no definida)
    private LocalDate fechaFin;

    private Double dineroRecibido;

    private String notas;
}
