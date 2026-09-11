package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.DeclaracionDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.TipoTrabajador;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class DeclaracionesService {

    public List<DeclaracionDTO> misDeclaraciones(Empleado empleado) {
        LocalDate hoy = LocalDate.now();
        List<DeclaracionDTO> declaraciones = new ArrayList<>();

        TipoTrabajador tipo = empleado.getTipoTrabajador() != null ? empleado.getTipoTrabajador() : TipoTrabajador.ASALARIADO;

        if (tipo == TipoTrabajador.AUTONOMO) {
            LocalDate proximoTrimestral = proximaFechaTrimestral(hoy);
            declaraciones.add(crear("Modelo 303 · IVA trimestral", "Autoliquidacion trimestral del IVA", proximoTrimestral, hoy));
            declaraciones.add(crear("Modelo 130 · Pago fraccionado IRPF", "Pago a cuenta trimestral del IRPF", proximoTrimestral, hoy));
            declaraciones.add(crear("Modelo 390 · Resumen anual IVA", "Declaracion-resumen anual del IVA", proximaFecha(1, 30, hoy), hoy));
        } else {
            declaraciones.add(crear("Declaracion de la Renta (IRPF)", "Campana anual de la Renta", proximaFecha(6, 30, hoy), hoy));
        }

        declaraciones.sort(Comparator.comparing(DeclaracionDTO::getFechaLimite));
        return declaraciones;
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
