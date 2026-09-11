package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.CambiarCredencialesDTO;
import com.rubensimon1.erp_lite.dto.CredencialesActualizadasDTO;
import com.rubensimon1.erp_lite.dto.EliminarCuentaInputDTO;
import com.rubensimon1.erp_lite.dto.PerfilDTO;
import com.rubensimon1.erp_lite.dto.PerfilUpdateDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    @GetMapping("/mio")
    public PerfilDTO miPerfil(@AuthenticationPrincipal Empleado empleado) {
        return perfilService.miPerfil(empleado);
    }

    @PutMapping("/mio")
    public PerfilDTO actualizar(@AuthenticationPrincipal Empleado empleado,
                                 @RequestBody @Valid PerfilUpdateDTO input) {
        return perfilService.actualizar(empleado, input);
    }

    @PutMapping("/credenciales")
    public CredencialesActualizadasDTO cambiarCredenciales(@AuthenticationPrincipal Empleado empleado,
                                                             @RequestBody @Valid CambiarCredencialesDTO input) {
        return perfilService.cambiarCredenciales(empleado, input);
    }

    @PutMapping("/desactivar")
    public PerfilDTO desactivar(@AuthenticationPrincipal Empleado empleado,
                                 @RequestBody @Valid EliminarCuentaInputDTO input) {
        return perfilService.desactivar(empleado, input);
    }

    @PutMapping("/reactivar")
    public PerfilDTO reactivar(@AuthenticationPrincipal Empleado empleado) {
        return perfilService.reactivarPropia(empleado);
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<Void> eliminar(@AuthenticationPrincipal Empleado empleado,
                                          @RequestBody @Valid EliminarCuentaInputDTO input) {
        perfilService.eliminarDefinitivamente(empleado, input);
        return ResponseEntity.noContent().build();
    }
}
