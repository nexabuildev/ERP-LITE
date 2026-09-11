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
public class ParoDTO {
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double dineroRecibido;
    private String notas;
    private long diasTranscurridos;
    private Long diasRestantes; // null si no hay fechaFin, o si ya ha terminado
    private boolean enCurso;
}
