# 🧠 Service - Lógica de Negocio

Esta carpeta contiene los **servicios** donde vive la lógica de negocio de la aplicación.

## ¿Por qué usamos Services?

```
Controller → Service → Repository
    ↓           ↓          ↓
  HTTP      Lógica       SQL
```

| Sin Service | Con Service |
|-------------|-------------|
| Controllers gordos | Controllers delegados |
| Lógica duplicada | Lógica centralizada |
| Difícil de testear | Fácil de testear |
| Acoplamiento | Desacoplamiento |

---

## 📁 Archivos

| Archivo | Entidad | Responsabilidad |
|---------|---------|-----------------|
| `AuthenticationService.java` | 🔐 Auth | Login y generación de token |
| `EmpleadoService.java` | Empleado | Gestión de empleados |
| `ProductoService.java` | Producto | Gestión de productos |

---

## 🔐 AuthenticationService.java (NUEVO)

**Propósito:** Manejar la autenticación de usuarios y generar tokens JWT.

**Método principal:**

```java
public AuthenticationResponse authenticate(AuthenticationRequest request) {
    // 1. Spring Security valida email/password
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    
    // 2. Si llegamos aquí, credenciales OK
    var user = repository.findByEmail(request.getEmail())
            .orElseThrow();
    
    // 3. Generamos el token JWT
    var jwtToken = jwtService.generateToken(user);
    
    // 4. Devolvemos el token
    return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
}
```

**Flujo interno:**
```
Email/Password → AuthenticationManager → ¿BD OK? 
    → Si OK: JwtService.generateToken(user) → Token JWT
    → Si FAIL: Excepción 401 Unauthorized
```

---

## 👥 EmpleadoService.java

**Métodos:**

#### `obtenerTodos() → List<EmpleadoDTO>`
```java
// 1. Obtiene todos los empleados de BD
// 2. Convierte cada uno a DTO usando el Mapper
// 3. Devuelve la lista de DTOs

return empleadoRepository.findAll().stream()
    .map(EmpleadoMapper::toDTO)
    .collect(Collectors.toList());
```

#### `registrarEmpleado(EmpleadoInputDTO) → EmpleadoDTO`
```java
// 1. Convierte InputDTO a Entity
Empleado empleado = EmpleadoMapper.toEntity(inputDto);

// 2. Busca el departamento REAL en BD
Departamento dep = departamentoRepository.findById(id)
    .orElseThrow(() -> new RuntimeException("Departamento no encontrado"));

// 3. Asigna el departamento completo
empleado.setDepartamento(dep);

// 4. Guarda en BD y devuelve DTO
return EmpleadoMapper.toDTO(empleadoRepository.save(empleado));
```

---

## 📦 ProductoService.java

**Métodos:**

#### `obtenerProductos() → List<ProductoDTO>`
Similar a obtenerTodos() de empleados.

#### `registrarProducto(ProductoInputDTO) → ProductoDTO`
Similar a registrarEmpleado pero para productos.

---

## 💡 Patrón Importante

**¿Por qué AuthenticationService usa AuthenticationManager?**

```java
// El AuthenticationManager de Spring:
// 1. Busca al usuario por email (UserDetailsService)
// 2. Compara el password con BCrypt
// 3. Si falla → lanza BadCredentialsException (401)
// 4. Si OK → continúa el flujo

authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(email, password)
);
```

Esto delega toda la lógica de verificación a Spring Security.

---

## 🔮 Mejoras Sugeridas

### Añadir endpoint de registro

```java
// En AuthenticationService
public AuthenticationResponse register(RegisterRequest request) {
    var user = Empleado.builder()
            .nombre(request.getNombre())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
    
    repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    
    return AuthenticationResponse.builder()
            .token(jwtToken)
            .build();
}
```

---

📍 **Ubicación:** `src/main/java/com/rubensimon1/erp_lite/service/`
