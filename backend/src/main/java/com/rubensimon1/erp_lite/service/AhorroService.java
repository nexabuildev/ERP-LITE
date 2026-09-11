package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.AportacionInputDTO;
import com.rubensimon1.erp_lite.dto.MetaAhorroDTO;
import com.rubensimon1.erp_lite.dto.MetaAhorroInputDTO;
import com.rubensimon1.erp_lite.entity.CuentaBancaria;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.MetaAhorro;
import com.rubensimon1.erp_lite.repository.CuentaBancariaRepository;
import com.rubensimon1.erp_lite.repository.MetaAhorroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AhorroService {

    private final MetaAhorroRepository metaAhorroRepository;
    private final CuentaBancariaRepository cuentaBancariaRepository;

    public MetaAhorroDTO crear(Empleado empleado, MetaAhorroInputDTO input) {
        MetaAhorro meta = new MetaAhorro();
        meta.setEmpleado(empleado);
        meta.setNombre(input.getNombre());
        meta.setMontoObjetivo(input.getMontoObjetivo());
        meta.setMontoActual(0.0);
        meta.setFechaObjetivo(input.getFechaObjetivo());
        meta.setCuentaOrigen(resolverCuenta(empleado, input.getCuentaOrigenId()));

        metaAhorroRepository.save(meta);
        return toDTO(meta);
    }

    public List<MetaAhorroDTO> misMetas(Empleado empleado) {
        return metaAhorroRepository.findByEmpleadoOrderByIdDesc(empleado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MetaAhorroDTO actualizar(Empleado empleado, Long id, MetaAhorroInputDTO input) {
        MetaAhorro meta = obtenerPropia(empleado, id);
        meta.setNombre(input.getNombre());
        meta.setMontoObjetivo(input.getMontoObjetivo());
        meta.setFechaObjetivo(input.getFechaObjetivo());
        meta.setCuentaOrigen(resolverCuenta(empleado, input.getCuentaOrigenId()));

        metaAhorroRepository.save(meta);
        return toDTO(meta);
    }

    @Transactional
    public MetaAhorroDTO aportar(Empleado empleado, Long id, AportacionInputDTO input) {
        MetaAhorro meta = obtenerPropia(empleado, id);
        meta.setMontoActual(meta.getMontoActual() + input.getMonto());
        metaAhorroRepository.save(meta);

        // El dinero "sale" de la cuenta origen hacia el ahorro
        ajustarCuentaOrigen(meta, -input.getMonto());
        return toDTO(meta);
    }

    @Transactional
    public MetaAhorroDTO retirar(Empleado empleado, Long id, AportacionInputDTO input) {
        MetaAhorro meta = obtenerPropia(empleado, id);
        meta.setMontoActual(Math.max(0.0, meta.getMontoActual() - input.getMonto()));
        metaAhorroRepository.save(meta);

        // El dinero "vuelve" del ahorro a la cuenta origen
        ajustarCuentaOrigen(meta, input.getMonto());
        return toDTO(meta);
    }

    public void eliminar(Empleado empleado, Long id) {
        metaAhorroRepository.delete(obtenerPropia(empleado, id));
    }

    private void ajustarCuentaOrigen(MetaAhorro meta, double delta) {
        CuentaBancaria cuenta = meta.getCuentaOrigen();
        if (cuenta == null) return;

        double actual = cuenta.getSaldoActual() != null ? cuenta.getSaldoActual() : 0.0;
        cuenta.setSaldoActual(Math.round((actual + delta) * 100.0) / 100.0);
        cuentaBancariaRepository.save(cuenta);
    }

    private CuentaBancaria resolverCuenta(Empleado empleado, Long cuentaId) {
        if (cuentaId == null) return null;
        CuentaBancaria cuenta = cuentaBancariaRepository.findById(cuentaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
        if (!cuenta.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esa cuenta no es tuya");
        }
        return cuenta;
    }

    private MetaAhorro obtenerPropia(Empleado empleado, Long id) {
        MetaAhorro meta = metaAhorroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta de ahorro no encontrada"));

        if (!meta.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar una meta de otro empleado");
        }
        return meta;
    }

    private MetaAhorroDTO toDTO(MetaAhorro m) {
        double porcentaje = m.getMontoObjetivo() > 0
                ? Math.min(100.0, Math.round((m.getMontoActual() / m.getMontoObjetivo()) * 10000.0) / 100.0)
                : 0.0;

        return MetaAhorroDTO.builder()
                .id(m.getId())
                .nombre(m.getNombre())
                .montoObjetivo(m.getMontoObjetivo())
                .montoActual(m.getMontoActual())
                .fechaObjetivo(m.getFechaObjetivo())
                .porcentajeCompletado(porcentaje)
                .cuentaOrigenId(m.getCuentaOrigen() != null ? m.getCuentaOrigen().getId() : null)
                .cuentaOrigenAlias(m.getCuentaOrigen() != null ? m.getCuentaOrigen().getAlias() : null)
                .build();
    }
}
