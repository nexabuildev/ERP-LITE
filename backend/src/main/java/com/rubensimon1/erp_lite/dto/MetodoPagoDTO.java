package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.TipoMetodoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetodoPagoDTO {
    private Long id;
    private TipoMetodoPago tipo;
    private String alias;
    private String titular;
    private String numeroEnmascarado; // Ej: "•••• •••• •••• 1234"
    private String fechaCaducidad;
    private String emailEnmascarado; // Ej: "j***@gmail.com"
    private Long cuentaVinculadaId;
    private String cuentaVinculadaAlias;
}
