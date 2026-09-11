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
public class DeclaracionDTO {
    private String nombre;
    private String descripcion;
    private LocalDate fechaLimite;
    private long diasRestantes;
}
