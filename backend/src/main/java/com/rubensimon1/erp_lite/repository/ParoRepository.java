package com.rubensimon1.erp_lite.repository;

import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Paro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParoRepository extends JpaRepository<Paro, Long> {

    List<Paro> findByEmpleadoOrderByFechaInicioDesc(Empleado empleado);
}
