# 🏢 ERP Lite Backend

Sistema de Gestión Empresarial (ERP) ligero desarrollado con **Spring Boot 3.4.1** y **Java 21**.

## 📊 Estado del Proyecto

| Aspecto | Estado | Observaciones |
|---------|--------|---------------|
| 🏗️ Arquitectura | ✅ Bien estructurada | Patrón MVC + DTO |
| 🔐 Seguridad | ✅ JWT Implementado | Spring Security + Bearer Token |
| 🔧 Base de Datos | ✅ H2 (desarrollo) | En memoria |
| 📖 Documentación API | ✅ Swagger/OpenAPI | Disponible en `/swagger-ui.html` |
| ✅ Validaciones | ✅ Implementadas | Jakarta Validation |
| 🛡️ Manejo de Errores | ✅ GlobalExceptionHandler | Respuestas JSON limpias |
| 🧪 Tests | ⚠️ Pendiente | Estructura creada pero sin tests |

## 🚀 Cómo Ejecutar

```bash
# Compilar
./mvnw clean compile

# Ejecutar
./mvnw spring-boot:run

# Swagger UI
http://localhost:8080/swagger-ui.html

# Consola H2 (Base de datos)
http://localhost:8080/h2-console
```

## 🔐 Autenticación JWT

### 1. Obtener Token

```bash
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
    "email": "ruben@erplite.com",
    "password": "1234"
}
```

**Respuesta:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. Usar Token en Peticiones

```bash
GET http://localhost:8080/api/empleados
Authorization: Bearer <tu_token_aquí>
```

### Usuarios de Prueba

| Email | Password | Rol |
|-------|----------|-----|
| ruben@erplite.com | 1234 | ADMIN |
| ana@erplite.com | 1234 | USER |

---

## 📦 Tecnologías Usadas

- **Spring Boot 3.4.1** - Framework principal
- **Java 21** - Versión de Java
- **Spring Security** - Autenticación y autorización
- **JWT (jjwt)** - Tokens de autenticación
- **H2 Database** - Base de datos en memoria
- **Spring Data JPA** - Persistencia de datos
- **Lombok** - Reducción de boilerplate code
- **Jakarta Validation** - Validación de datos
- **SpringDoc OpenAPI** - Documentación API automática

## 🗂️ Estructura del Proyecto

```
src/main/java/com/rubensimon1/erp_lite/
├── ErpLiteApplication.java    # Punto de entrada
├── config/                    # Configuración + Seguridad JWT
│   ├── ApplicationConfig.java      # Beans de autenticación
│   ├── DataSeeder.java              # Datos iniciales
│   ├── JwtAuthenticationFilter.java # Filtro de token
│   ├── JwtService.java              # Generar/validar tokens
│   └── SecurityConfiguration.java   # Reglas de seguridad
├── controller/                # Endpoints REST
│   ├── AuthenticationController.java # Login
│   ├── DepartamentoController.java
│   ├── EmpleadoController.java
│   └── ProductoController.java
├── dto/                       # Objetos de transferencia
│   ├── AuthenticationRequest.java   # Login request
│   ├── AuthenticationResponse.java  # Token response
│   └── ...
├── entity/                    # Entidades JPA
│   ├── Empleado.java          # Implementa UserDetails
│   ├── Role.java              # Enum USER/ADMIN
│   └── ...
├── exception/                 # Manejo global de errores
├── mapper/                    # Conversión Entity ↔ DTO
├── repository/                # Acceso a datos (JPA)
│   └── EmpleadoRepository.java      # findByEmail() para auth
└── service/                   # Lógica de negocio
    └── AuthenticationService.java   # Login service
```

## 🌐 Endpoints Disponibles

### 🔓 Públicos (sin token)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | Obtener token JWT |
| GET | `/swagger-ui.html` | Documentación API |
| GET | `/h2-console` | Consola de BD |

### 🔒 Protegidos (requieren token)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/departamentos` | Listar departamentos |
| POST | `/api/departamentos` | Crear departamento |
| GET | `/api/empleados` | Listar empleados |
| POST | `/api/empleados` | Crear empleado |
| GET | `/api/productos` | Listar productos |
| POST | `/api/productos` | Crear producto |

## 📋 Datos de Prueba Iniciales

Al arrancar la aplicación, se crean automáticamente:

**Departamentos:**
- 🖥️ Tecnología (IT-001)
- 👥 Recursos Humanos (RRHH-001)

**Empleados:**
- Ruben Developer (ruben@erplite.com) → IT → **ADMIN**
- Ana Recruiter (ana@erplite.com) → RRHH → **USER**

**Productos:**
- Ordenador Portátil (€1,200.50) - Stock: 10
- PC Gaming (€1,100.00) - Stock: 5

## 📂 Documentación por Carpeta

Cada carpeta contiene su propio `README.md` con información detallada:

- [Config](src/main/java/com/rubensimon1/erp_lite/config/README.md) - Configuración y Seguridad JWT
- [Controller](src/main/java/com/rubensimon1/erp_lite/controller/README.md) - Endpoints REST
- [DTO](src/main/java/com/rubensimon1/erp_lite/dto/README.md) - Objetos de transferencia
- [Entity](src/main/java/com/rubensimon1/erp_lite/entity/README.md) - Entidades JPA
- [Exception](src/main/java/com/rubensimon1/erp_lite/exception/README.md) - Manejo de errores
- [Mapper](src/main/java/com/rubensimon1/erp_lite/mapper/README.md) - Conversiones
- [Repository](src/main/java/com/rubensimon1/erp_lite/repository/README.md) - Acceso a datos
- [Service](src/main/java/com/rubensimon1/erp_lite/service/README.md) - Lógica de negocio

---

Desarrollado por **Ruben Simon** 🚀
