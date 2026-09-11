package com.rubensimon1.erp_lite.repository;

import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Genero;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interface es un repositorio
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    /*
     * Metodos personalizados
     */
    Optional<Empleado> findByEmail(String email);

    /*
     * Agregados para el registro retributivo (RD 902/2020)
     */
    @Query("SELECT AVG(e.salarioBrutoAnual) FROM Empleado e WHERE e.categoriaProfesional = :categoria")
    Double mediaSalarioPorCategoria(@Param("categoria") String categoria);

    @Query("SELECT AVG(e.salarioBrutoAnual) FROM Empleado e WHERE e.genero = :genero")
    Double mediaSalarioPorGenero(@Param("genero") Genero genero);
}