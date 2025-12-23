package com.rubensimon1.erp_lite.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.rubensimon1.erp_lite.dto.ProductoDTO;
import com.rubensimon1.erp_lite.dto.ProductoInputDTO;
import com.rubensimon1.erp_lite.entity.Departamento;
import com.rubensimon1.erp_lite.entity.Producto;
import com.rubensimon1.erp_lite.mapper.ProductoMapper;
import com.rubensimon1.erp_lite.repository.DepartamentoRepository;
import com.rubensimon1.erp_lite.repository.ProductoRepository;

@Service // 1. Indica que aquí vive la lógica de negocio
public class ProductoService {

    /*
     * Variables
     */
    private final ProductoRepository productoRepository;
    private final DepartamentoRepository departamentoRepository;

    /*
     * Constructor
     */
    public ProductoService(ProductoRepository productoRepository, DepartamentoRepository departamentoRepository) {
        this.productoRepository = productoRepository;
        this.departamentoRepository = departamentoRepository;
    }

    /*
     * Logica para listar
     */
    public List<ProductoDTO> obtenerProductos() {
        return productoRepository.findAll().stream()
                .map(ProductoMapper::toDTO)
                .collect(Collectors.toList());
    }

    /*
     * Logica para crear productos nuevos
     */
    public ProductoDTO registrarProducto(ProductoInputDTO inputDto) {
        /*
         * 1. Convertimos el DTO a Entidad
         */
        Producto producto = ProductoMapper.toEntity(inputDto);

        /*
         * 2. Buscar el departamento real en la BBDD
         */
        if (inputDto.getDepartamentoId() != null) {
            Departamento dep = departamentoRepository.findById(inputDto.getDepartamentoId())
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));

            /*
             * 3. lo añadimos al departamento de it o ventas
             */
            producto.setDepartamento(dep);
        }

        /*
         * 4. Guardamos el producto
         */
        Producto productoGuardado = productoRepository.save(producto);

        /*
         * 5. Convertimos el producto guardado a DTO
         */
        return ProductoMapper.toDTO(productoGuardado);
    }
}
