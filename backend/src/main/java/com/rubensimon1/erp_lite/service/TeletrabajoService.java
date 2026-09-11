package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.RegistroTeletrabajoDTO;
import com.rubensimon1.erp_lite.dto.RegistroTeletrabajoInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.RegistroTeletrabajo;
import com.rubensimon1.erp_lite.repository.RegistroTeletrabajoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeletrabajoService {

    // Tarifa de referencia (acuerdos habituales de compensacion por teletrabajo), configurable
    public static final double IMPORTE_DIARIO = 2.50;

    private final RegistroTeletrabajoRepository registroRepository;

    public RegistroTeletrabajoDTO registrar(Empleado empleado, RegistroTeletrabajoInputDTO input) {
        RegistroTeletrabajo registro = registroRepository
                .findByEmpleadoAndMesAndAnio(empleado, input.getMes(), input.getAnio())
                .orElseGet(RegistroTeletrabajo::new);

        registro.setEmpleado(empleado);
        registro.setMes(input.getMes());
        registro.setAnio(input.getAnio());
        registro.setDiasTeletrabajados(input.getDiasTeletrabajados());

        registroRepository.save(registro);
        return toDTO(registro);
    }

    public List<RegistroTeletrabajoDTO> misRegistros(Empleado empleado) {
        return registroRepository.findByEmpleadoOrderByAnioDescMesDesc(empleado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private RegistroTeletrabajoDTO toDTO(RegistroTeletrabajo r) {
        return RegistroTeletrabajoDTO.builder()
                .id(r.getId())
                .mes(r.getMes())
                .anio(r.getAnio())
                .diasTeletrabajados(r.getDiasTeletrabajados())
                .compensacion(Math.round(r.getDiasTeletrabajados() * IMPORTE_DIARIO * 100.0) / 100.0)
                .build();
    }
}
