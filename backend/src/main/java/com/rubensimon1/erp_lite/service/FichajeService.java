package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.FichajeDTO;
import com.rubensimon1.erp_lite.dto.ResumenFichajesDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Fichaje;
import com.rubensimon1.erp_lite.repository.FichajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FichajeService {

    private final FichajeRepository fichajeRepository;

    public FichajeDTO ficharEntrada(Empleado empleado) {
        fichajeRepository.findByEmpleadoAndFechaHoraSalidaIsNull(empleado).ifPresent(f -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya tienes un fichaje abierto, marca la salida primero");
        });

        Fichaje fichaje = new Fichaje();
        fichaje.setEmpleado(empleado);
        fichaje.setFechaHoraEntrada(LocalDateTime.now());
        fichajeRepository.save(fichaje);

        return toDTO(fichaje);
    }

    public FichajeDTO ficharSalida(Empleado empleado) {
        Fichaje fichaje = fichajeRepository.findByEmpleadoAndFechaHoraSalidaIsNull(empleado)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No tienes ningun fichaje abierto"));

        fichaje.setFechaHoraSalida(LocalDateTime.now());
        fichajeRepository.save(fichaje);

        return toDTO(fichaje);
    }

    public ResumenFichajesDTO resumen(Empleado empleado) {
        boolean fichado = fichajeRepository.findByEmpleadoAndFechaHoraSalidaIsNull(empleado).isPresent();

        YearMonth mesActual = YearMonth.now();
        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(23, 59, 59);

        List<Fichaje> fichajesDelMes = fichajeRepository.findByEmpleadoAndFechaHoraEntradaBetween(empleado, inicioMes, finMes);

        double horasMes = fichajesDelMes.stream()
                .filter(f -> f.getFechaHoraSalida() != null)
                .mapToDouble(this::calcularHoras)
                .sum();

        List<FichajeDTO> historico = fichajeRepository.findByEmpleadoOrderByFechaHoraEntradaDesc(empleado)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return ResumenFichajesDTO.builder()
                .fichado(fichado)
                .horasMesActual(Math.round(horasMes * 100.0) / 100.0)
                .fichajes(historico)
                .build();
    }

    private double calcularHoras(Fichaje f) {
        return Duration.between(f.getFechaHoraEntrada(), f.getFechaHoraSalida()).toMinutes() / 60.0;
    }

    private FichajeDTO toDTO(Fichaje f) {
        Double horas = f.getFechaHoraSalida() != null ? Math.round(calcularHoras(f) * 100.0) / 100.0 : null;
        return FichajeDTO.builder()
                .id(f.getId())
                .fechaHoraEntrada(f.getFechaHoraEntrada())
                .fechaHoraSalida(f.getFechaHoraSalida())
                .horasTrabajadas(horas)
                .build();
    }
}
