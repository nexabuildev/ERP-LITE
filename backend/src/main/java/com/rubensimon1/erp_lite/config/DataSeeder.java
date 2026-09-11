package com.rubensimon1.erp_lite.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.rubensimon1.erp_lite.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.rubensimon1.erp_lite.entity.Departamento;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.EstadoVacacion;
import com.rubensimon1.erp_lite.entity.Genero;
import com.rubensimon1.erp_lite.entity.Nomina;
import com.rubensimon1.erp_lite.entity.Producto;
import com.rubensimon1.erp_lite.entity.Role;
import com.rubensimon1.erp_lite.entity.SolicitudVacaciones;
import com.rubensimon1.erp_lite.entity.TipoTrabajador;
import com.rubensimon1.erp_lite.repository.DepartamentoRepository;
import com.rubensimon1.erp_lite.repository.EmpleadoRepository;
import com.rubensimon1.erp_lite.repository.NominaRepository;
import com.rubensimon1.erp_lite.repository.SolicitudVacacionesRepository;

@Component // 1. Estos le dice a Spring: "Carga esta clase al arrancar"
public class DataSeeder implements CommandLineRunner {
    /*
     * Variables
     */
    private final ProductoRepository productoRepository;
    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final NominaRepository nominaRepository;
    private final SolicitudVacacionesRepository vacacionesRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Constructor
     */
    public DataSeeder(
            DepartamentoRepository departamentoRepository,
            EmpleadoRepository empleadoRepository,
            ProductoRepository productoRepository,
            NominaRepository nominaRepository,
            SolicitudVacacionesRepository vacacionesRepository,
            PasswordEncoder passwordEncoder) {
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
        this.productoRepository = productoRepository;
        this.nominaRepository = nominaRepository;
        this.vacacionesRepository = vacacionesRepository;
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
             * Crear empleados. Se enriquecen con los campos del portal del empleado:
             * tipoTrabajador, genero y categoriaProfesional (usados en fichajes,
             * registro retributivo y declaraciones fiscales).
             */
            Empleado dev = new Empleado();
            dev.setNombre("Ruben Developer");
            dev.setEmail("ruben@erplite.com");
            dev.setDepartamento(it);
            dev.setPassword(passwordEncoder.encode("1234"));
            dev.setRole(Role.ADMIN);
            dev.setTipoTrabajador(TipoTrabajador.ASALARIADO);
            dev.setGenero(Genero.HOMBRE);
            dev.setCategoriaProfesional("Desarrollador");
            dev.setSalarioBrutoAnual(32000.0);

            Empleado recruiter = new Empleado();
            recruiter.setNombre("Ana Recruiter");
            recruiter.setEmail("ana@erplite.com");
            recruiter.setDepartamento(rrhh);
            recruiter.setPassword(passwordEncoder.encode("1234"));
            recruiter.setRole(Role.USER);
            recruiter.setTipoTrabajador(TipoTrabajador.ASALARIADO);
            recruiter.setGenero(Genero.MUJER);
            recruiter.setCategoriaProfesional("Reclutadora");
            recruiter.setSalarioBrutoAnual(26000.0);

            /*
             * Misma categoria que "dev" pero distinto genero y salario:
             * permite que el registro retributivo muestre una comparativa real.
             */
            Empleado marta = new Empleado();
            marta.setNombre("Marta Dev");
            marta.setEmail("marta@erplite.com");
            marta.setDepartamento(it);
            marta.setPassword(passwordEncoder.encode("1234"));
            marta.setRole(Role.USER);
            marta.setTipoTrabajador(TipoTrabajador.ASALARIADO);
            marta.setGenero(Genero.MUJER);
            marta.setCategoriaProfesional("Desarrollador");
            marta.setSalarioBrutoAnual(29000.0);

            /*
             * Trabajador autonomo: demuestra el perfil ampliado y las
             * declaraciones fiscales trimestrales (Modelo 303/130).
             */
            Empleado carlos = new Empleado();
            carlos.setNombre("Carlos Freelance");
            carlos.setEmail("carlos@erplite.com");
            carlos.setPassword(passwordEncoder.encode("1234"));
            carlos.setRole(Role.USER);
            carlos.setTipoTrabajador(TipoTrabajador.AUTONOMO);
            carlos.setGenero(Genero.HOMBRE);
            carlos.setCategoriaProfesional("Consultor");
            carlos.setSalarioBrutoAnual(40000.0);
            carlos.setNif("12345678Z");
            carlos.setEpigrafeIae("751 - Servicios de consultoria");
            carlos.setFechaAltaAutonomo(LocalDate.of(2021, 3, 15));

            /*
             * Guardamos los empleados
             */
            empleadoRepository.saveAll(List.of(dev, recruiter, marta, carlos));

            /*
             * Nominas de los ultimos 3 meses para dev, recruiter y marta (asalariados).
             * Carlos, al ser autonomo, no recibe nomina.
             */
            for (Empleado empleado : List.of(dev, recruiter, marta)) {
                for (int i = 1; i <= 3; i++) {
                    LocalDate mesNomina = LocalDate.now().minusMonths(i);
                    double bruto = empleado.getSalarioBrutoAnual() / 12.0;
                    double deducciones = bruto * 0.22; // aprox. IRPF + Seguridad Social
                    double neto = bruto - deducciones;

                    Nomina nomina = new Nomina();
                    nomina.setEmpleado(empleado);
                    nomina.setMes(mesNomina.getMonthValue());
                    nomina.setAnio(mesNomina.getYear());
                    nomina.setSalarioBruto(redondear(bruto));
                    nomina.setDeducciones(redondear(deducciones));
                    nomina.setSalarioNeto(redondear(neto));
                    nomina.setFechaPago(mesNomina.withDayOfMonth(mesNomina.lengthOfMonth()));
                    nominaRepository.save(nomina);
                }
            }

            /*
             * Una solicitud de vacaciones ya aprobada, para que el portal
             * no se vea vacio en la primera carga.
             */
            SolicitudVacaciones vacaciones = new SolicitudVacaciones();
            vacaciones.setEmpleado(recruiter);
            vacaciones.setFechaInicio(LocalDate.now().minusMonths(2));
            vacaciones.setFechaFin(LocalDate.now().minusMonths(2).plusDays(4));
            vacaciones.setMotivo("Vacaciones de verano");
            vacaciones.setEstado(EstadoVacacion.APROBADA);
            vacaciones.setFechaSolicitud(LocalDateTime.now().minusMonths(2).minusDays(10));
            vacacionesRepository.save(vacaciones);

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

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
