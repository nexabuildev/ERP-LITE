package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.EstadoVacacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudVacacionesDTO {
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivo;
    private EstadoVacacion estado;
    private LocalDateTime fechaSolicitud;
    private String empleadoNombre; // relevante en la vista de administración
}
