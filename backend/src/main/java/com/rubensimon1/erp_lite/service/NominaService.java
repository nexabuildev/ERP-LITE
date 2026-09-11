package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.NominaDTO;
import com.rubensimon1.erp_lite.dto.NominaInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Nomina;
import com.rubensimon1.erp_lite.repository.NominaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NominaService {

    // Aproximacion de IRPF + Seguridad Social a cargo del trabajador, usada
    // tanto para sembrar nominas de ejemplo como para el simulador de sueldo.
    public static final double TASA_DEDUCCION_APROX = 0.22;

    private final NominaRepository nominaRepository;

    public NominaDTO crear(Empleado empleado, NominaInputDTO input, MultipartFile archivo) {
        Nomina nomina = new Nomina();
        nomina.setEmpleado(empleado);
        nomina.setMes(input.getMes());
        nomina.setAnio(input.getAnio());
        nomina.setSalarioBruto(input.getSalarioBruto());
        nomina.setDeducciones(input.getDeducciones());
        nomina.setSalarioNeto(input.getSalarioNeto());
        nomina.setFechaPago(input.getFechaPago());

        if (archivo != null && !archivo.isEmpty()) {
            try {
                nomina.setArchivoNombre(archivo.getOriginalFilename());
                nomina.setArchivoTipo(archivo.getContentType());
                nomina.setArchivoContenido(archivo.getBytes());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer el archivo adjunto");
            }
        }

        nominaRepository.save(nomina);
        return toDTO(nomina);
    }

    public List<NominaDTO> misNominas(Empleado empleado) {
        return nominaRepository.findByEmpleadoOrderByAnioDescMesDesc(empleado)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void eliminar(Empleado empleado, Long id) {
        Nomina nomina = obtenerPropia(empleado, id);
        nominaRepository.delete(nomina);
    }

    public Nomina obtenerArchivo(Empleado empleado, Long id) {
        Nomina nomina = obtenerPropia(empleado, id);
        if (nomina.getArchivoContenido() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Esta nomina no tiene archivo adjunto");
        }
        return nomina;
    }

    private Nomina obtenerPropia(Empleado empleado, Long id) {
        Nomina nomina = nominaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nomina no encontrada"));

        if (!nomina.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a una nomina de otro empleado");
        }
        return nomina;
    }

    private NominaDTO toDTO(Nomina n) {
        return NominaDTO.builder()
                .id(n.getId())
                .mes(n.getMes())
                .anio(n.getAnio())
                .salarioBruto(n.getSalarioBruto())
                .deducciones(n.getDeducciones())
                .salarioNeto(n.getSalarioNeto())
                .fechaPago(n.getFechaPago())
                .tieneArchivo(n.getArchivoContenido() != null)
                .archivoNombre(n.getArchivoNombre())
                .archivoTipo(n.getArchivoTipo())
                .build();
    }
}
