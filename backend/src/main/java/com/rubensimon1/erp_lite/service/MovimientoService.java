package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.HistorialMovimientosDTO;
import com.rubensimon1.erp_lite.dto.IncrementoGastoDTO;
import com.rubensimon1.erp_lite.dto.MovimientoDTO;
import com.rubensimon1.erp_lite.dto.MovimientoInputDTO;
import com.rubensimon1.erp_lite.dto.PuntoHistorialDTO;
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

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public ResumenMovimientosDTO resumen(Empleado empleado, Integer mes, Integer anio) {
        List<Movimiento> movimientos;
        if (mes != null && anio != null) {
            YearMonth periodo = YearMonth.of(anio, mes);
            movimientos = movimientoRepository.findByEmpleadoAndFechaBetweenOrderByFechaDescIdDesc(
                    empleado, periodo.atDay(1), periodo.atEndOfMonth());
        } else {
            movimientos = movimientoRepository.findByEmpleadoOrderByFechaDescIdDesc(empleado);
        }

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

    public HistorialMovimientosDTO historial(Empleado empleado) {
        int meses = 6;
        YearMonth mesActual = YearMonth.now();
        YearMonth mesInicio = mesActual.minusMonths(meses - 1);

        List<Movimiento> movimientos = movimientoRepository.findByEmpleadoOrderByFechaDescIdDesc(empleado);

        Map<YearMonth, double[]> porMes = new java.util.HashMap<>(); // [ingresos, gastos]
        for (Movimiento m : movimientos) {
            YearMonth ym = YearMonth.from(m.getFecha());
            if (ym.isBefore(mesInicio) || ym.isAfter(mesActual)) continue;
            double[] par = porMes.computeIfAbsent(ym, k -> new double[2]);
            if (m.getTipo() == TipoMovimiento.INGRESO) par[0] += m.getImporte();
            else par[1] += m.getImporte();
        }

        List<PuntoHistorialDTO> puntos = new ArrayList<>();
        double acumulado = 0;
        for (int i = 0; i < meses; i++) {
            YearMonth ym = mesInicio.plusMonths(i);
            double[] par = porMes.getOrDefault(ym, new double[2]);
            double ingresos = redondear(par[0]);
            double gastos = redondear(par[1]);
            double neto = redondear(ingresos - gastos);
            acumulado = redondear(acumulado + neto);

            String etiqueta = capitalizar(ym.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"))) + " " + ym.getYear();

            puntos.add(PuntoHistorialDTO.builder()
                    .periodo(ym.toString())
                    .etiqueta(etiqueta)
                    .ingresos(ingresos)
                    .gastos(gastos)
                    .neto(neto)
                    .saldoAcumulado(acumulado)
                    .build());
        }

        Double variacion = null;
        if (puntos.size() >= 2) {
            double netoUltimo = puntos.get(puntos.size() - 1).getNeto();
            double netoAnterior = puntos.get(puntos.size() - 2).getNeto();
            if (netoAnterior != 0) {
                variacion = redondear(((netoUltimo - netoAnterior) / Math.abs(netoAnterior)) * 100.0);
            }
        }

        return HistorialMovimientosDTO.builder()
                .puntos(puntos)
                .variacionPorcentaje(variacion)
                .build();
    }

    // Compara los dos gastos mas recientes de cada concepto repetido (misma
    // etiqueta, sin distinguir mayusculas) y avisa si el importe ha subido.
    // Simplificacion deliberada: no hay un flag de "gasto recurrente" en el
    // modelo, asi que cualquier concepto que se repita se trata como tal.
    public List<IncrementoGastoDTO> detectarIncrementos(Empleado empleado) {
        List<Movimiento> gastos = movimientoRepository.findByEmpleadoOrderByFechaDescIdDesc(empleado)
                .stream()
                .filter(m -> m.getTipo() == TipoMovimiento.GASTO)
                .collect(Collectors.toList());

        Map<String, List<Movimiento>> porConcepto = gastos.stream()
                .collect(Collectors.groupingBy(m -> m.getConcepto().trim().toLowerCase()));

        List<IncrementoGastoDTO> incrementos = new ArrayList<>();
        for (List<Movimiento> grupo : porConcepto.values()) {
            if (grupo.size() < 2) continue;

            grupo.sort(java.util.Comparator.comparing(Movimiento::getFecha).thenComparing(Movimiento::getId));
            Movimiento anterior = grupo.get(grupo.size() - 2);
            Movimiento actual = grupo.get(grupo.size() - 1);

            if (anterior.getImporte() <= 0 || actual.getImporte() <= anterior.getImporte()) continue;

            double porcentaje = redondear(((actual.getImporte() - anterior.getImporte()) / anterior.getImporte()) * 100.0);
            if (porcentaje < 1.0) continue; // ruido de decimales, no una subida real

            incrementos.add(IncrementoGastoDTO.builder()
                    .concepto(actual.getConcepto())
                    .fechaAnterior(anterior.getFecha())
                    .importeAnterior(anterior.getImporte())
                    .fechaActual(actual.getFecha())
                    .importeActual(actual.getImporte())
                    .incrementoPorcentaje(porcentaje)
                    .build());
        }

        incrementos.sort((a, b) -> Double.compare(b.getIncrementoPorcentaje(), a.getIncrementoPorcentaje()));
        return incrementos;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
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
