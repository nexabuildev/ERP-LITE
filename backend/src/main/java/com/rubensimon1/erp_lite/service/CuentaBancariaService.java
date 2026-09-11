package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.CuentaBancariaDTO;
import com.rubensimon1.erp_lite.dto.CuentaBancariaInputDTO;
import com.rubensimon1.erp_lite.dto.ResumenCuentasDTO;
import com.rubensimon1.erp_lite.entity.CategoriaCuenta;
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
        CuentaBancaria cuenta = new CuentaBancaria();
        cuenta.setEmpleado(empleado);
        aplicarCambios(cuenta, input);
        // El saldo inicial solo se fija al crear la cuenta.
        cuenta.setSaldoActual(input.getSaldoActual() != null ? input.getSaldoActual() : 0.0);

        cuentaBancariaRepository.save(cuenta);
        return toDTO(cuenta);
    }

    public List<CuentaBancariaDTO> misCuentas(Empleado empleado) {
        return cuentaBancariaRepository.findByEmpleadoOrderByIdAsc(empleado)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CuentaBancariaDTO actualizar(Empleado empleado, Long id, CuentaBancariaInputDTO input) {
        CuentaBancaria cuenta = obtenerPropia(empleado, id);
        aplicarCambios(cuenta, input);
        // saldoActual NO se toca aquí: a partir de la creación solo cambia por
        // movimientos, ajustes y aportaciones de ahorro (ver MovimientoService/AhorroService).

        cuentaBancariaRepository.save(cuenta);
        return toDTO(cuenta);
    }

    public void eliminar(Empleado empleado, Long id) {
        cuentaBancariaRepository.delete(obtenerPropia(empleado, id));
    }

    public ResumenCuentasDTO resumen(Empleado empleado) {
        List<CuentaBancaria> cuentas = cuentaBancariaRepository.findByEmpleadoOrderByIdAsc(empleado);

        double totalBanco = cuentas.stream()
                .filter(c -> c.getCategoria() == CategoriaCuenta.BANCO)
                .mapToDouble(c -> c.getSaldoActual() != null ? c.getSaldoActual() : 0.0)
                .sum();

        double totalEfectivo = cuentas.stream()
                .filter(c -> c.getCategoria() == CategoriaCuenta.EFECTIVO)
                .mapToDouble(c -> c.getSaldoActual() != null ? c.getSaldoActual() : 0.0)
                .sum();

        double totalPaypal = cuentas.stream()
                .filter(c -> c.getCategoria() == CategoriaCuenta.PAYPAL)
                .mapToDouble(c -> c.getSaldoActual() != null ? c.getSaldoActual() : 0.0)
                .sum();

        return ResumenCuentasDTO.builder()
                .totalGeneral(redondear(totalBanco + totalEfectivo + totalPaypal))
                .totalBanco(redondear(totalBanco))
                .totalEfectivo(redondear(totalEfectivo))
                .totalPaypal(redondear(totalPaypal))
                .cuentas(cuentas.stream().map(this::toDTO).collect(Collectors.toList()))
                .build();
    }

    private void aplicarCambios(CuentaBancaria cuenta, CuentaBancariaInputDTO input) {
        cuenta.setAlias(input.getAlias());
        cuenta.setCategoria(input.getCategoria());
        cuenta.setTipoCuenta(input.getTipoCuenta());

        if (input.getCategoria() == CategoriaCuenta.BANCO) {
            cuenta.setIban(input.getIban());
            cuenta.setBanco(input.getBanco());
        } else {
            cuenta.setIban(null);
            cuenta.setBanco(null);
        }
    }

    private CuentaBancaria obtenerPropia(Empleado empleado, Long id) {
        CuentaBancaria cuenta = cuentaBancariaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));

        if (!cuenta.getEmpleado().getId().equals(empleado.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar una cuenta de otro empleado");
        }
        return cuenta;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private CuentaBancariaDTO toDTO(CuentaBancaria c) {
        return CuentaBancariaDTO.builder()
                .id(c.getId())
                .alias(c.getAlias())
                .categoria(c.getCategoria())
                .tipoCuenta(c.getTipoCuenta())
                .iban(c.getIban())
                .banco(c.getBanco())
                .saldoActual(c.getSaldoActual())
                .build();
    }
}
