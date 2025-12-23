# 🎮 Controller - Endpoints REST

Esta carpeta contiene los **controladores REST** que exponen la API.

## 📁 Archivos

| Archivo | Endpoint Base | Descripción |
|---------|---------------|-------------|
| `AuthenticationController.java` | `/api/v1/auth` | 🔐 Login (JWT) |
| `DepartamentoController.java` | `/api/departamentos` | CRUD de departamentos |
| `EmpleadoController.java` | `/api/empleados` | CRUD de empleados |
| `ProductoController.java` | `/api/productos` | CRUD de productos |

---

## 🔐 AuthenticationController.java (NUEVO)

**Propósito:** Manejar el login y devolver el token JWT.

**Endpoint:**
| Método | URL | Función | Acceso |
|--------|-----|---------|--------|
| POST | `/api/v1/auth/login` | Autenticar usuario | 🌐 Público |

**Request Body:**
```json
{
    "email": "ruben@erplite.com",
    "password": "1234"
}
```

**Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "ruben@erplite.com", "password": "1234"}'
```

---

## 🔒 Endpoints Protegidos (Requieren Token)

### `DepartamentoController.java`

**Endpoints:**
| Método | URL | Función |
|--------|-----|---------|
| GET | `/api/departamentos` | Obtener todos |
| POST | `/api/departamentos` | Crear uno nuevo |

**Ejemplo con token:**
```bash
curl http://localhost:8080/api/departamentos \
  -H "Authorization: Bearer <tu_token>"
```

---

### `EmpleadoController.java`

**Endpoints:**
| Método | URL | Función |
|--------|-----|---------|
| GET | `/api/empleados` | Obtener todos |
| POST | `/api/empleados` | Crear empleado |

**Características:**
- ✅ Usa capa Service
- ✅ Validaciones con `@Valid`
- ✅ DTOs de entrada/salida

**Ejemplo de creación:**
```bash
curl -X POST http://localhost:8080/api/empleados \
  -H "Authorization: Bearer <tu_token>" \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Carlos", "email": "carlos@erplite.com", "departamentoId": 1}'
```

---

### `ProductoController.java`

**Endpoints:**
| Método | URL | Función |
|--------|-----|---------|
| GET | `/api/productos` | Obtener todos |
| POST | `/api/productos` | Crear producto |

**Características:**
- ✅ Usa capa Service
- ✅ Validaciones con `@Valid`
- ✅ DTOs de entrada/salida

---

## 🏗️ Patrón Arquitectónico

```
                         🔐 JWT Filter
                              ↓
Cliente → Controller → Service → Repository → Base de Datos
                ↓            ↓
              DTOs        Entities
```

Los controllers:
1. Reciben peticiones HTTP
2. El filtro JWT valida el token (para rutas protegidas)
3. Validan datos de entrada (`@Valid`)
4. Delegan la lógica al Service
5. Devuelven respuestas JSON

---

## 💡 Cómo usar la API con Postman

1. **Obtener token:**
   - POST `http://localhost:8080/api/v1/auth/login`
   - Body: `{"email": "ruben@erplite.com", "password": "1234"}`

2. **Usar token en otras peticiones:**
   - Header: `Authorization: Bearer <token_copiado>`

---

📍 **Ubicación:** `src/main/java/com/rubensimon1/erp_lite/controller/`
