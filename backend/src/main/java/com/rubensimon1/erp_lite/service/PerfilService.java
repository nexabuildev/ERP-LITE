package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.config.JwtService;
import com.rubensimon1.erp_lite.dto.CambiarCredencialesDTO;
import com.rubensimon1.erp_lite.dto.CredencialesActualizadasDTO;
import com.rubensimon1.erp_lite.dto.EliminarCuentaInputDTO;
import com.rubensimon1.erp_lite.dto.PerfilDTO;
import com.rubensimon1.erp_lite.dto.PerfilUpdateDTO;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.TipoTrabajador;
import com.rubensimon1.erp_lite.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PerfilService {

    private final EmpleadoRepository empleadoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public PerfilDTO miPerfil(Empleado empleado) {
        return toDTO(empleado);
    }

    public PerfilDTO actualizar(Empleado empleado, PerfilUpdateDTO input) {
        empleado.setNombre(input.getNombre());
        empleado.setNombre1(input.getNombre1());
        empleado.setNombre2(input.getNombre2());
        empleado.setApellidos(input.getApellidos());
        // "nombre" (usado en listados de admin, exportaciones, etc.) se mantiene sincronizado
        // con el nombre desglosado cuando este viene informado.
        if (input.getNombre1() != null && !input.getNombre1().isBlank()) {
            String nombreCompleto = Stream.of(input.getNombre1(), input.getNombre2(), input.getApellidos())
                    .filter(parte -> parte != null && !parte.isBlank())
                    .collect(Collectors.joining(" "));
            empleado.setNombre(nombreCompleto);
        }
        empleado.setDni(input.getDni());
        empleado.setNumeroSeguridadSocial(input.getNumeroSeguridadSocial());
        empleado.setTipoTrabajador(input.getTipoTrabajador());
        empleado.setGenero(input.getGenero());
        empleado.setCategoriaProfesional(input.getCategoriaProfesional());
        empleado.setSalarioBrutoAnual(input.getSalarioBrutoAnual());

        if (input.getTipoTrabajador() == TipoTrabajador.AUTONOMO) {
            empleado.setNif(input.getNif());
            empleado.setEpigrafeIae(input.getEpigrafeIae());
            empleado.setFechaAltaAutonomo(input.getFechaAltaAutonomo());
        } else {
            empleado.setNif(null);
            empleado.setEpigrafeIae(null);
            empleado.setFechaAltaAutonomo(null);
        }

        empleado.setCalle(input.getCalle());
        empleado.setNumero(input.getNumero());
        empleado.setPiso(input.getPiso());
        empleado.setCodigoPostal(input.getCodigoPostal());
        empleado.setCiudad(input.getCiudad());
        empleado.setProvincia(input.getProvincia());

        empleado.setCalle2(input.getCalle2());
        empleado.setNumero2(input.getNumero2());
        empleado.setPiso2(input.getPiso2());
        empleado.setCodigoPostal2(input.getCodigoPostal2());
        empleado.setCiudad2(input.getCiudad2());
        empleado.setProvincia2(input.getProvincia2());

        empleado.setGrupoSanguineo(input.getGrupoSanguineo());
        empleado.setAlergias(input.getAlergias());
        empleado.setContactoEmergenciaNombre(input.getContactoEmergenciaNombre());
        empleado.setContactoEmergenciaTelefono(input.getContactoEmergenciaTelefono());
        empleado.setSeguroMedico(input.getSeguroMedico());

        empleadoRepository.save(empleado);
        return toDTO(empleado);
    }

    public CredencialesActualizadasDTO cambiarCredenciales(Empleado empleado, CambiarCredencialesDTO input) {
        if (!passwordEncoder.matches(input.getPasswordActual(), empleado.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La contraseña actual no es correcta");
        }

        if (input.getNuevoEmail() != null && !input.getNuevoEmail().isBlank()
                && !input.getNuevoEmail().equalsIgnoreCase(empleado.getEmail())) {
            empleadoRepository.findByEmail(input.getNuevoEmail()).ifPresent(existente -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese email ya está en uso");
            });
            empleado.setEmail(input.getNuevoEmail());
        }

        if (input.getNuevaPassword() != null && !input.getNuevaPassword().isBlank()) {
            if (input.getNuevaPassword().length() < 4) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La nueva contraseña debe tener al menos 4 caracteres");
            }
            empleado.setPassword(passwordEncoder.encode(input.getNuevaPassword()));
        }

        empleadoRepository.save(empleado);

        // Regeneramos el token: si cambio el email, el JWT anterior (firmado con el email viejo) deja de resolver
        String nuevoToken = jwtService.generateToken(empleado);

        return CredencialesActualizadasDTO.builder()
                .perfil(toDTO(empleado))
                .token(nuevoToken)
                .build();
    }

    private static final String PALABRA_CONFIRMACION = "DELETE-CUENTA";

    public PerfilDTO desactivar(Empleado empleado, EliminarCuentaInputDTO input) {
        validarConfirmacion(empleado, input);
        empleado.setActiva(false);
        empleadoRepository.save(empleado);
        return toDTO(empleado);
    }

    public PerfilDTO reactivarPropia(Empleado empleado) {
        empleado.setActiva(true);
        empleadoRepository.save(empleado);
        return toDTO(empleado);
    }

    public void eliminarDefinitivamente(Empleado empleado, EliminarCuentaInputDTO input) {
        validarConfirmacion(empleado, input);
        empleadoRepository.delete(empleado);
    }

    private void validarConfirmacion(Empleado empleado, EliminarCuentaInputDTO input) {
        if (!PALABRA_CONFIRMACION.equals(input.getConfirmacion())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escribe exactamente \"" + PALABRA_CONFIRMACION + "\" para confirmar");
        }
        if (!passwordEncoder.matches(input.getPasswordActual(), empleado.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La contraseña actual no es correcta");
        }
    }

    private PerfilDTO toDTO(Empleado e) {
        return PerfilDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .nombre1(e.getNombre1())
                .nombre2(e.getNombre2())
                .apellidos(e.getApellidos())
                .email(e.getEmail())
                .role(e.getRole())
                .dni(e.getDni())
                .numeroSeguridadSocial(e.getNumeroSeguridadSocial())
                .departamentoNombre(e.getDepartamento() != null ? e.getDepartamento().getNombre() : null)
                .tipoTrabajador(e.getTipoTrabajador())
                .genero(e.getGenero())
                .categoriaProfesional(e.getCategoriaProfesional())
                .salarioBrutoAnual(e.getSalarioBrutoAnual())
                .nif(e.getNif())
                .epigrafeIae(e.getEpigrafeIae())
                .fechaAltaAutonomo(e.getFechaAltaAutonomo())
                .calle(e.getCalle())
                .numero(e.getNumero())
                .piso(e.getPiso())
                .codigoPostal(e.getCodigoPostal())
                .ciudad(e.getCiudad())
                .provincia(e.getProvincia())
                .calle2(e.getCalle2())
                .numero2(e.getNumero2())
                .piso2(e.getPiso2())
                .codigoPostal2(e.getCodigoPostal2())
                .ciudad2(e.getCiudad2())
                .provincia2(e.getProvincia2())
                .grupoSanguineo(e.getGrupoSanguineo())
                .alergias(e.getAlergias())
                .contactoEmergenciaNombre(e.getContactoEmergenciaNombre())
                .contactoEmergenciaTelefono(e.getContactoEmergenciaTelefono())
                .seguroMedico(e.getSeguroMedico())
                .activa(e.getActiva() == null || e.getActiva())
                .build();
    }
}
