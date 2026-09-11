package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.NominaDTO;
import com.rubensimon1.erp_lite.dto.NominaInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Nomina;
import com.rubensimon1.erp_lite.service.NominaService;
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
@RequestMapping("/api/v1/nominas")
@RequiredArgsConstructor
public class NominaController {

    private final NominaService nominaService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public NominaDTO crear(@AuthenticationPrincipal Empleado empleado,
                            @ModelAttribute @Valid NominaInputDTO input,
                            @RequestParam(value = "archivo", required = false) MultipartFile archivo) {
        return nominaService.crear(empleado, input, archivo);
    }

    @GetMapping("/mias")
    public List<NominaDTO> misNominas(@AuthenticationPrincipal Empleado empleado) {
        return nominaService.misNominas(empleado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        nominaService.eliminar(empleado, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<byte[]> archivo(@AuthenticationPrincipal Empleado empleado, @PathVariable Long id) {
        Nomina nomina = nominaService.obtenerArchivo(empleado, id);
        MediaType tipo;
        try {
            tipo = nomina.getArchivoTipo() != null
                    ? MediaType.parseMediaType(nomina.getArchivoTipo())
                    : MediaType.APPLICATION_OCTET_STREAM;
        } catch (Exception e) {
            tipo = MediaType.APPLICATION_OCTET_STREAM;
        }

        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomina.getArchivoNombre() + "\"")
                .body(nomina.getArchivoContenido());
    }
}
