package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.WrappedDTO;
import com.rubensimon1.erp_lite.entity.EstadoVacacion;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Fichaje;
import com.rubensimon1.erp_lite.entity.Movimiento;
import com.rubensimon1.erp_lite.entity.SolicitudVacaciones;
import com.rubensimon1.erp_lite.entity.TipoMovimiento;
import com.rubensimon1.erp_lite.repository.FichajeRepository;
import com.rubensimon1.erp_lite.repository.MovimientoRepository;
import com.rubensimon1.erp_lite.repository.RegistroTeletrabajoRepository;
import com.rubensimon1.erp_lite.repository.SolicitudVacacionesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WrappedService {

    private final MovimientoRepository movimientoRepository;
    private final SolicitudVacacionesRepository vacacionesRepository;
    private final RegistroTeletrabajoRepository teletrabajoRepository;
    private final FichajeRepository fichajeRepository;

    public WrappedDTO delMesActual(Empleado empleado) {
        YearMonth mesActual = YearMonth.now();
        YearMonth mesAnterior = mesActual.minusMonths(1);

        List<Movimiento> movimientos = movimientoRepository.findByEmpleadoOrderByFechaDescIdDesc(empleado);

        double[] totalesActual = totalesDelMes(movimientos, mesActual);
        double[] totalesAnterior = totalesDelMes(movimientos, mesAnterior);
        double balanceActual = redondear(totalesActual[0] - totalesActual[1]);
        double balanceAnterior = redondear(totalesAnterior[0] - totalesAnterior[1]);

        Double variacion = null;
        if (balanceAnterior != 0) {
            variacion = redondear(((balanceActual - balanceAnterior) / Math.abs(balanceAnterior)) * 100.0);
        }

        Map.Entry<String, Double> topGasto = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.GASTO && YearMonth.from(m.getFecha()).equals(mesActual))
                .collect(Collectors.groupingBy(Movimiento::getConcepto, Collectors.summingDouble(Movimiento::getImporte)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        int diasVacaciones = vacacionesRepository.findByEmpleadoAndEstado(empleado, EstadoVacacion.APROBADA).stream()
                .filter(s -> YearMonth.from(s.getFechaInicio()).equals(mesActual))
                .mapToInt(this::duracionEnDias)
                .sum();

        int diasTeletrabajo = teletrabajoRepository.findByEmpleadoAndMesAndAnio(empleado, mesActual.getMonthValue(), mesActual.getYear())
                .map(r -> r.getDiasTeletrabajados())
                .orElse(0);

        LocalDateTime inicioMes = mesActual.atDay(1).atStartOfDay();
        LocalDateTime finMes = mesActual.atEndOfMonth().atTime(LocalTime.MAX);
        int horasTrabajadas = (int) fichajeRepository.findByEmpleadoAndFechaHoraEntradaBetween(empleado, inicioMes, finMes).stream()
                .filter(f -> f.getFechaHoraSalida() != null)
                .mapToLong(f -> Duration.between(f.getFechaHoraEntrada(), f.getFechaHoraSalida()).toMinutes())
                .sum() / 60;

        String etiqueta = capitalizar(mesActual.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"))) + " " + mesActual.getYear();

        return WrappedDTO.builder()
                .mesEtiqueta(etiqueta)
                .ingresos(redondear(totalesActual[0]))
                .gastos(redondear(totalesActual[1]))
                .balance(balanceActual)
                .variacionBalancePorcentaje(variacion)
                .categoriaTopGasto(topGasto != null ? topGasto.getKey() : null)
                .importeCategoriaTopGasto(topGasto != null ? redondear(topGasto.getValue()) : null)
                .diasVacacionesDisfrutados(diasVacaciones)
                .horasTrabajadas(horasTrabajadas)
                .diasTeletrabajados(diasTeletrabajo)
                .build();
    }

    private double[] totalesDelMes(List<Movimiento> movimientos, YearMonth mes) {
        double ingresos = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.INGRESO && YearMonth.from(m.getFecha()).equals(mes))
                .mapToDouble(Movimiento::getImporte)
                .sum();
        double gastos = movimientos.stream()
                .filter(m -> m.getTipo() == TipoMovimiento.GASTO && YearMonth.from(m.getFecha()).equals(mes))
                .mapToDouble(Movimiento::getImporte)
                .sum();
        return new double[]{ingresos, gastos};
    }

    private int duracionEnDias(SolicitudVacaciones s) {
        return (int) (java.time.temporal.ChronoUnit.DAYS.between(s.getFechaInicio(), s.getFechaFin()) + 1);
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
