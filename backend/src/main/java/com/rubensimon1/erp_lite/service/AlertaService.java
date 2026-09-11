package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.AlertaDTO;
import com.rubensimon1.erp_lite.dto.AlertaInputDTO;
import com.rubensimon1.erp_lite.dto.ResumenAlertasDTO;
import com.rubensimon1.erp_lite.entity.Alerta;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.EstadoAlerta;
import com.rubensimon1.erp_lite.repository.AlertaRepository;
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
public class AlertaService {

    private static final long DIAS_PARA_PROXIMA = 30;

    private final AlertaRepository alertaRepository;

    public AlertaDTO crear(Empleado empleado, AlertaInputDTO input) {
        Alerta alerta = new Alerta();
        alerta.setEmpleado(empleado);
        alerta.setTitulo(input.getTitulo());
        alerta.setFechaVencimiento(input.getFechaVencimiento());
        alerta.setNotas(input.getNotas());

        alertaRepository.save(alerta);
        return toDTO(alerta);
    }

    public void eliminar(Empleado empleado, Long id) {
        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alerta no encontrada"));

        if (!alerta.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar una alerta de otro empleado");
        }

        alertaRepository.delete(alerta);
    }

    public ResumenAlertasDTO resumen(Empleado empleado) {
        List<AlertaDTO> alertas = alertaRepository.findByEmpleadoOrderByFechaVencimientoAsc(empleado)
                .stream().map(this::toDTO).collect(Collectors.toList());

        long vencidas = alertas.stream().filter(a -> a.getEstado() == EstadoAlerta.VENCIDA).count();
        long proximas = alertas.stream().filter(a -> a.getEstado() == EstadoAlerta.PROXIMA).count();

        return ResumenAlertasDTO.builder()
                .vencidas(vencidas)
                .proximas(proximas)
                .alertas(alertas)
                .build();
    }

    private AlertaDTO toDTO(Alerta a) {
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), a.getFechaVencimiento());
        EstadoAlerta estado;
        if (dias < 0) {
            estado = EstadoAlerta.VENCIDA;
        } else if (dias <= DIAS_PARA_PROXIMA) {
            estado = EstadoAlerta.PROXIMA;
        } else {
            estado = EstadoAlerta.VIGENTE;
        }

        return AlertaDTO.builder()
                .id(a.getId())
                .titulo(a.getTitulo())
                .fechaVencimiento(a.getFechaVencimiento())
                .notas(a.getNotas())
                .diasRestantes(dias)
                .estado(estado)
                .build();
    }
}
