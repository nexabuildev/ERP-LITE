package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.CuentaBancariaDTO;
import com.rubensimon1.erp_lite.dto.CuentaBancariaInputDTO;
import com.rubensimon1.erp_lite.dto.ResumenCuentasDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.CuentaBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuentas-bancarias")
@RequiredArgsConstructor
public class CuentaBancariaController {

    private final CuentaBancariaService cuentaBancariaService;

    @PostMapping
    public CuentaBancariaDTO anadir(@AuthenticationPrincipal Empleado empleado,
                                     @RequestBody @Valid CuentaBancariaInputDTO input) {
        return cuentaBancariaService.anadir(empleado, input);
    }

    @GetMapping("/mias")
    public List<CuentaBancariaDTO> misCuentas(@AuthenticationPrincipal Empleado empleado) {
        return cuentaBancariaService.misCuentas(empleado);
    }

    @GetMapping("/resumen")
    public ResumenCuentasDTO resumen(@AuthenticationPrincipal Empleado empleado) {
        return cuentaBancariaService.resumen(empleado);
    }

    @PutMapping("/{id}")
    public CuentaBancariaDTO actualizar(@AuthenticationPrincipal Empleado empleado,
                                         @PathVariable Long id,
                                         @RequestBody @Valid CuentaBancariaInputDTO input) {
        return cuentaBancariaService.actualizar(empleado, id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        cuentaBancariaService.eliminar(empleado, id);
        return ResponseEntity.noContent().build();
    }
}
