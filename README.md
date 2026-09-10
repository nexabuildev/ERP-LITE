# ERP Lite

Un ERP simplificado, construido para practicar cómo se monta una API con autenticación real y varias entidades relacionadas entre sí, no otro CRUD de un único modelo.

Es un monorepo con dos partes independientes: `backend/`, una API REST en Spring Boot que gestiona empleados, departamentos y productos con login por JWT, y `frontend/`, una SPA en React que consume esa API.

## Por qué lo hice

Después del ciclo de DAM tenía varios proyectos pequeños de un solo modelo (un CRUD de tareas, un CRUD de notas) y quería dar el siguiente paso: algo que se pareciera, aunque fuera a pequeña escala, a un backend con el que te podrías encontrar en una empresa. Eso significaba entidades relacionadas entre sí de verdad (un empleado pertenece a un departamento, un producto también), autenticación con roles en vez de un login de juguete, y una API documentada en vez de endpoints que solo yo entiendo.

Elegí un ERP como excusa porque obliga a modelar relaciones reales: departamentos con empleados y productos asociados, un sistema de permisos donde no todos los usuarios pueden hacer lo mismo, y una capa de seguridad que protege la API de verdad, no solo la pantalla de login del frontend.

## Qué hace

- Login con JWT: el backend valida el email y la contraseña, devuelve un token firmado con expiración de 24 horas, y ese token viaja en la cabecera `Authorization` en cada petición protegida (sesión sin estado, sin cookies).
- Roles de usuario: cada empleado tiene un rol, `ADMIN` o `USER`, que Spring Security usa para decidir qué puede hacer.
- Gestión de empleados: alta y consulta, cada uno vinculado a un departamento.
- Gestión de departamentos: alta y consulta, con sus empleados y productos asociados.
- Gestión de productos: alta y consulta, con precio, stock y departamento propietario.
- Documentación de la API generada automáticamente con Swagger/OpenAPI, navegable desde el navegador.
- Manejo de errores centralizado: las excepciones no se escapan como stack traces, se convierten en respuestas JSON consistentes.
- Frontend mínimo: pantalla de login y un panel que, una vez autenticado, consulta y muestra la lista de empleados usando el token recibido.

Por ahora el CRUD es de alta y lectura (no hay edición ni borrado todavía); es lo primero que ampliaría si sigo con el proyecto.

## Decisiones técnicas

- **Monorepo con backend y frontend separados, en vez de mezclarlo todo en un solo proyecto.** Cada uno tiene su propio gestor de dependencias, su propio ciclo de vida y se puede levantar de forma independiente, que es como suele estar montado en la práctica un proyecto con API propia y cliente web.
- **JWT con Spring Security y roles, en vez de sesiones con cookies.** Quería practicar el patrón que se usa cuando el backend no sabe (ni le interesa) quién lo está consumiendo: puede ser este frontend, otro cliente o una app móvil. El propio empleado implementa `UserDetails`, así que no hace falta una tabla de usuarios aparte: el modelo de negocio y el modelo de autenticación son la misma entidad.
- **PostgreSQL en Docker para desarrollo, en vez de H2 en memoria.** Empecé con H2 porque arranca sin nada más instalado, pero cambié a Postgres levantado con `docker-compose` antes de terminar, para trabajar contra una base de datos persistente de verdad y no perder los datos cada vez que reinicio la aplicación. La dependencia de H2 se quedó en el `pom.xml` por si en algún momento quiero volver a un arranque sin Docker.
- **Documentar la API con Swagger/OpenAPI en vez de solo una colección de Postman.** La documentación vive junto al código y se regenera sola cuando cambian los controladores, así no hay riesgo de que quede desactualizada.
- **Clave secreta del JWT fija en el código, no en una variable de entorno.** Es la solución rápida para un proyecto de práctica; en un entorno real la sacaría del código y la gestionaría como secreto de despliegue, junto con las credenciales de la base de datos.

## Stack

**Backend**
- Java 21 y Spring Boot 3.4.1
- Spring Security + JWT (jjwt) para autenticación
- Spring Data JPA sobre PostgreSQL (H2 disponible como alternativa en memoria)
- Bean Validation (Jakarta Validation) para los DTOs de entrada
- SpringDoc OpenAPI para la documentación de la API
- Lombok

**Frontend**
- React 19
- Vite 7 como bundler y servidor de desarrollo
- ESLint

## Cómo ejecutarlo en local

**Backend**

```bash
cd backend

# Levanta PostgreSQL en Docker (puerto 5433, credenciales en application.properties)
docker compose up -d

# Compila y arranca la API
./mvnw spring-boot:run
```

La API queda en `http://localhost:8080` y la documentación interactiva en `http://localhost:8080/swagger-ui.html`.

**Frontend**

```bash
cd frontend
npm install
npm run dev
```

La aplicación queda en `http://localhost:5173`. Necesita el backend arrancado para poder hacer login.

Cada carpeta tiene su propio README con más detalle: [backend/README.md](backend/README.md) y [frontend/README.md](frontend/README.md).

## Credenciales de prueba

Al arrancar el backend con la base de datos vacía se crean automáticamente estos usuarios, pensados solo para probar en local:

| Email | Contraseña | Rol |
|-------|------------|-----|
| ruben@erplite.com | 1234 | ADMIN |
| ana@erplite.com | 1234 | USER |

Las contraseñas se guardan cifradas con BCrypt; el `1234` es solo el valor de entrada al hacer login, nunca lo que hay en la base de datos.

---

Ruben Simon · [github.com/nexabuildev](https://github.com/nexabuildev)
