package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaAhorroDTO {
    private Long id;
    private String nombre;
    private Double montoObjetivo;
    private Double montoActual;
    private LocalDate fechaObjetivo;
    private double porcentajeCompletado;
    private Long cuentaOrigenId;
    private String cuentaOrigenAlias;
}
