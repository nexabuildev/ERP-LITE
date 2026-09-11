package com.rubensimon1.erp_lite.repository;

import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Fichaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, Long> {

    List<Fichaje> findByEmpleadoOrderByFechaHoraEntradaDesc(Empleado empleado);

    Optional<Fichaje> findByEmpleadoAndFechaHoraSalidaIsNull(Empleado empleado);

    List<Fichaje> findByEmpleadoAndFechaHoraEntradaBetween(Empleado empleado, LocalDateTime desde, LocalDateTime hasta);
}
