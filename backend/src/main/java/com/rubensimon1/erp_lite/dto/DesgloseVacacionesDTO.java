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
public class DesgloseVacacionesDTO {
    private LocalDate fecha;
    private String concepto;
    private int dias; // positivo o negativo
    private String tipo; // "AJUSTE" o "VACACIONES"
}
