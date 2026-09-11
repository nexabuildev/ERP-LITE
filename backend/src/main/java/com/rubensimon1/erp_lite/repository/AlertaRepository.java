package com.rubensimon1.erp_lite.repository;

import com.rubensimon1.erp_lite.entity.Alerta;
import com.rubensimon1.erp_lite.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByEmpleadoOrderByFechaVencimientoAsc(Empleado empleado);
}
