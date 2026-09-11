package com.rubensimon1.erp_lite.repository;

import com.rubensimon1.erp_lite.entity.DeclaracionPresentada;
import com.rubensimon1.erp_lite.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeclaracionPresentadaRepository extends JpaRepository<DeclaracionPresentada, Long> {

    List<DeclaracionPresentada> findByEmpleadoOrderByFechaPresentacionDesc(Empleado empleado);
}
