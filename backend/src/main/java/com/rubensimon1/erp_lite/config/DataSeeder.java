package com.rubensimon1.erp_lite.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.rubensimon1.erp_lite.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.rubensimon1.erp_lite.entity.AjusteVacaciones;
import com.rubensimon1.erp_lite.entity.Alerta;
import com.rubensimon1.erp_lite.entity.CategoriaCuenta;
import com.rubensimon1.erp_lite.entity.CuentaBancaria;
import com.rubensimon1.erp_lite.entity.TipoCuenta;
import com.rubensimon1.erp_lite.entity.DeclaracionPresentada;
import com.rubensimon1.erp_lite.entity.Departamento;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Empresa;
import com.rubensimon1.erp_lite.entity.EstadoVacacion;
import com.rubensimon1.erp_lite.entity.Genero;
import com.rubensimon1.erp_lite.entity.MetaAhorro;
import com.rubensimon1.erp_lite.entity.MetodoPago;
import com.rubensimon1.erp_lite.entity.Movimiento;
import com.rubensimon1.erp_lite.entity.Nomina;
import com.rubensimon1.erp_lite.entity.Producto;
import com.rubensimon1.erp_lite.entity.Role;
import com.rubensimon1.erp_lite.entity.SolicitudVacaciones;
import com.rubensimon1.erp_lite.entity.TipoMetodoPago;
import com.rubensimon1.erp_lite.entity.TipoMovimiento;
import com.rubensimon1.erp_lite.entity.TipoPago;
import com.rubensimon1.erp_lite.entity.TipoTrabajador;
import com.rubensimon1.erp_lite.repository.AjusteVacacionesRepository;
import com.rubensimon1.erp_lite.repository.AlertaRepository;
import com.rubensimon1.erp_lite.repository.CuentaBancariaRepository;
import com.rubensimon1.erp_lite.repository.DeclaracionPresentadaRepository;
import com.rubensimon1.erp_lite.repository.DepartamentoRepository;
import com.rubensimon1.erp_lite.repository.EmpleadoRepository;
import com.rubensimon1.erp_lite.repository.EmpresaRepository;
import com.rubensimon1.erp_lite.repository.MetaAhorroRepository;
import com.rubensimon1.erp_lite.repository.MetodoPagoRepository;
import com.rubensimon1.erp_lite.repository.MovimientoRepository;
import com.rubensimon1.erp_lite.repository.NominaRepository;
import com.rubensimon1.erp_lite.repository.SolicitudVacacionesRepository;
import com.rubensimon1.erp_lite.service.NominaService;

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
    private final CuentaBancariaRepository cuentaBancariaRepository;
    private final MetaAhorroRepository metaAhorroRepository;
    private final MovimientoRepository movimientoRepository;
    private final AlertaRepository alertaRepository;
    private final AjusteVacacionesRepository ajusteVacacionesRepository;
    private final DeclaracionPresentadaRepository declaracionPresentadaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EmpresaRepository empresaRepository;
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
            CuentaBancariaRepository cuentaBancariaRepository,
            MetaAhorroRepository metaAhorroRepository,
            MovimientoRepository movimientoRepository,
            AlertaRepository alertaRepository,
            AjusteVacacionesRepository ajusteVacacionesRepository,
            DeclaracionPresentadaRepository declaracionPresentadaRepository,
            MetodoPagoRepository metodoPagoRepository,
            EmpresaRepository empresaRepository,
            PasswordEncoder passwordEncoder) {
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
        this.productoRepository = productoRepository;
        this.nominaRepository = nominaRepository;
        this.vacacionesRepository = vacacionesRepository;
        this.cuentaBancariaRepository = cuentaBancariaRepository;
        this.metaAhorroRepository = metaAhorroRepository;
        this.movimientoRepository = movimientoRepository;
        this.alertaRepository = alertaRepository;
        this.ajusteVacacionesRepository = ajusteVacacionesRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.declaracionPresentadaRepository = declaracionPresentadaRepository;
        this.empresaRepository = empresaRepository;
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
            dev.setDni("11223344B");
            dev.setNumeroSeguridadSocial("281234567890");
            dev.setCalle("Calle Mayor");
            dev.setNumero("12");
            dev.setPiso("3ºB");
            dev.setCodigoPostal("28001");
            dev.setCiudad("Madrid");
            dev.setProvincia("Madrid");
            dev.setGrupoSanguineo("0+");
            dev.setAlergias("Ninguna conocida");
            dev.setContactoEmergenciaNombre("Marta Dev");
            dev.setContactoEmergenciaTelefono("600111222");
            dev.setSeguroMedico("Sanitas");

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
                    double deducciones = bruto * NominaService.TASA_DEDUCCION_APROX;
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
             * Cuenta bancaria, meta de ahorro y movimientos de ejemplo (zona
             * financiera personal del empleado).
             */
            CuentaBancaria cuenta = new CuentaBancaria();
            cuenta.setEmpleado(dev);
            cuenta.setAlias("Cuenta principal");
            cuenta.setCategoria(CategoriaCuenta.BANCO);
            cuenta.setTipoCuenta(TipoCuenta.PRINCIPAL);
            cuenta.setIban("ES91 2100 0418 4502 0005 1332");
            cuenta.setBanco("Banco Ejemplo");
            cuenta.setSaldoActual(2450.30);

            CuentaBancaria efectivo = new CuentaBancaria();
            efectivo.setEmpleado(dev);
            efectivo.setAlias("Cartera");
            efectivo.setCategoria(CategoriaCuenta.EFECTIVO);
            efectivo.setTipoCuenta(TipoCuenta.SECUNDARIA);
            efectivo.setSaldoActual(60.0);

            cuentaBancariaRepository.saveAll(List.of(cuenta, efectivo));

            Empresa empresa = new Empresa();
            empresa.setEmpleado(dev);
            empresa.setNombre("Nexa Build S.L.");
            empresa.setCif("B12345678");
            empresa.setDireccion("Calle Innovación 5, Madrid");
            empresa.setTelefono("910000000");
            empresa.setSigueAhi(true);
            empresa.setFechaInicio(LocalDate.now().minusYears(2));
            empresaRepository.save(empresa);

            MetodoPago tarjeta = new MetodoPago();
            tarjeta.setEmpleado(dev);
            tarjeta.setTipo(TipoMetodoPago.TARJETA);
            tarjeta.setAlias("Visa personal");
            tarjeta.setTitular("Ruben Developer");
            tarjeta.setNumero("4242424242424242");
            tarjeta.setFechaCaducidad("09/29");
            tarjeta.setCvv("123");
            tarjeta.setCuentaVinculada(cuenta);
            metodoPagoRepository.save(tarjeta);

            MetodoPago paypal = new MetodoPago();
            paypal.setEmpleado(dev);
            paypal.setTipo(TipoMetodoPago.PAYPAL);
            paypal.setAlias("PayPal compras online");
            paypal.setEmailPaypal("ruben@erplite.com");
            metodoPagoRepository.save(paypal);

            MetaAhorro meta = new MetaAhorro();
            meta.setEmpleado(dev);
            meta.setNombre("Fondo de emergencia");
            meta.setMontoObjetivo(3000.0);
            meta.setMontoActual(950.0);
            meta.setFechaObjetivo(LocalDate.now().plusMonths(8));
            metaAhorroRepository.save(meta);

            movimientoRepository.saveAll(List.of(
                    movimiento(dev, "Nomina mensual", 1600.0, TipoMovimiento.INGRESO, TipoPago.NORMAL, LocalDate.now().minusDays(5)),
                    movimiento(dev, "Bizum de Marta Dev", 25.0, TipoMovimiento.INGRESO, TipoPago.BIZUM, LocalDate.now().minusDays(4)),
                    movimiento(dev, "Compra en Mercadona", 63.40, TipoMovimiento.GASTO, TipoPago.NORMAL, LocalDate.now().minusDays(3)),
                    movimiento(dev, "Factura wifi", 39.90, TipoMovimiento.GASTO, TipoPago.NORMAL, LocalDate.now().minusDays(2)),
                    movimiento(dev, "Bizum a Carlos Freelance", 15.0, TipoMovimiento.GASTO, TipoPago.BIZUM, LocalDate.now().minusDays(1))
            ));

            /*
             * Alertas de renovaciones/caducidades, el diferencial de "portal
             * del ciudadano": cosas que caducan y hay que recordar renovar.
             */
            alertaRepository.saveAll(List.of(
                    alerta(dev, "Renovar DNI", LocalDate.now().plusDays(20), "Cita previa en la comisaria"),
                    alerta(dev, "Renovar carnet de conducir", LocalDate.now().plusMonths(6), null),
                    alerta(dev, "ITV del coche", LocalDate.now().minusDays(3), "Ya vencida, pedir cita")
            ));

            /*
             * Ajuste manual de vacaciones (ej: dia extra por convenio) y una
             * declaracion fiscal ya presentada, para que el desglose y el
             * historial no se vean vacios en la primera carga.
             */
            AjusteVacaciones ajuste = new AjusteVacaciones();
            ajuste.setEmpleado(dev);
            ajuste.setDias(1);
            ajuste.setConcepto("Dia adicional por convenio");
            ajuste.setFecha(LocalDate.now().minusMonths(1));
            ajusteVacacionesRepository.save(ajuste);

            DeclaracionPresentada declaracion = new DeclaracionPresentada();
            declaracion.setEmpleado(carlos);
            declaracion.setModelo("303");
            declaracion.setPeriodo(LocalDate.now().getYear() + " - T1");
            declaracion.setFechaPresentacion(LocalDate.now().minusMonths(2));
            declaracion.setImporte(320.50);
            declaracion.setNotas("Presentada online");
            declaracionPresentadaRepository.save(declaracion);

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

    private Movimiento movimiento(Empleado empleado, String concepto, double importe, TipoMovimiento tipo, TipoPago medioPago, LocalDate fecha) {
        Movimiento m = new Movimiento();
        m.setEmpleado(empleado);
        m.setConcepto(concepto);
        m.setImporte(importe);
        m.setTipo(tipo);
        m.setMedioPago(medioPago);
        m.setFecha(fecha);
        return m;
    }

    private Alerta alerta(Empleado empleado, String titulo, LocalDate fechaVencimiento, String notas) {
        Alerta a = new Alerta();
        a.setEmpleado(empleado);
        a.setTitulo(titulo);
        a.setFechaVencimiento(fechaVencimiento);
        a.setNotas(notas);
        return a;
    }
}
