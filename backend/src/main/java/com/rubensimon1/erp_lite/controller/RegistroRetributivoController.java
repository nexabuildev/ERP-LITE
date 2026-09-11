package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.RegistroRetributivoDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.RegistroRetributivoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/registro-retributivo")
@RequiredArgsConstructor
public class RegistroRetributivoController {

    private final RegistroRetributivoService registroRetributivoService;

    @GetMapping("/mio")
    public RegistroRetributivoDTO mio(@AuthenticationPrincipal Empleado empleado) {
        return registroRetributivoService.miComparativa(empleado);
    }
}
