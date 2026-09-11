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
    private String dni;
    private String numeroSeguridadSocial;
    private String departamentoNombre;
    private TipoTrabajador tipoTrabajador;
    private Genero genero;
    private String categoriaProfesional;
    private Double salarioBrutoAnual;
    private String nif;
    private String epigrafeIae;
    private LocalDate fechaAltaAutonomo;

    private String calle;
    private String numero;
    private String piso;
    private String codigoPostal;
    private String ciudad;
    private String provincia;

    private String empresaNombre;
    private String empresaCif;
    private String empresaDireccion;
    private String empresaTelefono;

    private String grupoSanguineo;
    private String alergias;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private String seguroMedico;
    private Boolean activa;
}
