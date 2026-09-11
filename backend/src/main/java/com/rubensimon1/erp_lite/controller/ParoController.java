package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.ParoDTO;
import com.rubensimon1.erp_lite.dto.ParoInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.ParoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/paro")
@RequiredArgsConstructor
public class ParoController {

    private final ParoService paroService;

    @PostMapping
    public ParoDTO crear(@AuthenticationPrincipal Empleado empleado,
                          @RequestBody @Valid ParoInputDTO input) {
        return paroService.crear(empleado, input);
    }

    @GetMapping("/mias")
    public List<ParoDTO> misPeriodos(@AuthenticationPrincipal Empleado empleado) {
        return paroService.misPeriodos(empleado);
    }

    @PutMapping("/{id}")
    public ParoDTO actualizar(@AuthenticationPrincipal Empleado empleado,
                               @PathVariable Long id,
                               @RequestBody @Valid ParoInputDTO input) {
        return paroService.actualizar(empleado, id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        paroService.eliminar(empleado, id);
        return ResponseEntity.noContent().build();
    }
}
