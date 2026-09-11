package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.ExpedienteExportDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final PerfilService perfilService;
    private final CuentaBancariaService cuentaBancariaService;
    private final MetodoPagoService metodoPagoService;
    private final MovimientoService movimientoService;
    private final AhorroService ahorroService;
    private final VacacionesService vacacionesService;
    private final NominaService nominaService;
    private final TeletrabajoService teletrabajoService;
    private final AlertaService alertaService;
    private final DeclaracionesService declaracionesService;

    public ExpedienteExportDTO exportar(Empleado empleado) {
        return ExpedienteExportDTO.builder()
                .generadoEl(LocalDateTime.now())
                .perfil(perfilService.miPerfil(empleado))
                .cuentasBancarias(cuentaBancariaService.misCuentas(empleado))
                .metodosPago(metodoPagoService.misMetodos(empleado))
                .movimientos(movimientoService.resumen(empleado).getMovimientos())
                .metasAhorro(ahorroService.misMetas(empleado))
                .vacaciones(vacacionesService.misSolicitudes(empleado))
                .nominas(nominaService.misNominas(empleado))
                .teletrabajo(teletrabajoService.misRegistros(empleado))
                .alertas(alertaService.resumen(empleado).getAlertas())
                .declaracionesPresentadas(declaracionesService.misPresentadas(empleado))
                .build();
    }
}
