# 🗃️ Repository - Acceso a Datos

Esta carpeta contiene los **repositorios JPA** que proporcionan acceso a la base de datos.

## ¿Qué es un Repository?

Es una interfaz que extiende `JpaRepository` y te da **gratis** operaciones CRUD:

```java
@Repository
public interface MiRepository extends JpaRepository<Entidad, TipoId> {
    // Spring implementa automáticamente los métodos
}
```

---

## 📁 Archivos

| Archivo | Entidad | Tabla |
|---------|---------|-------|
| `DepartamentoRepository.java` | Departamento | departamentos |
| `EmpleadoRepository.java` | Empleado | empleados |
| `ProductoRepository.java` | Producto | productos |

---

## 🎁 Métodos Gratuitos (Heredados)

Al extender `JpaRepository<Entity, Long>` obtienes:

| Método | Descripción |
|--------|-------------|
| `save(entity)` | Guardar/Actualizar |
| `findById(id)` | Buscar por ID |
| `findAll()` | Obtener todos |
| `deleteById(id)` | Eliminar por ID |
| `count()` | Contar registros |
| `existsById(id)` | Verificar existencia |

---

## 🔍 Métodos Personalizados

### `EmpleadoRepository.java` (ACTUALIZADO)

```java
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    // 🔐 NUEVO: Usado por Spring Security para login
    Optional<Empleado> findByEmail(String email);
}
```

**¿Por qué es importante `findByEmail`?**

Este método es **clave** para la autenticación:

```java
// En ApplicationConfig.java (UserDetailsService)
@Bean
public UserDetailsService userDetailsService() {
    return username -> repository.findByEmail(username)  // ← Aquí se usa
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
}

// En AuthenticationService.java
var user = repository.findByEmail(request.getEmail())  // ← Y aquí también
        .orElseThrow();
```

---

### `DepartamentoRepository.java`

```java
@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    // Vacío por ahora - usa métodos heredados
}
```

### `ProductoRepository.java`

```java
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Vacío por ahora - usa métodos heredados
}
```

---

## 📊 Query Methods - Cómo funciona

Spring genera consultas automáticamente basándose en el nombre del método:

| Nombre del Método | SQL Generado |
|-------------------|--------------|
| `findByEmail(email)` | `WHERE email = ?` |
| `findByNombreContaining(str)` | `WHERE nombre LIKE %?%` |
| `findByDepartamentoId(id)` | `WHERE departamento_id = ?` |
| `existsByEmail(email)` | `SELECT EXISTS(... WHERE email = ?)` |

---

## 🔮 Posibles Ampliaciones

```java
// En DepartamentoRepository
Optional<Departamento> findByCodigo(String codigo);
boolean existsByCodigo(String codigo);

// En EmpleadoRepository
List<Empleado> findByDepartamentoId(Long departamentoId);
boolean existsByEmail(String email);  // Para validar registro

// En ProductoRepository
List<Producto> findByDepartamentoId(Long departamentoId);
List<Producto> findByPrecioBetween(Double min, Double max);
List<Producto> findByStockLessThan(Integer cantidad);  // Stock bajo
```

---

📍 **Ubicación:** `src/main/java/com/rubensimon1/erp_lite/repository/`
