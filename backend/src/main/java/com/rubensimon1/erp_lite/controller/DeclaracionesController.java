package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.DeclaracionDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.DeclaracionesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/declaraciones")
@RequiredArgsConstructor
public class DeclaracionesController {

    private final DeclaracionesService declaracionesService;

    @GetMapping("/mias")
    public List<DeclaracionDTO> misDeclaraciones(@AuthenticationPrincipal Empleado empleado) {
        return declaracionesService.misDeclaraciones(empleado);
    }
}
