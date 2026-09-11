package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.DeclaracionDTO;
import com.rubensimon1.erp_lite.dto.DeclaracionPresentadaDTO;
import com.rubensimon1.erp_lite.dto.DeclaracionPresentadaInputDTO;
import com.rubensimon1.erp_lite.entity.DeclaracionPresentada;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.DeclaracionesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(value = "/presentadas", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DeclaracionPresentadaDTO registrarPresentada(@AuthenticationPrincipal Empleado empleado,
                                                          @ModelAttribute @Valid DeclaracionPresentadaInputDTO input,
                                                          @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
        return declaracionesService.registrarPresentada(empleado, input, archivo);
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

    @GetMapping("/presentadas/{id}/archivo")
    public ResponseEntity<byte[]> archivo(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        DeclaracionPresentada d = declaracionesService.obtenerArchivo(empleado, id);
        MediaType tipo;
        try {
            tipo = d.getArchivoTipo() != null ? MediaType.parseMediaType(d.getArchivoTipo()) : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            tipo = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + d.getArchivoNombre() + "\"")
                .body(d.getArchivoContenido());
    }
}
