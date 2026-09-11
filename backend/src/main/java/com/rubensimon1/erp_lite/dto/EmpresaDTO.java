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
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String cif;
    private String direccion;
    private String telefono;
    private Boolean sigueAhi;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
