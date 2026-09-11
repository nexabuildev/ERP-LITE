package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.Genero;
import com.rubensimon1.erp_lite.entity.TipoTrabajador;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PerfilUpdateDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private TipoTrabajador tipoTrabajador;
    private Genero genero;

    // Solo se usan si tipoTrabajador = AUTONOMO
    private String nif;
    private String epigrafeIae;
    private LocalDate fechaAltaAutonomo;
}
