package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.AportacionInputDTO;
import com.rubensimon1.erp_lite.dto.MetaAhorroDTO;
import com.rubensimon1.erp_lite.dto.MetaAhorroInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.AhorroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ahorros")
@RequiredArgsConstructor
public class AhorroController {

    private final AhorroService ahorroService;

    @PostMapping
    public MetaAhorroDTO crear(@AuthenticationPrincipal Empleado empleado,
                                @RequestBody @Valid MetaAhorroInputDTO input) {
        return ahorroService.crear(empleado, input);
    }

    @GetMapping("/mias")
    public List<MetaAhorroDTO> misMetas(@AuthenticationPrincipal Empleado empleado) {
        return ahorroService.misMetas(empleado);
    }

    @PutMapping("/{id}/aportar")
    public MetaAhorroDTO aportar(@AuthenticationPrincipal Empleado empleado,
                                  @PathVariable Long id,
                                  @RequestBody @Valid AportacionInputDTO input) {
        return ahorroService.aportar(empleado, id, input);
    }

    @PutMapping("/{id}/retirar")
    public MetaAhorroDTO retirar(@AuthenticationPrincipal Empleado empleado,
                                  @PathVariable Long id,
                                  @RequestBody @Valid AportacionInputDTO input) {
        return ahorroService.retirar(empleado, id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        ahorroService.eliminar(empleado, id);
        return ResponseEntity.noContent().build();
    }
}
