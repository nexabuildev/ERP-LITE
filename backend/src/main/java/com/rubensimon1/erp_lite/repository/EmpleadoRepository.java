package com.rubensimon1.erp_lite.repository;

import com.rubensimon1.erp_lite.entity.Empleado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interface es un repositorio
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    /*
     * Metodos personalizados
     */
    Optional<Empleado> findByEmail(String email);
}