package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistroTeletrabajoDTO {
    private Long id;
    private Integer mes;
    private Integer anio;
    private Integer diasTeletrabajados;
    private Double compensacion;
}
