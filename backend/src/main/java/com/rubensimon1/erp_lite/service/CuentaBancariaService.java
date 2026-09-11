package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.CuentaBancariaDTO;
import com.rubensimon1.erp_lite.dto.CuentaBancariaInputDTO;
import com.rubensimon1.erp_lite.entity.CuentaBancaria;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.repository.CuentaBancariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentaBancariaService {

    private final CuentaBancariaRepository cuentaBancariaRepository;

    public CuentaBancariaDTO anadir(Empleado empleado, CuentaBancariaInputDTO input) {
        if (input.isPrincipal()) {
            quitarPrincipalActual(empleado);
        }

        CuentaBancaria cuenta = new CuentaBancaria();
        cuenta.setEmpleado(empleado);
        cuenta.setAlias(input.getAlias());
        cuenta.setIban(input.getIban());
        cuenta.setBanco(input.getBanco());
        cuenta.setPrincipal(input.isPrincipal());

        cuentaBancariaRepository.save(cuenta);
        return toDTO(cuenta);
    }

    public List<CuentaBancariaDTO> misCuentas(Empleado empleado) {
        return cuentaBancariaRepository.findByEmpleadoOrderByPrincipalDescIdAsc(empleado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void eliminar(Empleado empleado, Long id) {
        CuentaBancaria cuenta = obtenerPropia(empleado, id);
        cuentaBancariaRepository.delete(cuenta);
    }

    private void quitarPrincipalActual(Empleado empleado) {
        cuentaBancariaRepository.findByEmpleadoOrderByPrincipalDescIdAsc(empleado).stream()
                .filter(CuentaBancaria::isPrincipal)
                .forEach(c -> {
                    c.setPrincipal(false);
                    cuentaBancariaRepository.save(c);
                });
    }

    private CuentaBancaria obtenerPropia(Empleado empleado, Long id) {
        CuentaBancaria cuenta = cuentaBancariaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));

        if (!cuenta.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar una cuenta de otro empleado");
        }
        return cuenta;
    }

    private CuentaBancariaDTO toDTO(CuentaBancaria c) {
        return CuentaBancariaDTO.builder()
                .id(c.getId())
                .alias(c.getAlias())
                .iban(c.getIban())
                .banco(c.getBanco())
                .principal(c.isPrincipal())
                .build();
    }
}
