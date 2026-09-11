package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.SaldoVacacionesDTO;
import com.rubensimon1.erp_lite.dto.SolicitudVacacionesDTO;
import com.rubensimon1.erp_lite.dto.SolicitudVacacionesInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.EstadoVacacion;
import com.rubensimon1.erp_lite.entity.SolicitudVacaciones;
import com.rubensimon1.erp_lite.repository.SolicitudVacacionesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacacionesService {

    private static final int DIAS_ANUALES = 23; // minimo legal orientativo (dias laborables/anio)

    private final SolicitudVacacionesRepository vacacionesRepository;

    public SolicitudVacacionesDTO solicitar(Empleado empleado, SolicitudVacacionesInputDTO input) {
        if (input.getFechaFin().isBefore(input.getFechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de fin no puede ser anterior a la de inicio");
        }

        SolicitudVacaciones solicitud = new SolicitudVacaciones();
        solicitud.setEmpleado(empleado);
        solicitud.setFechaInicio(input.getFechaInicio());
        solicitud.setFechaFin(input.getFechaFin());
        solicitud.setMotivo(input.getMotivo());
        solicitud.setEstado(EstadoVacacion.PENDIENTE);
        solicitud.setFechaSolicitud(LocalDateTime.now());

        vacacionesRepository.save(solicitud);
        return toDTO(solicitud);
    }

    public List<SolicitudVacacionesDTO> misSolicitudes(Empleado empleado) {
        return vacacionesRepository.findByEmpleadoOrderByFechaSolicitudDesc(empleado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public SaldoVacacionesDTO miSaldo(Empleado empleado) {
        int diasAprobados = vacacionesRepository.findByEmpleadoAndEstado(empleado, EstadoVacacion.APROBADA)
                .stream()
                .mapToInt(this::contarDiasLaborables)
                .sum();

        return SaldoVacacionesDTO.builder()
                .diasAnuales(DIAS_ANUALES)
                .diasAprobados(diasAprobados)
                .diasRestantes(Math.max(0, DIAS_ANUALES - diasAprobados))
                .build();
    }

    public List<SolicitudVacacionesDTO> pendientes() {
        return vacacionesRepository.findByEstadoOrderByFechaSolicitudAsc(EstadoVacacion.PENDIENTE)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public SolicitudVacacionesDTO cambiarEstado(Long id, EstadoVacacion nuevoEstado) {
        SolicitudVacaciones solicitud = vacacionesRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        solicitud.setEstado(nuevoEstado);
        vacacionesRepository.save(solicitud);
        return toDTO(solicitud);
    }

    private int contarDiasLaborables(SolicitudVacaciones s) {
        return (int) ChronoUnit.DAYS.between(s.getFechaInicio(), s.getFechaFin()) + 1;
    }

    private SolicitudVacacionesDTO toDTO(SolicitudVacaciones s) {
        return SolicitudVacacionesDTO.builder()
                .id(s.getId())
                .fechaInicio(s.getFechaInicio())
                .fechaFin(s.getFechaFin())
                .motivo(s.getMotivo())
                .estado(s.getEstado())
                .fechaSolicitud(s.getFechaSolicitud())
                .empleadoNombre(s.getEmpleado().getNombre())
                .build();
    }
}
