package com.rubensimon1.erp_lite.service;

import com.rubensimon1.erp_lite.config.JwtService;
import com.rubensimon1.erp_lite.dto.AuthenticationRequest;
import com.rubensimon1.erp_lite.dto.AuthenticationResponse;
import com.rubensimon1.erp_lite.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final EmpleadoRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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