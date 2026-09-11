package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.EstadoAlerta;
import com.rubensimon1.erp_lite.entity.TipoAlerta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertaDTO {
    private Long id;
    private String titulo;
    private LocalDate fechaVencimiento;
    private String notas;
    private long diasRestantes;
    private EstadoAlerta estado;
    private TipoAlerta tipo;
    private LocalDate fechaCita;
}
