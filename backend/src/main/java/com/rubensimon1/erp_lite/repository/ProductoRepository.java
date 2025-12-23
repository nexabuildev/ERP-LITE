package com.rubensimon1.erp_lite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rubensimon1.erp_lite.entity.Producto;

@Repository // Indica que esta interface es un repositorio
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    /*
     * Metodos personalizados
     */
}
