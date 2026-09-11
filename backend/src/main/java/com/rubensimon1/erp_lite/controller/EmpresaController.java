package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.EmpresaDTO;
import com.rubensimon1.erp_lite.dto.EmpresaInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping
    public EmpresaDTO crear(@AuthenticationPrincipal Empleado empleado,
                             @RequestBody @Valid EmpresaInputDTO input) {
        return empresaService.crear(empleado, input);
    }

    @GetMapping("/mias")
    public List<EmpresaDTO> misEmpresas(@AuthenticationPrincipal Empleado empleado) {
        return empresaService.misEmpresas(empleado);
    }

    @PutMapping("/{id}")
    public EmpresaDTO actualizar(@AuthenticationPrincipal Empleado empleado,
                                  @PathVariable Long id,
                                  @RequestBody @Valid EmpresaInputDTO input) {
        return empresaService.actualizar(empleado, id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        empresaService.eliminar(empleado, id);
        return ResponseEntity.noContent().build();
    }
}
