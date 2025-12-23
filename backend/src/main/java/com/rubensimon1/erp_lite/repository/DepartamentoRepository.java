package com.rubensimon1.erp_lite.repository;

import com.rubensimon1.erp_lite.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interface es un repositorio
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    /*
     * Metodos personalizados
     */
}
