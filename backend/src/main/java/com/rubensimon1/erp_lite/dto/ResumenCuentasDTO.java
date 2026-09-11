package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumenCuentasDTO {
    private double totalGeneral;
    private double totalBanco;
    private double totalEfectivo;
    private double totalPaypal;
    private List<CuentaBancariaDTO> cuentas;
}
