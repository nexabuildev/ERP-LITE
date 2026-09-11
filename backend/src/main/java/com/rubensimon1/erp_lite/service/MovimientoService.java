package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.MovimientoDTO;
import com.rubensimon1.erp_lite.dto.MovimientoInputDTO;
import com.rubensimon1.erp_lite.dto.ResumenMovimientosDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Movimiento;
import com.rubensimon1.erp_lite.entity.TipoMovimiento;
import com.rubensimon1.erp_lite.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    public MovimientoDTO registrar(Empleado empleado, MovimientoInputDTO input) {
        Movimiento movimiento = new Movimiento();
        movimiento.setEmpleado(empleado);
        movimiento.setConcepto(input.getConcepto());
        movimiento.setImporte(input.getImporte());
        movimiento.setTipo(input.getTipo());
        movimiento.setMedioPago(input.getMedioPago());
        movimiento.setFecha(input.getFecha());

        movimientoRepository.save(movimiento);
        return toDTO(movimiento);
    }

    public void eliminar(Empleado empleado, Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));

        if (!movimiento.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes eliminar un movimiento de otro empleado");
        }

        movimientoRepository.delete(movimiento);
    }

    public ResumenMovimientosDTO resumen(Empleado empleado) {
        List<Movimiento> movimientos = movimientoRepository.findByEmpleadoOrderByFechaDescIdDesc(empleado);

        double totalIngresos = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.INGRESO)
                .mapToDouble(Movimiento::getImporte)
                .sum();

        double totalGastos = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.GASTO)
                .mapToDouble(Movimiento::getImporte)
                .sum();

        List<MovimientoDTO> dtos = movimientos.stream().map(this::toDTO).collect(Collectors.toList());

        return ResumenMovimientosDTO.builder()
                .saldo(redondear(totalIngresos - totalGastos))
                .totalIngresos(redondear(totalIngresos))
                .totalGastos(redondear(totalGastos))
                .movimientos(dtos)
                .build();
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private MovimientoDTO toDTO(Movimiento m) {
        return MovimientoDTO.builder()
                .id(m.getId())
                .concepto(m.getConcepto())
                .importe(m.getImporte())
                .tipo(m.getTipo())
                .medioPago(m.getMedioPago())
                .fecha(m.getFecha())
                .build();
    }
}
