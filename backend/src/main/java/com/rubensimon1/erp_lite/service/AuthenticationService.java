package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.config.JwtService;
import com.rubensimon1.erp_lite.dto.AuthenticationRequest;
import com.rubensimon1.erp_lite.dto.AuthenticationResponse;
import com.rubensimon1.erp_lite.dto.RegisterRequest;
import com.rubensimon1.erp_lite.entity.Empleado;
import com.rubensimon1.erp_lite.entity.Role;
import com.rubensimon1.erp_lite.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final EmpleadoRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse register(RegisterRequest request) {
        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese email");
        }

        var empleado = Empleado.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        repository.save(empleado);

        var jwtToken = jwtService.generateToken(empleado);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Spring Security intenta hacer el login (lanza excepción si falla)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        // 2. Si llegamos aquí, usuario y contraseña son correctos. Buscamos al usuario.
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        
        // 3. Generamos el Token
        var jwtToken = jwtService.generateToken(user);
        
        // 4. Devolvemos el Token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}