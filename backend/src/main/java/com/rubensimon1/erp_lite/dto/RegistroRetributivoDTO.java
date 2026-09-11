package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistroRetributivoDTO {
    private Double miSalario;
    private String miCategoria;
    private Double mediaCategoria; // media salarial de mi misma categoria profesional
    private Double mediaHombres;
    private Double mediaMujeres;
    private Double brechaPorcentaje; // diferencia entre mediaHombres y mediaMujeres, en %
}
