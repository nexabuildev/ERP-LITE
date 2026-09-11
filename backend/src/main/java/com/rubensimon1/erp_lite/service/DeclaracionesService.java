package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.DeclaracionDTO;
import com.rubensimon1.erp_lite.dto.DeclaracionPresentadaDTO;
import com.rubensimon1.erp_lite.dto.DeclaracionPresentadaInputDTO;
import com.rubensimon1.erp_lite.entity.DeclaracionPresentada;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.TipoTrabajador;
import com.rubensimon1.erp_lite.repository.DeclaracionPresentadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DeclaracionesService {

    private final DeclaracionPresentadaRepository declaracionPresentadaRepository;

    public List<DeclaracionDTO> misDeclaraciones(Empleado empleado) {
        LocalDate hoy = LocalDate.now();
        List<DeclaracionDTO> declaraciones = new ArrayList<>();

        TipoTrabajador tipo = empleado.getTipoTrabajador() != null ? empleado.getTipoTrabajador() : TipoTrabajador.ASALARIADO;

        switch (tipo) {
            case AUTONOMO -> {
                LocalDate proximoTrimestral = proximaFechaTrimestral(hoy);
                declaraciones.add(crear("Modelo 303 · IVA trimestral", "Autoliquidacion trimestral del IVA", proximoTrimestral, hoy));
                declaraciones.add(crear("Modelo 130 · Pago fraccionado IRPF", "Pago a cuenta trimestral del IRPF", proximoTrimestral, hoy));
                declaraciones.add(crear("Modelo 390 · Resumen anual IVA", "Declaracion-resumen anual del IVA", proximaFecha(1, 30, hoy), hoy));
            }
            case ASALARIADO -> declaraciones.add(crear("Declaracion de la Renta (IRPF)", "Campana anual de la Renta", proximaFecha(6, 30, hoy), hoy));
            case DESEMPLEADO, ESTUDIANTE -> {
                // No suelen tener obligaciones fiscales periódicas propias: sin declaraciones automáticas.
            }
        }

        declaraciones.sort(Comparator.comparing(DeclaracionDTO::getFechaLimite));
        return declaraciones;
    }

    public DeclaracionPresentadaDTO registrarPresentada(Empleado empleado, DeclaracionPresentadaInputDTO input, MultipartFile archivo) {
        DeclaracionPresentada d = new DeclaracionPresentada();
        d.setEmpleado(empleado);
        d.setModelo(input.getModelo());
        d.setPeriodo(input.getPeriodo());
        d.setFechaPresentacion(input.getFechaPresentacion());
        d.setImporte(input.getImporte());
        d.setNotas(input.getNotas());

        if (archivo != null && !archivo.isEmpty()) {
            try {
                d.setArchivoNombre(archivo.getOriginalFilename());
                d.setArchivoTipo(archivo.getContentType());
                d.setArchivoContenido(archivo.getBytes());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo leer el archivo adjunto");
            }
        }

        declaracionPresentadaRepository.save(d);
        return toPresentadaDTO(d);
    }

    public DeclaracionPresentada obtenerArchivo(Empleado empleado, Long id) {
        DeclaracionPresentada d = declaracionPresentadaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Declaracion no encontrada"));

        if (!d.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a una declaracion de otro empleado");
        }
        if (d.getArchivoContenido() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Esta declaracion no tiene archivo adjunto");
        }
        return d;
    }

    public List<DeclaracionPresentadaDTO> misPresentadas(Empleado empleado) {
        return declaracionPresentadaRepository.findByEmpleadoOrderByFechaPresentacionDesc(empleado)
                .stream().map(this::toPresentadaDTO).collect(Collectors.toList());
    }

    public void eliminarPresentada(Empleado empleado, Long id) {
        DeclaracionPresentada d = declaracionPresentadaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Declaracion no encontrada"));

        if (!d.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar una declaracion de otro empleado");
        }

        declaracionPresentadaRepository.delete(d);
    }

    private DeclaracionPresentadaDTO toPresentadaDTO(DeclaracionPresentada d) {
        return DeclaracionPresentadaDTO.builder()
                .id(d.getId())
                .modelo(d.getModelo())
                .periodo(d.getPeriodo())
                .fechaPresentacion(d.getFechaPresentacion())
                .importe(d.getImporte())
                .notas(d.getNotas())
                .tieneArchivo(d.getArchivoContenido() != null)
                .archivoNombre(d.getArchivoNombre())
                .archivoTipo(d.getArchivoTipo())
                .build();
    }

    private DeclaracionDTO crear(String nombre, String descripcion, LocalDate fechaLimite, LocalDate hoy) {
        return DeclaracionDTO.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .fechaLimite(fechaLimite)
                .diasRestantes(ChronoUnit.DAYS.between(hoy, fechaLimite))
                .build();
    }

    private LocalDate proximaFecha(int mes, int dia, LocalDate hoy) {
        LocalDate fecha = LocalDate.of(hoy.getYear(), mes, dia);
        if (fecha.isBefore(hoy)) {
            fecha = fecha.plusYears(1);
        }
        return fecha;
    }

    private LocalDate proximaFechaTrimestral(LocalDate hoy) {
        return Stream.of(
                proximaFecha(4, 20, hoy),
                proximaFecha(7, 20, hoy),
                proximaFecha(10, 20, hoy),
                proximaFecha(1, 30, hoy)
        ).min(LocalDate::compareTo).orElseThrow();
    }
}
