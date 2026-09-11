package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.AlertaDTO;
import com.rubensimon1.erp_lite.dto.AlertaInputDTO;
import com.rubensimon1.erp_lite.dto.ResumenAlertasDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.AlertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @PostMapping
    public AlertaDTO crear(@AuthenticationPrincipal Empleado empleado, @RequestBody @Valid AlertaInputDTO input) {
        return alertaService.crear(empleado, input);
    }

    @GetMapping("/mias/resumen")
    public ResumenAlertasDTO resumen(@AuthenticationPrincipal Empleado empleado) {
        return alertaService.resumen(empleado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        alertaService.eliminar(empleado, id);
        return ResponseEntity.noContent().build();
    }
}
