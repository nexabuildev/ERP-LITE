# ⚙️ Config - Configuración de la Aplicación

Esta carpeta contiene las **clases de configuración** de Spring Boot y **Seguridad JWT**.

## 📁 Archivos

| Archivo | Propósito |
|---------|-----------|
| `DataSeeder.java` | Inicializa BD con datos de prueba |
| `ApplicationConfig.java` | Beans de autenticación (UserDetailsService, PasswordEncoder) |
| `SecurityConfiguration.java` | Reglas de seguridad HTTP |
| `JwtService.java` | Generar y validar tokens JWT |
| `JwtAuthenticationFilter.java` | Filtro que intercepta cada petición HTTP |

---

## 🔐 Flujo de Autenticación JWT

```
1. POST /api/v1/auth/login → AuthenticationController
2. Valida email/password → AuthenticationService
3. Si OK → JwtService genera token
4. Cliente guarda token
5. Cliente envía: Authorization: Bearer <token>
6. JwtAuthenticationFilter intercepta → valida → permite acceso
```

---

### `DataSeeder.java`

**Propósito:** Inicializar la base de datos con datos de prueba al arrancar.

**Datos que carga:**
| Tipo | Datos | Credenciales |
|------|-------|--------------|
| Departamentos | IT, RRHH | - |
| Empleados | Ruben (ADMIN), Ana (USER) | `1234` (cifrada) |
| Productos | Portátil, PC Gaming | - |

**Código clave:**
```java
@Component
public class DataSeeder implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;  // ← Cifra contraseñas
    
    dev.setPassword(passwordEncoder.encode("1234"));  // → $2a$10$...
    dev.setRole(Role.ADMIN);
}
```

---

### `ApplicationConfig.java`

**Propósito:** Configurar los beans de Spring Security.

**Beans que define:**

| Bean | Función |
|------|---------|
| `UserDetailsService` | Busca usuarios en BD por email |
| `AuthenticationProvider` | Configura cómo validar credenciales |
| `AuthenticationManager` | Gestiona el proceso de login |
| `PasswordEncoder` | BCrypt para cifrar/verificar contraseñas |

**Código clave:**
```java
@Bean
public UserDetailsService userDetailsService() {
    return username -> repository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // ← Cifrado seguro
}
```

---

### `SecurityConfiguration.java`

**Propósito:** Definir qué rutas son públicas y cuáles requieren autenticación.

**Configuración:**

| Ruta | Acceso |
|------|--------|
| `/api/v1/auth/**` | ✅ Público (login) |
| `/swagger-ui/**`, `/v3/api-docs/**` | ✅ Público (documentación) |
| Cualquier otra ruta | 🔒 Requiere token JWT |

**Código clave:**
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/v1/auth/**", "/swagger-ui/**").permitAll()
    .anyRequest().authenticated()
)
.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```

---

### `JwtService.java`

**Propósito:** Gestionar todo lo relacionado con tokens JWT.

**Métodos principales:**

| Método | Función |
|--------|---------|
| `generateToken(UserDetails)` | Crea un nuevo token JWT |
| `extractUsername(token)` | Obtiene el email del token |
| `isTokenValid(token, userDetails)` | Verifica si el token es válido |

**Código clave:**
```java
private static final String SECRET_KEY = "404E635266...";  // ← 256 bits

public String generateToken(UserDetails userDetails) {
    return Jwts.builder()
            .setSubject(userDetails.getUsername())    // Email
            .setIssuedAt(new Date())                  // Fecha creación
            .setExpiration(new Date(... + 24h))       // Expira en 24h
            .signWith(getSignInKey(), HS256)          // Firma digital
            .compact();
}
```

> ⚠️ **IMPORTANTE**: En producción, `SECRET_KEY` debe estar en variables de entorno.

---

### `JwtAuthenticationFilter.java`

**Propósito:** Interceptar cada petición HTTP y validar el token JWT.

**Flujo del filtro:**

```
1. Lee cabecera "Authorization"
2. ¿Empieza con "Bearer "? → Extrae token
3. ¿Token válido? → Marca usuario como autenticado
4. Continúa al siguiente filtro/controller
```

**Código clave:**
```java
@Override
protected void doFilterInternal(request, response, filterChain) {
    final String authHeader = request.getHeader("Authorization");
    
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }
    
    jwt = authHeader.substring(7);  // Quita "Bearer "
    userEmail = jwtService.extractUsername(jwt);
    
    if (jwtService.isTokenValid(jwt, userDetails)) {
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
```

---

📍 **Ubicación:** `src/main/java/com/rubensimon1/erp_lite/config/`
