# 📦 DTO - Data Transfer Objects

Esta carpeta contiene los **objetos de transferencia de datos** que definen qué información entra y sale de la API.

## ¿Por qué usamos DTOs?

| Problema | Solución con DTOs |
|----------|------------------|
| Entidades exponen datos sensibles | DTOs solo muestran lo necesario |
| JSON con referencias circulares | DTOs rompen el ciclo |
| Validaciones en la entidad | Validaciones separadas en InputDTO |
| API acoplada a BD | Cambios en BD no afectan la API |

---

## 📁 Archivos

| Archivo | Tipo | Propósito |
|---------|------|-----------|
| `AuthenticationRequest.java` | 🔐 Request | Datos de login |
| `AuthenticationResponse.java` | 🔐 Response | Token JWT |
| `EmpleadoDTO.java` | Response | Datos de empleado |
| `EmpleadoInputDTO.java` | Request | Crear empleado |
| `ProductoDTO.java` | Response | Datos de producto |
| `ProductoInputDTO.java` | Request | Crear producto |

---

## 🔐 DTOs de Autenticación (NUEVOS)

### `AuthenticationRequest.java`

**Propósito:** Recibir credenciales de login.

```java
{
  "email": "ruben@erplite.com",
  "password": "1234"
}
```

**Campos:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| email | String | Email del usuario |
| password | String | Contraseña en texto plano |

---

### `AuthenticationResponse.java`

**Propósito:** Devolver el token JWT al cliente.

```java
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Campos:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| token | String | Token JWT firmado (válido 24h) |

---

## 📤 DTOs de Salida (Response)

Estos se envían al cliente cuando hace GET o recibe respuesta de POST.

### `EmpleadoDTO.java`
```json
{
  "id": 1,
  "nombre": "Ruben Developer",
  "email": "ruben@erplite.com",
  "departamentoId": 1,
  "nombreDepartamento": "Tecnologia"
}
```
> ⚠️ Nota: `password` y `role` NO se exponen en el DTO por seguridad.

### `ProductoDTO.java`
```json
{
  "id": 1,
  "nombre": "PC Gaming",
  "descripcion": "PC de sobremesa",
  "precio": 1100.00,
  "stock": 5,
  "departamentoId": 1,
  "nombreDepartamento": "Tecnologia"
}
```

---

## 📥 DTOs de Entrada (Request)

Estos los envía el cliente para crear recursos nuevos.

### `EmpleadoInputDTO.java`

**Validaciones:**
| Campo | Validación | Mensaje de Error |
|-------|------------|------------------|
| nombre | `@NotBlank` | "El nombre es obligatorio" |
| email | `@NotBlank` + `@Email` | "El formato del email es invalido" |
| departamentoId | `@NotNull` | "Debes especificar un departamento" |

**Ejemplo válido:**
```json
{
  "nombre": "Carlos Dev",
  "email": "carlos@empresa.com",
  "departamentoId": 1
}
```

### `ProductoInputDTO.java`

**Validaciones:**
| Campo | Validación | Mensaje de Error |
|-------|------------|------------------|
| nombre | `@NotBlank` | "El nombre es obligatorio" |
| descripcion | `@NotBlank` | "La descripcion es obligatoria" |
| precio | `@NotNull` | "El precio tiene que ser entre 0 y 999.999" |
| stock | `@NotNull` | "El stock debe estar entre 1 a 999" |
| departamentoId | `@NotNull` | "Debes especificar un departamento" |

---

## 🛠️ Tecnologías Usadas

- **Lombok `@Data`** → Genera getters, setters, toString, equals, hashCode
- **Lombok `@Builder`** → Patrón builder para crear objetos fácilmente
- **Jakarta Validation** → `@NotBlank`, `@NotNull`, `@Email`

---

📍 **Ubicación:** `src/main/java/com/rubensimon1/erp_lite/dto/`
