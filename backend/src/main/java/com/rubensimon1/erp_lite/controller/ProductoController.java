package com.rubensimon1.erp_lite.controller;

import java.util.*;
import org.springframework.web.bind.annotation.*;
import com.rubensimon1.erp_lite.dto.ProductoDTO;
import com.rubensimon1.erp_lite.dto.ProductoInputDTO;
import com.rubensimon1.erp_lite.repository.ProductoRepository;
import com.rubensimon1.erp_lite.service.ProductoService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController // <--- Importante: Convierte esto en una API REST
@RequestMapping("/api/productos") // <--- La URL base
public class ProductoController {
    /*
     * Variable de tipo ProductoService
     */
    private final ProductoService productoService;

    /*
     * Constructor
     */
    public ProductoController(ProductoRepository productoRepository, ProductoService productoService) {
        this.productoService = productoService;
    }

    /*
     * GET: Listar todos
     */
    @GetMapping
    public List<ProductoDTO> obtenerTodos() {
        return productoService.obtenerProductos();
    }

    /*
     * POST: Crear uno nuevo
     */
    @PostMapping
    @Valid
    public ProductoDTO create(@RequestBody @Valid ProductoInputDTO inputDTO) {
        return productoService.registrarProducto(inputDTO);
    }
}
