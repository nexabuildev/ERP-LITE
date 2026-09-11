package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.FactorSaludDTO;
import com.rubensimon1.erp_lite.dto.PerfilDTO;
import com.rubensimon1.erp_lite.dto.SaludAdministrativaDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaludService {

    private final PerfilService perfilService;
    private final AlertaService alertaService;
    private final DeclaracionesService declaracionesService;
    private final MovimientoService movimientoService;

    public SaludAdministrativaDTO calcular(Empleado empleado) {
        List<FactorSaludDTO> factores = new ArrayList<>();

        factores.add(factorPerfil(empleado));
        factores.add(factorAlertas(empleado));
        factores.add(factorDeclaraciones(empleado));
        factores.add(factorBalance(empleado));

        int puntuacion = factores.stream().mapToInt(FactorSaludDTO::getPuntos).sum();

        return SaludAdministrativaDTO.builder()
                .puntuacion(puntuacion)
                .factores(factores)
                .build();
    }

    private FactorSaludDTO factorPerfil(Empleado empleado) {
        PerfilDTO p = perfilService.miPerfil(empleado);
        int puntos = 0;
        int completados = 0;
        int total = 4;

        if (esValido(p.getDni())) { puntos += 10; completados++; }
        if (esValido(p.getCalle()) && esValido(p.getCodigoPostal()) && esValido(p.getCiudad())) { puntos += 10; completados++; }
        if (esValido(p.getGrupoSanguineo()) && esValido(p.getContactoEmergenciaNombre()) && esValido(p.getContactoEmergenciaTelefono())) { puntos += 10; completados++; }
        if (p.getTipoTrabajador() != null && p.getSalarioBrutoAnual() != null) { puntos += 10; completados++; }

        return FactorSaludDTO.builder()
                .etiqueta("Perfil completo")
                .puntos(puntos)
                .puntosMaximos(40)
                .mensaje(completados + " de " + total + " bloques del perfil completados")
                .build();
    }

    private FactorSaludDTO factorAlertas(Empleado empleado) {
        long vencidas = alertaService.resumen(empleado).getVencidas();
        int puntos = vencidas == 0 ? 20 : (int) Math.max(0, 20 - vencidas * 10);
        String mensaje = vencidas == 0
                ? "No tienes alertas vencidas"
                : vencidas + " alerta(s) vencida(s) sin resolver";

        return FactorSaludDTO.builder().etiqueta("Alertas al día").puntos(puntos).puntosMaximos(20).mensaje(mensaje).build();
    }

    private FactorSaludDTO factorDeclaraciones(Empleado empleado) {
        long proximas = declaracionesService.misDeclaraciones(empleado).stream()
                .filter(d -> d.getDiasRestantes() <= 15)
                .count();
        int puntos = proximas == 0 ? 20 : 10;
        String mensaje = proximas == 0
                ? "Ninguna declaración vence en los próximos 15 días"
                : proximas + " declaración(es) vencen en menos de 15 días";

        return FactorSaludDTO.builder().etiqueta("Declaraciones al día").puntos(puntos).puntosMaximos(20).mensaje(mensaje).build();
    }

    private FactorSaludDTO factorBalance(Empleado empleado) {
        double saldo = movimientoService.resumen(empleado).getSaldo();
        int puntos;
        if (saldo >= 0) puntos = 20;
        else if (saldo >= -200) puntos = 10;
        else puntos = 0;

        String mensaje = saldo >= 0
                ? "Tu balance registrado es positivo"
                : "Tu balance registrado es negativo (" + saldo + " €)";

        return FactorSaludDTO.builder().etiqueta("Balance económico").puntos(puntos).puntosMaximos(20).mensaje(mensaje).build();
    }

    private boolean esValido(String valor) {
        return valor != null && !valor.isBlank();
    }
}
