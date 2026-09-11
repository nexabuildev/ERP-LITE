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
        empleado.setDni(input.getDni());
        empleado.setNumeroSeguridadSocial(input.getNumeroSeguridadSocial());
        empleado.setTipoTrabajador(input.getTipoTrabajador());
        empleado.setGenero(input.getGenero());

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

        empleado.setEmpresaNombre(input.getEmpresaNombre());
        empleado.setEmpresaCif(input.getEmpresaCif());
        empleado.setEmpresaDireccion(input.getEmpresaDireccion());
        empleado.setEmpresaTelefono(input.getEmpresaTelefono());

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
                .empresaNombre(e.getEmpresaNombre())
                .empresaCif(e.getEmpresaCif())
                .empresaDireccion(e.getEmpresaDireccion())
                .empresaTelefono(e.getEmpresaTelefono())
                .grupoSanguineo(e.getGrupoSanguineo())
                .alergias(e.getAlergias())
                .contactoEmergenciaNombre(e.getContactoEmergenciaNombre())
                .contactoEmergenciaTelefono(e.getContactoEmergenciaTelefono())
                .seguroMedico(e.getSeguroMedico())
                .activa(e.getActiva() == null || e.getActiva())
                .build();
    }
}
