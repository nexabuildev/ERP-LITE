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
public class NominaDTO {
    private Long id;
    private Integer mes;
    private Integer anio;
    private Double salarioBruto;
    private Double deducciones;
    private Double salarioNeto;
    private LocalDate fechaPago;
    private boolean tieneArchivo;
    private String archivoNombre;
    private String archivoTipo;
}
