package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.Genero;
import com.rubensimon1.erp_lite.entity.Role;
import com.rubensimon1.erp_lite.entity.TipoTrabajador;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PerfilDTO {
    private Long id;
    private String nombre;
    private String email;
    private Role role;
    private String departamentoNombre;
    private TipoTrabajador tipoTrabajador;
    private Genero genero;
    private String categoriaProfesional;
    private Double salarioBrutoAnual;
    private String nif;
    private String epigrafeIae;
    private LocalDate fechaAltaAutonomo;
}
