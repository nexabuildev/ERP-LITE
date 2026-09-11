package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.DeclaracionDTO;
import com.rubensimon1.erp_lite.dto.DeclaracionPresentadaDTO;
import com.rubensimon1.erp_lite.dto.DeclaracionPresentadaInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.DeclaracionesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/declaraciones")
@RequiredArgsConstructor
public class DeclaracionesController {

    private final DeclaracionesService declaracionesService;

    @GetMapping("/mias")
    public List<DeclaracionDTO> misDeclaraciones(@AuthenticationPrincipal Empleado empleado) {
        return declaracionesService.misDeclaraciones(empleado);
    }

    @PostMapping("/presentadas")
    public DeclaracionPresentadaDTO registrarPresentada(@AuthenticationPrincipal Empleado empleado,
                                                          @RequestBody @Valid DeclaracionPresentadaInputDTO input) {
        return declaracionesService.registrarPresentada(empleado, input);
    }

    @GetMapping("/presentadas")
    public List<DeclaracionPresentadaDTO> misPresentadas(@AuthenticationPrincipal Empleado empleado) {
        return declaracionesService.misPresentadas(empleado);
    }

    @DeleteMapping("/presentadas/{id}")
    public ResponseEntity<Void> eliminarPresentada(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        declaracionesService.eliminarPresentada(empleado, id);
        return ResponseEntity.noContent().build();
    }
}
