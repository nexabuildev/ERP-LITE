package com.rubensimon1.erp_lite.repository;

import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.RegistroTeletrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroTeletrabajoRepository extends JpaRepository<RegistroTeletrabajo, Long> {

    List<RegistroTeletrabajo> findByEmpleadoOrderByAnioDescMesDesc(Empleado empleado);

    Optional<RegistroTeletrabajo> findByEmpleadoAndMesAndAnio(Empleado empleado, Integer mes, Integer anio);
}
