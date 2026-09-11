package com.rubensimon1.erp_lite.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.rubensimon1.erp_lite.config.CryptoConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity // <--- Indica que esta clase es una entidad
@Table(name = "empleados") // <--- Indica el nombre de la tabla en la base de datos
@Getter // <--- Genera los getters
@Setter // <--- Genera los setters
@NoArgsConstructor // <--- Genera el constructor por defecto
@AllArgsConstructor // <--- Util para constructores rapidos
@Builder // <--- Util para crear objetos complejos
public class Empleado implements UserDetails {
    /*
     * Atributos
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true)
    private String email;   // El email no puede repetirse, será nuestro "Usuario" para el login

    // -- NUEVOS CAMPOS DE SEGURIDAD -- //
    @Column(nullable = false)
    private String password;    // Aquí guardaremos el HASH (ej: $2a$10$D8...), no "1234"

    @Enumerated(EnumType.STRING)    // Guarda el texto "ADMIN" o "USER" en la BD
    private Role role;

    /*
     * Relacion con departamento
     */
    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    // --- PORTAL DEL EMPLEADO ---

    @Enumerated(EnumType.STRING)
    private TipoTrabajador tipoTrabajador; // ASALARIADO o AUTONOMO

    @Enumerated(EnumType.STRING)
    private Genero genero; // Usado solo para el registro retributivo (RD 902/2020)

    private String categoriaProfesional; // Puesto/categoria, usado para comparar salarios

    private Double salarioBrutoAnual;

    @Convert(converter = CryptoConverter.class)
    private String dni;

    @Convert(converter = CryptoConverter.class)
    private String numeroSeguridadSocial;

    // --- Campos solo relevantes si tipoTrabajador = AUTONOMO ---
    @Convert(converter = CryptoConverter.class)
    private String nif;

    private String epigrafeIae;
    private java.time.LocalDate fechaAltaAutonomo;

    // --- DIRECCIÓN ---
    private String calle;
    private String numero;
    private String piso;
    private String codigoPostal;
    private String ciudad;
    private String provincia;

    // --- EMPRESA (informativo, introducido a mano por el ciudadano) ---
    private String empresaNombre;
    private String empresaCif;
    private String empresaDireccion;
    private String empresaTelefono;

    // --- DATOS MÉDICOS (ficha de emergencia) ---
    @Convert(converter = CryptoConverter.class)
    private String grupoSanguineo;

    @Convert(converter = CryptoConverter.class)
    private String alergias;

    private String contactoEmergenciaNombre;

    @Convert(converter = CryptoConverter.class)
    private String contactoEmergenciaTelefono;

    private String seguroMedico;

    // --- ESTADO DE LA CUENTA ---
    @Builder.Default
    private Boolean activa = true; // false = "eliminada temporalmente" (desactivada, se puede reactivar)

    // --- MÉTODOS DE USER DETAILS (Contrato de Seguridad) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() 
    {
        // Convierte tu Enum ROLE en un permiso que Spring entiende
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername()
    {
        // Usaremos el EMAIL para hacer login, no el nombre
        return email;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    // Estos 4 métodos controlan si la cuenta está bloqueada o expirada.
    // Para el ERP Lite, devolvemos siempre 'true' (todo ok).
    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() 
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() 
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return activa == null || activa;
    }
}
