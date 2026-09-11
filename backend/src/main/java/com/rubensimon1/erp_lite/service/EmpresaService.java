package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.EmpresaDTO;
import com.rubensimon1.erp_lite.dto.EmpresaInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Empresa;
import com.rubensimon1.erp_lite.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaDTO crear(Empleado empleado, EmpresaInputDTO input) {
        Empresa empresa = new Empresa();
        empresa.setEmpleado(empleado);
        aplicarCambios(empresa, input);

        empresaRepository.save(empresa);
        return toDTO(empresa);
    }

    public List<EmpresaDTO> misEmpresas(Empleado empleado) {
        return empresaRepository.findByEmpleadoOrderByFechaInicioDesc(empleado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EmpresaDTO actualizar(Empleado empleado, Long id, EmpresaInputDTO input) {
        Empresa empresa = obtenerPropia(empleado, id);
        aplicarCambios(empresa, input);

        empresaRepository.save(empresa);
        return toDTO(empresa);
    }

    public void eliminar(Empleado empleado, Long id) {
        empresaRepository.delete(obtenerPropia(empleado, id));
    }

    private void aplicarCambios(Empresa empresa, EmpresaInputDTO input) {
        empresa.setNombre(input.getNombre());
        empresa.setCif(input.getCif());
        empresa.setDireccion(input.getDireccion());
        empresa.setTelefono(input.getTelefono());
        empresa.setSigueAhi(Boolean.TRUE.equals(input.getSigueAhi()));
        empresa.setFechaInicio(input.getFechaInicio());
        empresa.setFechaFin(empresa.getSigueAhi() ? null : input.getFechaFin());
    }

    private Empresa obtenerPropia(Empleado empleado, Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));

        if (!empresa.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar una empresa de otro empleado");
        }
        return empresa;
    }

    private EmpresaDTO toDTO(Empresa e) {
        return EmpresaDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .cif(e.getCif())
                .direccion(e.getDireccion())
                .telefono(e.getTelefono())
                .sigueAhi(e.getSigueAhi())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .build();
    }
}
