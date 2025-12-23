# 📝 APUNTES ERP LITE - PARA ESCRIBIR EN PAPEL

---

## 🏗️ ARQUITECTURA GENERAL (Flujo de datos)

```
CLIENTE (Postman/Frontend)
      ↓
   CONTROLLER  ← Recibe peticiones HTTP (GET, POST...)
      ↓
    SERVICE    ← Lógica de negocio
      ↓
   REPOSITORY  ← Acceso a Base de Datos
      ↓
 BASE DE DATOS (H2)
```

---

## 📁 CARPETAS Y SU FUNCIÓN

### 1. ENTITY (Entidades)
**¿Qué es?** → Las TABLAS de la base de datos en Java

| Clase | Tabla SQL | Campos |
|-------|-----------|--------|
| Departamento | departamentos | id, nombre, codigo |
| Empleado | empleados | id, nombre, email, departamento_id |
| Producto | productos | id, nombre, descripcion, precio, stock, departamento_id |

**Anotaciones clave:**
- `@Entity` → "Esta clase es una tabla"
- `@Id` → "Este campo es la clave primaria"
- `@ManyToOne` → "Muchos de estos → Un departamento"
- `@OneToMany` → "Un departamento → Muchos empleados"

---

### 2. REPOSITORY (Repositorios)
**¿Qué es?** → ACCESO A DATOS (SQL automático)

```
interface EmpleadoRepository extends JpaRepository<Empleado, Long>
```

**Métodos GRATIS (no escribes nada):**
- `findAll()` → SELECT * FROM empleados
- `findById(id)` → SELECT * WHERE id = ?
- `save(objeto)` → INSERT o UPDATE
- `deleteById(id)` → DELETE WHERE id = ?
- `count()` → SELECT COUNT(*)

---

### 3. DTO (Data Transfer Object)
**¿Qué es?** → Lo que ENTRA y SALE de la API

**DTOs de SALIDA (Response):**
```
EmpleadoDTO {
    id, nombre, email,
    departamentoId,        ← Solo el ID
    nombreDepartamento     ← Solo el nombre
}
```

**DTOs de ENTRADA (Request):**
```
EmpleadoInputDTO {
    nombre    → @NotBlank
    email     → @NotBlank @Email
    departamentoId → @NotNull
}
```

---

### 4. MAPPER (Conversores)
**¿Qué es?** → Convierte entre Entity ↔ DTO

```
Entity (BD) ←→ Mapper ←→ DTO (JSON)
```

**Métodos:**
- `toDTO(Entity)` → Convierte para enviar al cliente
- `toEntity(InputDTO)` → Convierte para guardar en BD

---

### 5. SERVICE (Servicios)
**¿Qué es?** → LÓGICA DE NEGOCIO

**Ejemplo EmpleadoService:**
```java
obtenerTodos() {
    1. Buscar todos en BD (Repository)
    2. Convertir cada uno a DTO (Mapper)
    3. Devolver lista
}

registrarEmpleado(InputDTO) {
    1. Convertir DTO a Entity
    2. Buscar departamento real en BD
    3. Asignar departamento al empleado
    4. Guardar en BD
    5. Devolver DTO
}
```

---

### 6. CONTROLLER (Controladores)
**¿Qué es?** → ENDPOINTS de la API

| Anotación | Significado |
|-----------|-------------|
| `@RestController` | "Soy una API REST" |
| `@RequestMapping("/api/x")` | "Mi URL base es /api/x" |
| `@GetMapping` | "Respondo a GET" |
| `@PostMapping` | "Respondo a POST" |
| `@Valid` | "Valida los datos" |

**Endpoints:**
```
GET  /api/departamentos     → Listar todos
POST /api/departamentos     → Crear uno

GET  /api/empleados         → Listar todos  
POST /api/empleados         → Crear uno

GET  /api/productos         → Listar todos
POST /api/productos         → Crear uno
```

---

### 7. EXCEPTION (Excepciones)
**¿Qué es?** → MANEJO DE ERRORES global

```java
@RestControllerAdvice  ← Aplica a TODOS los controllers

@ExceptionHandler(MethodArgumentNotValidException.class)
→ Cuando falla @Valid, devuelve JSON limpio:

{
    "nombre": "El nombre es obligatorio",
    "email": "Formato inválido"
}
```

---

### 8. CONFIG (Configuración)
**¿Qué es?** → Configuración al arrancar

**DataSeeder:**
- Se ejecuta al iniciar Spring
- Si la BD está vacía → Crea datos de prueba
- Crea: 2 departamentos, 2 empleados, 2 productos

---

## 🔗 RELACIONES ENTRE TABLAS

```
DEPARTAMENTO (1) ──────┬──────▶ (N) EMPLEADO
                       │
                       └──────▶ (N) PRODUCTO
```

- 1 Departamento tiene MUCHOS Empleados
- 1 Departamento tiene MUCHOS Productos
- 1 Empleado pertenece a 1 Departamento
- 1 Producto pertenece a 1 Departamento

---

## 📋 RESUMEN RÁPIDO (1 línea por carpeta)

| Carpeta | Una frase |
|---------|-----------|
| **entity** | Las tablas de la BD en Java |
| **repository** | El que habla con la BD |
| **dto** | Lo que entra/sale de la API |
| **mapper** | Convierte Entity ↔ DTO |
| **service** | La lógica del negocio |
| **controller** | Los endpoints HTTP |
| **exception** | Maneja errores bonito |
| **config** | Configuración inicial |

---

## 🔄 FLUJO COMPLETO DE UN POST

```
1. Cliente envía JSON → POST /api/empleados
   {"nombre": "Ana", "email": "ana@x.com", "departamentoId": 1}

2. Controller recibe → @Valid valida el InputDTO

3. Service procesa:
   - Mapper convierte InputDTO → Entity
   - Repository busca Departamento
   - Repository guarda Empleado

4. Mapper convierte Entity → DTO

5. Controller devuelve JSON:
   {"id": 3, "nombre": "Ana", "email": "ana@x.com", 
    "departamentoId": 1, "nombreDepartamento": "IT"}
```

---

## 🔧 ANOTACIONES MÁS IMPORTANTES

| Anotación | ¿Dónde? | ¿Qué hace? |
|-----------|---------|------------|
| `@Entity` | Entity | "Soy tabla de BD" |
| `@Repository` | Repository | "Accedo a datos" |
| `@Service` | Service | "Tengo lógica" |
| `@RestController` | Controller | "Soy API REST" |
| `@Data` | DTO | Genera getters/setters |
| `@NotBlank` | DTO | "Campo obligatorio" |
| `@Valid` | Controller | "Valida esto" |

---

¡Listo para copiar a papel! ✏️
