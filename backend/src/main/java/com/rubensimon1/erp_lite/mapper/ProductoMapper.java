package com.rubensimon1.erp_lite.mapper;

import com.rubensimon1.erp_lite.dto.ProductoDTO;
import com.rubensimon1.erp_lite.dto.ProductoInputDTO;
import com.rubensimon1.erp_lite.entity.Producto;

public class ProductoMapper {
    /*
     * Convierte de Entidad (BBDD) -> DTO (JSON)
     */
    public static ProductoDTO toDTO(Producto producto) {
        /*
         * Si el producto es null, devolvemos null
         */
        if (producto == null) {
            return null;
        }

        /*
         * Creamos el DTO
         */
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());

        /*
         * Mapeamos los datos del departamento si exite
         */
        if (producto.getDepartamento() != null) {
            dto.setDepartamentoId(producto.getDepartamento().getId());
            dto.setNombreDepartamento(producto.getDepartamento().getNombre());
        }

        /*
         * Devolvemos el DTO
         */
        return dto;
    }

    /*
     * Convierte de DTO (JSON) -> Entidad (BBDD)
     */
    public static Producto toEntity(ProductoInputDTO dto) {
        /*
         * Si el DTO es null, devolvemos null
         */
        if (dto == null) {
            return null;
        }

        /*
         * Creamos la entidad
         */
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());

        /*
         * Truco JPA: Para asignar un departamento solo con el ID,
         * no hace falta buscarlo en la BBDD. Basta con crear un objeto vacío con ese
         * ID.
         */
        if (dto.getDepartamentoId() != null) {
            com.rubensimon1.erp_lite.entity.Departamento dep = new com.rubensimon1.erp_lite.entity.Departamento();
            dep.setId(dto.getDepartamentoId());
            producto.setDepartamento(dep);
        }

        /*
         * Devolvemos la entidad
         */
        return producto;
    }
}
