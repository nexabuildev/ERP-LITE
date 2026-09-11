package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.ParoDTO;
import com.rubensimon1.erp_lite.dto.ParoInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Paro;
import com.rubensimon1.erp_lite.repository.ParoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParoService {

    private final ParoRepository paroRepository;

    public ParoDTO crear(Empleado empleado, ParoInputDTO input) {
        Paro paro = new Paro();
        paro.setEmpleado(empleado);
        aplicarCambios(paro, input);

        paroRepository.save(paro);
        return toDTO(paro);
    }

    public List<ParoDTO> misPeriodos(Empleado empleado) {
        return paroRepository.findByEmpleadoOrderByFechaInicioDesc(empleado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ParoDTO actualizar(Empleado empleado, Long id, ParoInputDTO input) {
        Paro paro = obtenerPropio(empleado, id);
        aplicarCambios(paro, input);

        paroRepository.save(paro);
        return toDTO(paro);
    }

    public void eliminar(Empleado empleado, Long id) {
        paroRepository.delete(obtenerPropio(empleado, id));
    }

    private void aplicarCambios(Paro paro, ParoInputDTO input) {
        paro.setFechaInicio(input.getFechaInicio());
        paro.setFechaFin(input.getFechaFin());
        paro.setDineroRecibido(input.getDineroRecibido());
        paro.setNotas(input.getNotas());
    }

    private Paro obtenerPropio(Empleado empleado, Long id) {
        Paro paro = paroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo de paro no encontrado"));

        if (!paro.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar un periodo de otro empleado");
        }
        return paro;
    }

    private ParoDTO toDTO(Paro p) {
        LocalDate hoy = LocalDate.now();
        LocalDate finParaTranscurridos = p.getFechaFin() != null && p.getFechaFin().isBefore(hoy) ? p.getFechaFin() : hoy;
        long diasTranscurridos = ChronoUnit.DAYS.between(p.getFechaInicio(), finParaTranscurridos) + 1;

        boolean enCurso = p.getFechaFin() == null || !p.getFechaFin().isBefore(hoy);
        Long diasRestantes = (p.getFechaFin() != null && !p.getFechaFin().isBefore(hoy))
                ? ChronoUnit.DAYS.between(hoy, p.getFechaFin())
                : null;

        return ParoDTO.builder()
                .id(p.getId())
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .dineroRecibido(p.getDineroRecibido())
                .notas(p.getNotas())
                .diasTranscurridos(Math.max(diasTranscurridos, 0))
                .diasRestantes(diasRestantes)
                .enCurso(enCurso)
                .build();
    }
}
