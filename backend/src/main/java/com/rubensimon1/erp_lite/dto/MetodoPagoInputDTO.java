package com.rubensimon1.erp_lite.dto;

import com.rubensimon1.erp_lite.entity.TipoMetodoPago;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MetodoPagoInputDTO {

    @NotNull(message = "Indica el tipo (TARJETA o PAYPAL)")
    private TipoMetodoPago tipo;

    @NotBlank(message = "El alias es obligatorio")
    private String alias;

    // --- Solo si tipo = TARJETA ---
    private String titular;
    private String numero;
    private String fechaCaducidad;
    private String cvv;

    // --- Solo si tipo = PAYPAL ---
    private String emailPaypal;

    // Cuenta bancaria a la que pertenece la tarjeta (opcional)
    private Long cuentaVinculadaId;
}
