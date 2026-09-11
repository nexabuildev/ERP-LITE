package com.rubensimon1.erp_lite.repository;

import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.EstadoVacacion;
import com.rubensimon1.erp_lite.entity.SolicitudVacaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudVacacionesRepository extends JpaRepository<SolicitudVacaciones, Long> {

    List<SolicitudVacaciones> findByEmpleadoOrderByFechaSolicitudDesc(Empleado empleado);

    List<SolicitudVacaciones> findByEmpleadoAndEstado(Empleado empleado, EstadoVacacion estado);

    List<SolicitudVacaciones> findByEstadoOrderByFechaSolicitudAsc(EstadoVacacion estado);
}
