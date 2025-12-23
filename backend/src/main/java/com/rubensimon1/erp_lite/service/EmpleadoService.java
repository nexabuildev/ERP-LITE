package com.rubensimon1.erp_lite.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.rubensimon1.erp_lite.dto.EmpleadoDTO;
import com.rubensimon1.erp_lite.dto.EmpleadoInputDTO;
import com.rubensimon1.erp_lite.entity.Departamento;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.mapper.EmpleadoMapper;
import com.rubensimon1.erp_lite.repository.DepartamentoRepository;
import com.rubensimon1.erp_lite.repository.EmpleadoRepository;

@Service // 1. Indica que aquí vive la lógica de negocio
public class EmpleadoService {
    /*
     * Variables
     */
    private final EmpleadoRepository empleadoRepository;
    private final DepartamentoRepository departamentoRepository;

    /*
     * Constructor
     */
    public EmpleadoService(EmpleadoRepository empleadoRepository, DepartamentoRepository departamentoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    /*
     * Logica para listar
     */
    public List<EmpleadoDTO> obtenerTodos() {
        /*
         * 1. Obtenemos todos los empleados de la BBDD
         */
        return empleadoRepository.findAll().stream()
                .map(EmpleadoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /*
     * Logica para CREAR (Aqui arreglamos el NULL)
     */
    public EmpleadoDTO registrarEmpleado(EmpleadoInputDTO inputDto) {
        /*
         * 1. Convertimos el DTO a Entidad
         */
        Empleado empleado = EmpleadoMapper.toEntity(inputDto);

        /*
         * 2. BUSCAMOS el departamento real en la BBDD (El paso que faltaba)
         */
        if (inputDto.getDepartamentoId() != null) {
            Departamento dep = departamentoRepository.findById(inputDto.getDepartamentoId())
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));

            /*
             * 3. Asignamos el departamento REAL (con nombre y todo)
             */
            empleado.setDepartamento(dep);
        }

        /*
         * 4. Guardamos
         */
        Empleado empleadoGuardado = empleadoRepository.save(empleado);

        /*
         * 5. Devolvemos el DTO (ahora sí tendrá el nombre del departamento)
         */
        return EmpleadoMapper.toDTO(empleadoGuardado);
    }
}
