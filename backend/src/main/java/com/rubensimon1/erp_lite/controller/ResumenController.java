package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.SaludAdministrativaDTO;
import com.rubensimon1.erp_lite.dto.WrappedDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.SaludService;
import com.rubensimon1.erp_lite.service.WrappedService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resumen")
@RequiredArgsConstructor
public class ResumenController {

    private final WrappedService wrappedService;
    private final SaludService saludService;

    @GetMapping("/wrapped")
    public WrappedDTO wrapped(@AuthenticationPrincipal Empleado empleado) {
        return wrappedService.delMesActual(empleado);
    }

    @GetMapping("/salud")
    public SaludAdministrativaDTO salud(@AuthenticationPrincipal Empleado empleado) {
        return saludService.calcular(empleado);
    }
}
