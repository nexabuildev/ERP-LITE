package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.TipoAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AlertaInputDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;

    private String notas;

    private TipoAlerta tipo;

    // Solo obligatorio si tipo = CITA_PREVIA
    private LocalDate fechaCita;
}
