# ⚠️ Exception - Manejo Global de Errores

Esta carpeta contiene el **manejador global de excepciones** para devolver errores consistentes y amigables.

## 📁 Archivos

### `GlobalExceptionHandler.java`

**Propósito:** Interceptar excepciones y convertirlas en respuestas JSON limpias.

**¿Cómo funciona?**

```
Cliente envía datos inválidos
           ↓
Controller con @Valid detecta error
           ↓
Spring lanza MethodArgumentNotValidException
           ↓
GlobalExceptionHandler intercepta
           ↓
Respuesta JSON limpia con HTTP 400
```

---

## 📋 Excepciones Manejadas

### `MethodArgumentNotValidException`

**¿Cuándo ocurre?** Cuando fallan las validaciones de `@Valid`

**Sin handler (por defecto):**
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "trace": "org.springframework.web.bind..."
  // ← Expone stacktrace (¡Malo!)
}
```

**Con GlobalExceptionHandler:**
```json
{
  "nombre": "El nombre es obligatorio",
  "email": "El formato del email es invalido"
}
```

---

## 🔧 Código

```java
@RestControllerAdvice  // ← Aplica a TODOS los controllers
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(...) {
        Map<String, String> errores = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }
}
```

---

## 🔮 Ampliaciones Sugeridas

Podrías añadir más handlers:

```java
// Para recursos no encontrados
@ExceptionHandler(EntityNotFoundException.class)
public ResponseEntity<...> handleNotFound(...) { ... }

// Para errores de base de datos
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<...> handleDBError(...) { ... }

// Para errores genéricos
@ExceptionHandler(Exception.class)
public ResponseEntity<...> handleGeneric(...) { ... }
```

---

📍 **Ubicación:** `src/main/java/com/rubensimon1/erp_lite/exception/`
