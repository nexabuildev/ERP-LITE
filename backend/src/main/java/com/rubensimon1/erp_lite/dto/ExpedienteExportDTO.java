package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpedienteExportDTO {
    private LocalDateTime generadoEl;
    private PerfilDTO perfil;
    private List<CuentaBancariaDTO> cuentasBancarias;
    private List<MetodoPagoDTO> metodosPago;
    private List<MovimientoDTO> movimientos;
    private List<MetaAhorroDTO> metasAhorro;
    private List<SolicitudVacacionesDTO> vacaciones;
    private List<NominaDTO> nominas;
    private List<RegistroTeletrabajoDTO> teletrabajo;
    private List<AlertaDTO> alertas;
    private List<DeclaracionPresentadaDTO> declaracionesPresentadas;
}
