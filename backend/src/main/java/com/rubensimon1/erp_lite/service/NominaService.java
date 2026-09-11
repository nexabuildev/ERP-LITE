package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.dto.NominaDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.repository.NominaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NominaService {

    private final NominaRepository nominaRepository;

    public List<NominaDTO> misNominas(Empleado empleado) {
        return nominaRepository.findByEmpleadoOrderByAnioDescMesDesc(empleado)
                .stream()
                .map(n -> NominaDTO.builder()
                        .id(n.getId())
                        .mes(n.getMes())
                        .anio(n.getAnio())
                        .salarioBruto(n.getSalarioBruto())
                        .deducciones(n.getDeducciones())
                        .salarioNeto(n.getSalarioNeto())
                        .fechaPago(n.getFechaPago())
                        .build())
                .collect(Collectors.toList());
    }
}
