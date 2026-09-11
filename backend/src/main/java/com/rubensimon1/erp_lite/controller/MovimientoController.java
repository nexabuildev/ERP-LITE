package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.HistorialMovimientosDTO;
import com.rubensimon1.erp_lite.dto.MovimientoDTO;
import com.rubensimon1.erp_lite.dto.MovimientoInputDTO;
import com.rubensimon1.erp_lite.dto.ResumenMovimientosDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @PostMapping
    public MovimientoDTO registrar(@AuthenticationPrincipal Empleado empleado,
                                    @RequestBody @Valid MovimientoInputDTO input) {
        return movimientoService.registrar(empleado, input);
    }

    @PutMapping("/{id}")
    public MovimientoDTO actualizar(@AuthenticationPrincipal Empleado empleado,
                                     @PathVariable Long id,
                                     @RequestBody @Valid MovimientoInputDTO input) {
        return movimientoService.actualizar(empleado, id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        movimientoService.eliminar(empleado, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mios/resumen")
    public ResumenMovimientosDTO resumen(@AuthenticationPrincipal Empleado empleado) {
        return movimientoService.resumen(empleado);
    }

    @GetMapping("/mios/historial")
    public HistorialMovimientosDTO historial(@AuthenticationPrincipal Empleado empleado) {
        return movimientoService.historial(empleado);
    }
}
