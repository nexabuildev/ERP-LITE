package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmpresaInputDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String nombre;

    private String cif;
    private String direccion;
    private String telefono;

    @NotNull(message = "Indica si sigues en la empresa")
    private Boolean sigueAhi;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    // Solo aplica si sigueAhi = false
    private LocalDate fechaFin;
}
