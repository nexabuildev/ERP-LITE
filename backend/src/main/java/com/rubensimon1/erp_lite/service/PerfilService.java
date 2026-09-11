package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.PerfilDTO;
import com.rubensimon1.erp_lite.dto.PerfilUpdateDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.TipoTrabajador;
import com.rubensimon1.erp_lite.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final EmpleadoRepository empleadoRepository;

    public PerfilDTO miPerfil(Empleado empleado) {
        return toDTO(empleado);
    }

    public PerfilDTO actualizar(Empleado empleado, PerfilUpdateDTO input) {
        empleado.setNombre(input.getNombre());
        empleado.setTipoTrabajador(input.getTipoTrabajador());
        empleado.setGenero(input.getGenero());

        if (input.getTipoTrabajador() == TipoTrabajador.AUTONOMO) {
            empleado.setNif(input.getNif());
            empleado.setEpigrafeIae(input.getEpigrafeIae());
            empleado.setFechaAltaAutonomo(input.getFechaAltaAutonomo());
        } else {
            empleado.setNif(null);
            empleado.setEpigrafeIae(null);
            empleado.setFechaAltaAutonomo(null);
        }

        empleadoRepository.save(empleado);
        return toDTO(empleado);
    }

    private PerfilDTO toDTO(Empleado e) {
        return PerfilDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .email(e.getEmail())
                .role(e.getRole())
                .departamentoNombre(e.getDepartamento() != null ? e.getDepartamento().getNombre() : null)
                .tipoTrabajador(e.getTipoTrabajador())
                .genero(e.getGenero())
                .categoriaProfesional(e.getCategoriaProfesional())
                .salarioBrutoAnual(e.getSalarioBrutoAnual())
                .nif(e.getNif())
                .epigrafeIae(e.getEpigrafeIae())
                .fechaAltaAutonomo(e.getFechaAltaAutonomo())
                .build();
    }
}
