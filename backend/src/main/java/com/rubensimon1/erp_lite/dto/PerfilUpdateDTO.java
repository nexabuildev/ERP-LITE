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

    private String dni;
    private String numeroSeguridadSocial;

    private TipoTrabajador tipoTrabajador;
    private Genero genero;

    // Solo se usan si tipoTrabajador = AUTONOMO
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
}
