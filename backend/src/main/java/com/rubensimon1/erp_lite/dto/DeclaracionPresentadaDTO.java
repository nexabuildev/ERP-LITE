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
public class DeclaracionPresentadaDTO {
    private Long id;
    private String modelo;
    private String periodo;
    private LocalDate fechaPresentacion;
    private Double importe;
    private String notas;
    private boolean tieneArchivo;
    private String archivoNombre;
    private String archivoTipo;
}
