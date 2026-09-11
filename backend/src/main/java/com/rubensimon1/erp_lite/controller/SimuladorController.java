package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.SimulacionNominaDTO;
import com.rubensimon1.erp_lite.dto.SimulacionNominaInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.SimuladorNominaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulador")
@RequiredArgsConstructor
public class SimuladorController {

    private final SimuladorNominaService simuladorNominaService;

    @PostMapping("/nomina")
    public SimulacionNominaDTO simularNomina(@AuthenticationPrincipal Empleado empleado,
                                              @RequestBody @Valid SimulacionNominaInputDTO input) {
        return simuladorNominaService.simular(empleado, input);
    }
}
