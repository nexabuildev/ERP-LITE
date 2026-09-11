package com.rubensimon1.erp_lite.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "declaraciones_presentadas")
@Getter
@Setter
@NoArgsConstructor
public class DeclaracionPresentada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Empleado empleado;

    @Column(nullable = false)
    private String modelo; // Ej: "130", "303", "Renta"

    @Column(nullable = false)
    private String periodo; // Ej: "2026 - T2", "2025 Anual"

    @Column(nullable = false)
    private LocalDate fechaPresentacion;

    private Double importe; // resultado a pagar/devolver, opcional

    private String notas;

    // --- Archivo adjunto (PDF/imagen de la declaracion real), opcional ---
    private String archivoNombre;
    private String archivoTipo;
    private byte[] archivoContenido;
}
