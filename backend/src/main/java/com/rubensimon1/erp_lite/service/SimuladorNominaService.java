package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.SimulacionNominaDTO;
import com.rubensimon1.erp_lite.dto.SimulacionNominaInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SimuladorNominaService {

    public SimulacionNominaDTO simular(Empleado empleado, SimulacionNominaInputDTO input) {
        Double salarioActual = empleado.getSalarioBrutoAnual();
        if (salarioActual == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Todavia no tienes un salario bruto anual registrado");
        }

        double netoActualMensual = netoMensual(salarioActual);
        double netoNuevoMensual = netoMensual(input.getNuevoSalarioBrutoAnual());
        double diferencia = netoNuevoMensual - netoActualMensual;
        double porcentaje = netoActualMensual > 0 ? (diferencia / netoActualMensual) * 100.0 : 0.0;

        return SimulacionNominaDTO.builder()
                .salarioActualBrutoAnual(redondear(salarioActual))
                .salarioActualNetoMensual(redondear(netoActualMensual))
                .nuevoSalarioBrutoAnual(redondear(input.getNuevoSalarioBrutoAnual()))
                .nuevoSalarioNetoMensual(redondear(netoNuevoMensual))
                .diferenciaMensual(redondear(diferencia))
                .diferenciaPorcentaje(redondear(porcentaje))
                .build();
    }

    private double netoMensual(double brutoAnual) {
        double brutoMensual = brutoAnual / 12.0;
        return brutoMensual * (1 - NominaService.TASA_DEDUCCION_APROX);
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
