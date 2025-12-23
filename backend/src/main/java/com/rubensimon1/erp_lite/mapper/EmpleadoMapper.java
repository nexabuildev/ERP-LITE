package com.rubensimon1.erp_lite.mapper;

import com.rubensimon1.erp_lite.dto.EmpleadoDTO;
import com.rubensimon1.erp_lite.dto.EmpleadoInputDTO;
import com.rubensimon1.erp_lite.entity.Empleado;

public class EmpleadoMapper {
    /*
     * Convierte de Entidad (BBDD) -> DTO (JSON)
     */
    public static EmpleadoDTO toDTO(Empleado empleado) {
        /*
         * Si el empleado es null, devolvemos null
         */
        if (empleado == null) {
            return null;
        }

        /*
         * Creamos el DTO
         */
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(empleado.getId());
        dto.setNombre(empleado.getNombre());
        dto.setEmail(empleado.getEmail());

        /*
         * Mapeamos los datos del departamento si exite
         */
        if (empleado.getDepartamento() != null) {
            dto.setDepartamentoId(empleado.getDepartamento().getId());
            dto.setNombreDepartamento(empleado.getDepartamento().getNombre());
        }

        /*
         * Devolvemos el DTO
         */
        return dto;
    }

    /*
     * Convierte de DTO (JSON Entrada) -> Entidad (BBDD)
     */
    public static Empleado toEntity(EmpleadoInputDTO dto) {
        /*
         * Si el DTO es null, devolvemos null
         */
        if (dto == null) {
            return null;
        }

        /*
         * Creamos la entidad
         */
        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre());
        empleado.setEmail(dto.getEmail());

        /*
         * Truco JPA: Para asignar un departamento solo con el ID,
         * no hace falta buscarlo en la BBDD. Basta con crear un objeto vacío con ese
         * ID.
         */
        if (dto.getDepartamentoId() != null) {
            com.rubensimon1.erp_lite.entity.Departamento dep = new com.rubensimon1.erp_lite.entity.Departamento();
            dep.setId(dto.getDepartamentoId());
            empleado.setDepartamento(dep);
        }

        /*
         * Devolvemos la entidad
         */
        return empleado;
    }
}
