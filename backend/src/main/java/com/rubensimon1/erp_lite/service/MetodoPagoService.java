package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.MetodoPagoDTO;
import com.rubensimon1.erp_lite.dto.MetodoPagoInputDTO;
import com.rubensimon1.erp_lite.dto.MetodoPagoSensibleDTO;
import com.rubensimon1.erp_lite.entity.CuentaBancaria;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.MetodoPago;
import com.rubensimon1.erp_lite.repository.CuentaBancariaRepository;
import com.rubensimon1.erp_lite.repository.MetodoPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;
    private final CuentaBancariaRepository cuentaBancariaRepository;

    public MetodoPagoDTO crear(Empleado empleado, MetodoPagoInputDTO input) {
        MetodoPago metodo = new MetodoPago();
        metodo.setEmpleado(empleado);
        aplicarCambios(metodo, input);

        metodoPagoRepository.save(metodo);
        return toDTO(metodo);
    }

    public List<MetodoPagoDTO> misMetodos(Empleado empleado) {
        return metodoPagoRepository.findByEmpleadoOrderByIdDesc(empleado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MetodoPagoDTO actualizar(Empleado empleado, Long id, MetodoPagoInputDTO input) {
        MetodoPago metodo = obtenerPropio(empleado, id);
        aplicarCambios(metodo, input);

        metodoPagoRepository.save(metodo);
        return toDTO(metodo);
    }

    public void eliminar(Empleado empleado, Long id) {
        metodoPagoRepository.delete(obtenerPropio(empleado, id));
    }

    public MetodoPagoSensibleDTO obtenerSensible(Empleado empleado, Long id) {
        MetodoPago metodo = obtenerPropio(empleado, id);
        return MetodoPagoSensibleDTO.builder()
                .numero(metodo.getNumero())
                .cvv(metodo.getCvv())
                .emailPaypal(metodo.getEmailPaypal())
                .build();
    }

    private void aplicarCambios(MetodoPago metodo, MetodoPagoInputDTO input) {
        metodo.setTipo(input.getTipo());
        metodo.setAlias(input.getAlias());
        metodo.setTitular(input.getTitular());
        metodo.setFechaCaducidad(input.getFechaCaducidad());

        // Los campos sensibles solo se sobrescriben si vienen rellenos:
        // así al editar puedes cambiar el alias sin tener que reescribir el CVV.
        if (input.getNumero() != null && !input.getNumero().isBlank()) {
            metodo.setNumero(input.getNumero());
        }
        if (input.getCvv() != null && !input.getCvv().isBlank()) {
            metodo.setCvv(input.getCvv());
        }
        if (input.getEmailPaypal() != null && !input.getEmailPaypal().isBlank()) {
            metodo.setEmailPaypal(input.getEmailPaypal());
        }

        if (input.getCuentaVinculadaId() != null) {
            CuentaBancaria cuenta = cuentaBancariaRepository.findById(input.getCuentaVinculadaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta vinculada no encontrada"));
            if (!cuenta.getEmpleado().getId().equals(metodo.getEmpleado().getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esa cuenta no es tuya");
            }
            metodo.setCuentaVinculada(cuenta);
        } else {
            metodo.setCuentaVinculada(null);
        }
    }

    private MetodoPago obtenerPropio(Empleado empleado, Long id) {
        MetodoPago metodo = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Método de pago no encontrado"));

        if (!metodo.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a un método de pago de otro empleado");
        }
        return metodo;
    }

    private MetodoPagoDTO toDTO(MetodoPago m) {
        return MetodoPagoDTO.builder()
                .id(m.getId())
                .tipo(m.getTipo())
                .alias(m.getAlias())
                .titular(m.getTitular())
                .numeroEnmascarado(enmascararNumero(m.getNumero()))
                .fechaCaducidad(m.getFechaCaducidad())
                .emailEnmascarado(enmascararEmail(m.getEmailPaypal()))
                .cuentaVinculadaId(m.getCuentaVinculada() != null ? m.getCuentaVinculada().getId() : null)
                .cuentaVinculadaAlias(m.getCuentaVinculada() != null ? m.getCuentaVinculada().getAlias() : null)
                .build();
    }

    private String enmascararNumero(String numero) {
        if (numero == null || numero.isBlank()) return null;
        String limpio = numero.replaceAll("\\s+", "");
        if (limpio.length() < 4) return "••••";
        return "•••• •••• •••• " + limpio.substring(limpio.length() - 4);
    }

    private String enmascararEmail(String email) {
        if (email == null || email.isBlank()) return null;
        int arroba = email.indexOf('@');
        if (arroba <= 1) return "***" + email.substring(Math.max(arroba, 0));
        return email.charAt(0) + "***" + email.substring(arroba);
    }
}
