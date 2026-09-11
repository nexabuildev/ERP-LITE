package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.NominaDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.NominaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nominas")
@RequiredArgsConstructor
public class NominaController {

    private final NominaService nominaService;

    @GetMapping("/mias")
    public List<NominaDTO> misNominas(@AuthenticationPrincipal Empleado empleado) {
        return nominaService.misNominas(empleado);
    }
}
