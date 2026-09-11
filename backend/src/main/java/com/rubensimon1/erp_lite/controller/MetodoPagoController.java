package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.MetodoPagoDTO;
import com.rubensimon1.erp_lite.dto.MetodoPagoInputDTO;
import com.rubensimon1.erp_lite.dto.MetodoPagoSensibleDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.MetodoPagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metodos-pago")
@RequiredArgsConstructor
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    @PostMapping
    public MetodoPagoDTO crear(@AuthenticationPrincipal Empleado empleado, @RequestBody @Valid MetodoPagoInputDTO input) {
        return metodoPagoService.crear(empleado, input);
    }

    @GetMapping("/mios")
    public List<MetodoPagoDTO> misMetodos(@AuthenticationPrincipal Empleado empleado) {
        return metodoPagoService.misMetodos(empleado);
    }

    @PutMapping("/{id}")
    public MetodoPagoDTO actualizar(@AuthenticationPrincipal Empleado empleado,
                                     @PathVariable Long id,
                                     @RequestBody @Valid MetodoPagoInputDTO input) {
        return metodoPagoService.actualizar(empleado, id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        metodoPagoService.eliminar(empleado, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/sensible")
    public MetodoPagoSensibleDTO sensible(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        return metodoPagoService.obtenerSensible(empleado, id);
    }
}
