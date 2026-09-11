package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.RegistroRetributivoDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Genero;
import com.rubensimon1.erp_lite.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistroRetributivoService {

    private final EmpleadoRepository empleadoRepository;

    public RegistroRetributivoDTO miComparativa(Empleado empleado) {
        Double mediaCategoria = empleado.getCategoriaProfesional() != null
                ? empleadoRepository.mediaSalarioPorCategoria(empleado.getCategoriaProfesional())
                : null;

        Double mediaHombres = empleadoRepository.mediaSalarioPorGenero(Genero.HOMBRE);
        Double mediaMujeres = empleadoRepository.mediaSalarioPorGenero(Genero.MUJER);

        Double brecha = null;
        if (mediaHombres != null && mediaMujeres != null && mediaHombres > 0) {
            brecha = Math.round(((mediaHombres - mediaMujeres) / mediaHombres) * 10000.0) / 100.0;
        }

        return RegistroRetributivoDTO.builder()
                .miSalario(empleado.getSalarioBrutoAnual())
                .miCategoria(empleado.getCategoriaProfesional())
                .mediaCategoria(redondear(mediaCategoria))
                .mediaHombres(redondear(mediaHombres))
                .mediaMujeres(redondear(mediaMujeres))
                .brechaPorcentaje(brecha)
                .build();
    }

    private Double redondear(Double valor) {
        return valor != null ? Math.round(valor * 100.0) / 100.0 : null;
    }
}
