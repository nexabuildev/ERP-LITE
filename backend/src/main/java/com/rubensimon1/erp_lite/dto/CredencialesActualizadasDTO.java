package com.rubensimon1.erp_lite.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CredencialesActualizadasDTO {
    private PerfilDTO perfil;
    private String token; // nuevo JWT: si cambia el email, el token anterior deja de ser valido
}
