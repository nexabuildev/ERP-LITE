package com.rubensimon1.erp_lite.controller;

import com.rubensimon1.erp_lite.dto.SaldoVacacionesDTO;
import com.rubensimon1.erp_lite.dto.SolicitudVacacionesDTO;
import com.rubensimon1.erp_lite.dto.SolicitudVacacionesInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.EstadoVacacion;
import com.rubensimon1.erp_lite.service.VacacionesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vacaciones")
@RequiredArgsConstructor
public class VacacionesController {

    private final VacacionesService vacacionesService;

    @PostMapping
    public SolicitudVacacionesDTO solicitar(@AuthenticationPrincipal Empleado empleado,
                                             @RequestBody @Valid SolicitudVacacionesInputDTO input) {
        return vacacionesService.solicitar(empleado, input);
    }

    @GetMapping("/mias")
    public List<SolicitudVacacionesDTO> misSolicitudes(@AuthenticationPrincipal Empleado empleado) {
        return vacacionesService.misSolicitudes(empleado);
    }

    @GetMapping("/saldo")
    public SaldoVacacionesDTO miSaldo(@AuthenticationPrincipal Empleado empleado) {
        return vacacionesService.miSaldo(empleado);
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<SolicitudVacacionesDTO> pendientes() {
        return vacacionesService.pendientes();
    }

    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public SolicitudVacacionesDTO aprobar(@PathVariable Long id) {
        return vacacionesService.cambiarEstado(id, EstadoVacacion.APROBADA);
    }

    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasAuthority('ADMIN')")
    public SolicitudVacacionesDTO rechazar(@PathVariable Long id) {
        return vacacionesService.cambiarEstado(id, EstadoVacacion.RECHAZADA);
    }
}
