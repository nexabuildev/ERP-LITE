package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CuentaBancariaDTO {
    private Long id;
    private String alias;
    private String iban;
    private String banco;
    private boolean principal;
}
