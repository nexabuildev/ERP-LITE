package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.RegistroTeletrabajoDTO;
import com.rubensimon1.erp_lite.dto.RegistroTeletrabajoInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.TeletrabajoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teletrabajo")
@RequiredArgsConstructor
public class TeletrabajoController {

    private final TeletrabajoService teletrabajoService;

    @PostMapping
    public RegistroTeletrabajoDTO registrar(@AuthenticationPrincipal Empleado empleado,
                                             @RequestBody @Valid RegistroTeletrabajoInputDTO input) {
        return teletrabajoService.registrar(empleado, input);
    }

    @GetMapping("/mios")
    public List<RegistroTeletrabajoDTO> misRegistros(@AuthenticationPrincipal Empleado empleado) {
        return teletrabajoService.misRegistros(empleado);
    }
}
