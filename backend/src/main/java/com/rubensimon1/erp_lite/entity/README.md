# 🗄️ Entity - Entidades JPA

Esta carpeta contiene las **entidades JPA** que representan las tablas de la base de datos.

## 📁 Archivos

| Archivo | Tabla SQL | Descripción |
|---------|-----------|-------------|
| `Departamento.java` | `departamentos` | Áreas de la empresa |
| `Empleado.java` | `empleados` | Trabajadores (+ UserDetails para auth) |
| `Producto.java` | `productos` | Inventario |
| `Role.java` | - | Enum de roles (USER, ADMIN) |

---

## 🔗 Diagrama de Relaciones

```
┌──────────────────┐
│   DEPARTAMENTO   │
├──────────────────┤
│ id (PK)          │
│ nombre           │
│ codigo           │
└────────┬─────────┘
         │
         │ 1:N
         ▼
┌──────────────────┐     ┌──────────────────┐
│    EMPLEADO      │     │    PRODUCTO      │
├──────────────────┤     ├──────────────────┤
│ id (PK)          │     │ id (PK)          │
│ nombre           │     │ nombre           │
│ email (UNIQUE)   │     │ descripcion      │
│ password 🔐      │     │ precio           │
│ role 🔐          │     │ stock            │
│ departamento_id  │     │ departamento_id  │
│ (FK)             │     │ (FK)             │
└──────────────────┘     └──────────────────┘
```

---

## 📋 Detalle de Entidades

### `Role.java` (NUEVO)

**Propósito:** Enum que define los permisos de usuario.

```java
public enum Role {
    USER,   // Permisos limitados
    ADMIN   // Permisos totales
}
```

**Uso en BD:** Se guarda como texto (STRING) → `"ADMIN"` o `"USER"`

---

### `Empleado.java` (ACTUALIZADO)

**Propósito:** Representa un empleado Y también un usuario del sistema (implementa `UserDetails`).

**Campos:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK auto-generada |
| nombre | String | Nombre completo |
| email | String | **UNIQUE** - Usado como username para login |
| password | String | 🔐 Hash BCrypt (ej: `$2a$10$...`) |
| role | Role | 🔐 Enum: USER o ADMIN |
| departamento | Departamento | FK → departamento_id |

**Anotaciones clave:**
```java
@Entity
@Builder  // ← Patrón builder para crear objetos
public class Empleado implements UserDetails {  // ← Interfaz de Spring Security
    
    @Column(unique = true)
    private String email;  // ← No puede repetirse
    
    @Column(nullable = false)
    private String password;  // ← Obligatorio
    
    @Enumerated(EnumType.STRING)  // ← Guarda "ADMIN" no el ordinal
    private Role role;
}
```

**Métodos de UserDetails:**
| Método | Retorna | Propósito |
|--------|---------|-----------|
| `getUsername()` | email | Spring usa email para login |
| `getPassword()` | password | Hash BCrypt |
| `getAuthorities()` | List(role) | Permisos del usuario |
| `isAccountNonExpired()` | true | Cuenta no expirada |
| `isAccountNonLocked()` | true | Cuenta no bloqueada |
| `isCredentialsNonExpired()` | true | Credenciales válidas |
| `isEnabled()` | true | Cuenta activa |

---

### `Departamento.java`

**Campos:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK auto-generada |
| nombre | String | Nombre del departamento |
| codigo | String | Código identificador |
| empleados | List\<Empleado\> | Relación 1:N |
| productos | List\<Producto\> | Relación 1:N |

**Anotaciones importantes:**
```java
@OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL)
@JsonIgnore  // ← Evita bucles infinitos en JSON
private List<Empleado> empleados;
```

---

### `Producto.java`

**Campos:**
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | Long | PK auto-generada |
| nombre | String | Nombre del producto |
| descripcion | String | Descripción detallada |
| precio | Double | Precio unitario |
| stock | Integer | Cantidad disponible |
| departamento | Departamento | FK → departamento_id |

---

## 🛠️ Tecnologías Usadas

- **Jakarta Persistence API (JPA)** → ORM
- **Lombok** → `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- **Jackson** → `@JsonIgnore` para evitar recursión
- **Spring Security** → `UserDetails` para autenticación

---

📍 **Ubicación:** `src/main/java/com/rubensimon1/erp_lite/entity/`
