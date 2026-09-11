package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.FichajeDTO;
import com.rubensimon1.erp_lite.dto.ResumenFichajesDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.FichajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fichajes")
@RequiredArgsConstructor
public class FichajeController {

    private final FichajeService fichajeService;

    @PostMapping("/entrada")
    public FichajeDTO ficharEntrada(@AuthenticationPrincipal Empleado empleado) {
        return fichajeService.ficharEntrada(empleado);
    }

    @PostMapping("/salida")
    public FichajeDTO ficharSalida(@AuthenticationPrincipal Empleado empleado) {
        return fichajeService.ficharSalida(empleado);
    }

    @GetMapping("/mios/resumen")
    public ResumenFichajesDTO resumen(@AuthenticationPrincipal Empleado empleado) {
        return fichajeService.resumen(empleado);
    }
}
