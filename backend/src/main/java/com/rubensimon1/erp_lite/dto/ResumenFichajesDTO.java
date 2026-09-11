package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumenFichajesDTO {
    private boolean fichado; // true si tiene un fichaje abierto ahora mismo
    private double horasMesActual;
    private List<FichajeDTO> fichajes;
}
