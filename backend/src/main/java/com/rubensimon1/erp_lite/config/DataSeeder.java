package com.rubensimon1.erp_lite.config;

import java.util.List;
import com.rubensimon1.erp_lite.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.rubensimon1.erp_lite.entity.Departamento;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Producto;
import com.rubensimon1.erp_lite.entity.Role;
import com.rubensimon1.erp_lite.repository.DepartamentoRepository;
import com.rubensimon1.erp_lite.repository.EmpleadoRepository;

@Component // 1. Estos le dice a Spring: "Carga esta clase al arrancar"
public class DataSeeder implements CommandLineRunner {
    /*
     * Variables
     */
    private final ProductoRepository productoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Constructor
     */
    public DataSeeder(
            DepartamentoRepository departamentoRepository, 
            EmpleadoRepository empleadoRepository,
            ProductoRepository productoRepository,
            PasswordEncoder passwordEncoder) {
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
        this.productoRepository = productoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Metodo que se ejecutara para crear la BBDD
     * String... args: argumentos que se le pasan al arrancar la aplicacion
     */
    @Override
    public void run(String... args) throws Exception {
        /*
         * Solo creamos datos si la base de datos está vacía
         */
        if (departamentoRepository.count() == 0) {
            Departamento it = new Departamento();
            it.setNombre("Tecnologia");
            it.setCodigo("IT-001");

            /*
             * Creamos el departamento RRHH
             */
            Departamento rrhh = new Departamento();
            rrhh.setNombre("Recursos Humanos");
            rrhh.setCodigo("RRHH-001");

            /*
             * Guardamos los departamentos primero (para tener ID)
             */
            departamentoRepository.saveAll(List.of(it, rrhh));

            /*
             * Crear empleados
             */
            Empleado dev = new Empleado();
            dev.setNombre("Ruben Developer");
            dev.setEmail("ruben@erplite.com");
            dev.setDepartamento(it); // Asignamos el objeto java, Spring se encarga del ID
            dev.setPassword(passwordEncoder.encode("1234"));    // Ciframos "1234" -> "$2a$10$..."
            dev.setRole(Role.ADMIN);    // Permisos totales

            /*
             * Creamos el empleado recruiter
             */
            Empleado recruiter = new Empleado();
            recruiter.setNombre("Ana Recruiter");
            recruiter.setEmail("ana@erplite.com");
            recruiter.setDepartamento(rrhh);
            recruiter.setPassword(passwordEncoder.encode("1234"));
            recruiter.setRole(Role.USER);   // Permisos limitados

            /*
             * Guardamos los empleados
             */
            empleadoRepository.saveAll(List.of(dev, recruiter));

            /*
             * Crear productos
             */
            Producto portatil = new Producto();
            portatil.setNombre("Ordenador Portatil");
            portatil.setDescripcion("Gama alta");
            portatil.setPrecio(1200.50);
            portatil.setStock(10);
            portatil.setDepartamento(it);

            /*
             * Crear producto PC
             */
            Producto pc = new Producto();
            pc.setNombre("PC Gaming");
            pc.setDescripcion("PC de sobremesa");
            pc.setPrecio(1100.00);
            pc.setStock(5);
            pc.setDepartamento(it);

            /*
             * Guardamos los productos
             */
            productoRepository.saveAll(List.of(portatil, pc));

            /*
             * Mensaje de confirmacion
             */
            System.out.println("✅ BASE DE DATOS INICIALIZADA CON DATOS DE PRUEBA");
        }
    }
}
