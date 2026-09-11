package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.MovimientoDTO;
import com.rubensimon1.erp_lite.dto.MovimientoInputDTO;
import com.rubensimon1.erp_lite.dto.ResumenMovimientosDTO;
import com.rubensimon1.erp_lite.entity.CuentaBancaria;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.MetodoPago;
import com.rubensimon1.erp_lite.entity.Movimiento;
import com.rubensimon1.erp_lite.entity.TipoMovimiento;
import com.rubensimon1.erp_lite.repository.CuentaBancariaRepository;
import com.rubensimon1.erp_lite.repository.MetodoPagoRepository;
import com.rubensimon1.erp_lite.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final MetodoPagoRepository metodoPagoRepository;

    @Transactional
    public MovimientoDTO registrar(Empleado empleado, MovimientoInputDTO input) {
        Movimiento movimiento = new Movimiento();
        movimiento.setEmpleado(empleado);
        aplicarDatos(movimiento, input, empleado);

        movimientoRepository.save(movimiento);
        aplicarEfectoSaldo(movimiento, movimiento.getTipo(), movimiento.getImporte());
        return toDTO(movimiento);
    }

    @Transactional
    public MovimientoDTO actualizar(Empleado empleado, Long id, MovimientoInputDTO input) {
        Movimiento movimiento = obtenerPropio(empleado, id);

        // 1. Revertimos el efecto que tenia sobre la cuenta/tarjeta anterior
        aplicarEfectoSaldo(movimiento, opuesto(movimiento.getTipo()), movimiento.getImporte());

        // 2. Actualizamos los datos (incluida la cuenta/metodo de pago elegidos)
        aplicarDatos(movimiento, input, empleado);
        movimientoRepository.save(movimiento);

        // 3. Aplicamos el nuevo efecto
        aplicarEfectoSaldo(movimiento, movimiento.getTipo(), movimiento.getImporte());
        return toDTO(movimiento);
    }

    @Transactional
    public void eliminar(Empleado empleado, Long id) {
        Movimiento movimiento = obtenerPropio(empleado, id);
        aplicarEfectoSaldo(movimiento, opuesto(movimiento.getTipo()), movimiento.getImporte());
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

    private void aplicarDatos(Movimiento movimiento, MovimientoInputDTO input, Empleado empleado) {
        movimiento.setConcepto(input.getConcepto());
        movimiento.setImporte(input.getImporte());
        movimiento.setTipo(input.getTipo());
        movimiento.setMedioPago(input.getMedioPago());
        movimiento.setFecha(input.getFecha());

        movimiento.setCuentaBancaria(
                input.getCuentaBancariaId() != null
                        ? obtenerCuentaPropia(empleado, input.getCuentaBancariaId())
                        : null
        );
        movimiento.setMetodoPago(
                input.getMetodoPagoId() != null
                        ? obtenerMetodoPropio(empleado, input.getMetodoPagoId())
                        : null
        );
    }

    // Resuelve que cuenta se ve afectada por el movimiento: si hay un metodo de
    // pago con cuenta vinculada, esa; si no, la cuenta indicada directamente.
    private void aplicarEfectoSaldo(Movimiento movimiento, TipoMovimiento tipo, double importe) {
        CuentaBancaria cuenta = null;
        if (movimiento.getMetodoPago() != null && movimiento.getMetodoPago().getCuentaVinculada() != null) {
            cuenta = movimiento.getMetodoPago().getCuentaVinculada();
        } else if (movimiento.getCuentaBancaria() != null) {
            cuenta = movimiento.getCuentaBancaria();
        }

        if (cuenta == null) return;

        double actual = cuenta.getSaldoActual() != null ? cuenta.getSaldoActual() : 0.0;
        double nuevo = tipo == TipoMovimiento.INGRESO ? actual + importe : actual - importe;
        cuenta.setSaldoActual(redondear(nuevo));
        cuentaBancariaRepository.save(cuenta);
    }

    private TipoMovimiento opuesto(TipoMovimiento tipo) {
        return tipo == TipoMovimiento.INGRESO ? TipoMovimiento.GASTO : TipoMovimiento.INGRESO;
    }

    private CuentaBancaria obtenerCuentaPropia(Empleado empleado, Long id) {
        CuentaBancaria cuenta = cuentaBancariaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
        if (!cuenta.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esa cuenta no es tuya");
        }
        return cuenta;
    }

    private MetodoPago obtenerMetodoPropio(Empleado empleado, Long id) {
        MetodoPago metodo = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pago no encontrado"));
        if (!metodo.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ese método de pago no es tuyo");
        }
        return metodo;
    }

    private Movimiento obtenerPropio(Empleado empleado, Long id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));

        if (!movimiento.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar un movimiento de otro empleado");
        }
        return movimiento;
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
                .cuentaBancariaId(m.getCuentaBancaria() != null ? m.getCuentaBancaria().getId() : null)
                .cuentaBancariaAlias(m.getCuentaBancaria() != null ? m.getCuentaBancaria().getAlias() : null)
                .metodoPagoId(m.getMetodoPago() != null ? m.getMetodoPago().getId() : null)
                .metodoPagoAlias(m.getMetodoPago() != null ? m.getMetodoPago().getAlias() : null)
                .build();
    }
}
