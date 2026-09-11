package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.PerfilDTO;
import com.rubensimon1.erp_lite.dto.PerfilUpdateDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
