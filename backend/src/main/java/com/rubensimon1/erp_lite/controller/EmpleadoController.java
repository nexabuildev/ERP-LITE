package com.rubensimon1.erp_lite.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.rubensimon1.erp_lite.dto.EmpleadoDTO;
import com.rubensimon1.erp_lite.dto.EmpleadoInputDTO;
import com.rubensimon1.erp_lite.service.EmpleadoService;
import jakarta.validation.Valid;

@RestController // <--- Importante: Convierte esto en una API REST
@RequestMapping("/api/v1/empleados") // <--- La URL base
@CrossOrigin(origins = "http://localhost:5173")
public class EmpleadoController {
    /*
     * Variable de tipo EmpleadoService
     */
    private final EmpleadoService empleadoService;

    /*
     * El constructor recibe un objeto EmpleadoService
     */
    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    /*
     * Capturar empleados
     * GetMapping: Indica que el metodo responde a peticiones HTTP GET
     * List<EmpleadoDTO>: Indica que el metodo retorna una lista de empleados
     */
    @GetMapping
    public List<EmpleadoDTO> getAll() {
        return empleadoService.obtenerTodos();
    }

    /*
     * Metodo para almacenar empleados
     * PostMapping: Indica que el metodo responde a peticiones HTTP POST
     * Valid: Activa las validaciones del DTO. Si fallan, lanza una expecion y no
     * entra al metodo
     */
    @PostMapping
    @Valid
    public EmpleadoDTO create(@RequestBody @Valid EmpleadoInputDTO inputDTO) {
        return empleadoService.registrarEmpleado(inputDTO);
    }
}
