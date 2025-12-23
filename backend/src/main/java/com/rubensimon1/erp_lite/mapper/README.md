# 🔄 Mapper - Conversión Entity ↔ DTO

Esta carpeta contiene los **mappers** que convierten entre entidades JPA y DTOs.

## ¿Por qué usamos Mappers?

```
Base de Datos ←→ Entity ←→ Mapper ←→ DTO ←→ JSON/API
```

| Entrada (Cliente) | Salida (Cliente) |
|-------------------|------------------|
| InputDTO → Entity | Entity → DTO |

---

## 📁 Archivos

| Archivo | Convierte |
|---------|----------|
| `EmpleadoMapper.java` | Empleado ↔ EmpleadoDTO |
| `ProductoMapper.java` | Producto ↔ ProductoDTO |

---

## 📋 Detalle de Mappers

### `EmpleadoMapper.java`

**Métodos:**

#### `toDTO(Empleado) → EmpleadoDTO`
Convierte una entidad de BD a respuesta JSON.

```java
// Entrada (desde BD)
Empleado {
  id: 1,
  nombre: "Ruben",
  email: "ruben@erplite.com",
  departamento: Departamento { id: 1, nombre: "IT", ... }
}

// Salida (hacia API)
EmpleadoDTO {
  id: 1,
  nombre: "Ruben",
  email: "ruben@erplite.com",
  departamentoId: 1,        // ← Extrae solo el ID
  nombreDepartamento: "IT"  // ← Extrae solo el nombre
}
```

#### `toEntity(EmpleadoInputDTO) → Empleado`
Convierte una petición JSON a entidad para guardar.

```java
// Entrada (desde cliente)
EmpleadoInputDTO {
  nombre: "Carlos",
  email: "carlos@erplite.com",
  departamentoId: 1
}

// Salida (para BD) - Truco JPA
Empleado {
  nombre: "Carlos",
  email: "carlos@erplite.com",
  departamento: Departamento { id: 1 }  // ← Solo el ID, JPA entiende la referencia
}
```

---

### `ProductoMapper.java`

**Métodos:**

#### `toDTO(Producto) → ProductoDTO`
```java
ProductoDTO {
  id, nombre, descripcion, precio, stock,
  departamentoId,        // ← Solo ID
  nombreDepartamento     // ← Solo nombre
}
```

#### `toEntity(ProductoInputDTO) → Producto`
```java
Producto {
  nombre, descripcion, precio, stock,
  departamento: Departamento { id: X }  // ← Referencia por ID
}
```

---

## 💡 Truco JPA Importante

En `toEntity()`, no necesitamos buscar el departamento en la BD:

```java
// ❌ No necesario (consulta extra)
Departamento dep = departamentoRepository.findById(id);
empleado.setDepartamento(dep);

// ✅ Truco JPA (sin consulta)
Departamento dep = new Departamento();
dep.setId(id);  // Solo asignar el ID
empleado.setDepartamento(dep);
// JPA entiende que es una referencia al ID existente
```

> **Nota:** Esto funciona porque solo necesitamos la FK. Si necesitáramos datos del departamento, sí habría que buscarlo.

---

## 🔮 Alternativas

En proyectos más grandes podrías usar:
- **MapStruct** → Genera mappers automáticamente
- **ModelMapper** → Mapeo por reflexión

---

📍 **Ubicación:** `src/main/java/com/rubensimon1/erp_lite/mapper/`
