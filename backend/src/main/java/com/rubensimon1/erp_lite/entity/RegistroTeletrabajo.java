package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "registros_teletrabajo", uniqueConstraints = @UniqueConstraint(columnNames = { "empleado_id", "mes", "anio" }))
@Getter
@Setter
@NoArgsConstructor
public class RegistroTeletrabajo {

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
    private Integer diasTeletrabajados;
}
