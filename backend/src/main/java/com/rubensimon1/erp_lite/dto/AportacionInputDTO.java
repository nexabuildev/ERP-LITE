package com.rubensimon1.erp_lite.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AportacionInputDTO {

    @NotNull
    @Positive(message = "La aportación debe ser mayor que cero")
    private Double monto;
}
