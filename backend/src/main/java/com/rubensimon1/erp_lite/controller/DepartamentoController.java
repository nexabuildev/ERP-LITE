package com.rubensimon1.erp_lite.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rubensimon1.erp_lite.entity.Departamento;
import com.rubensimon1.erp_lite.repository.DepartamentoRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController // <--- Importante: Convierte esto en una API REST
@RequestMapping("/api/departamentos") // <--- La URL base
public class DepartamentoController {

    /*
     * Variable de tipo DepartamentoRepository
     */
    private final DepartamentoRepository departamentoRepository;

    /*
     * Constructor
     */
    public DepartamentoController(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    /*
     * GET: Ver todos
     */
    @GetMapping
    public List<Departamento> getAll() {
        return departamentoRepository.findAll();
    }

    /*
     * POST: Crear uno
     */
    @PostMapping
    public Departamento create(@RequestBody Departamento departamento) {
        return departamentoRepository.save(departamento);
    }
}
