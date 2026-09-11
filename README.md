# ZIVIKO (antes ERP Lite)

Un Portal del Empleado y Ciudadano completo, construido para practicar cómo se monta una API con autenticación real y múltiples ecosistemas de entidades relacionadas entre sí (laboral, financiero, administrativo), yendo mucho más allá de un simple CRUD.

Es un monorepo con dos partes independientes: `backend/`, una API REST en Spring Boot que gestiona toda la lógica de negocio con login por JWT, y `frontend/`, una SPA en React que consume esa API. (Además, el ecosistema cuenta con una app móvil nativa en Android construida con KMM en un repositorio aparte).

## Por qué lo hice

Después del ciclo de DAM tenía varios proyectos pequeños de un solo modelo (un CRUD de tareas, un CRUD de notas) y quería dar el siguiente paso: algo que se pareciera a un ecosistema de software real de una empresa. Eso significaba entidades relacionadas entre sí de verdad, autenticación con roles en vez de un login de juguete, y una API documentada en vez de endpoints que solo yo entiendo.

Empecé construyendo un "ERP Lite" como excusa porque obligaba a modelar relaciones reales. Sin embargo, el proyecto evolucionó rápidamente hacia **ZIVIKO**: un centro de mandos personal que no solo gestiona empleados, sino que abarca la vida **laboral** (fichajes, nóminas, vacaciones), **financiera** (cuentas, movimientos, ahorros) y **administrativa** (trámites, alertas). Esta evolución me permitió enfrentarme a retos de arquitectura más complejos y a diseñar una base de datos mucho más rica.

## Qué hace

- Login con JWT: el backend valida el email y la contraseña, devuelve un token firmado con expiración de 24 horas, y ese token viaja en la cabecera `Authorization` en cada petición protegida (sesión sin estado, sin cookies).
- Roles de usuario: cada empleado tiene un rol, `ADMIN` o `USER`, que Spring Security usa para decidir qué puede hacer.
- **Área de Trabajo**: Fichajes de entrada/salida, control de vacaciones (días solicitados y saldo), registro de teletrabajo y archivo histórico de nóminas.
- **Área de Dinero**: Gestión de cuentas bancarias y balances, historial de movimientos de ingresos/gastos y creación de metas de ahorro.
- **Área Administrativa**: Control de declaraciones (renta, impuestos) y un sistema de alertas proactivo sobre vencimientos.
- Documentación de la API generada automáticamente con Swagger/OpenAPI, navegable desde el navegador.
- Manejo de errores centralizado: las excepciones no se escapan como stack traces, se convierten en respuestas JSON consistentes.
- Frontend moderno: Interfaz web construida en React con una estética "Brutalista / Minimalista" muy cuidada, separando la UI de la lógica de negocio.

## Decisiones técnicas

- **Monorepo con backend y frontend separados, en vez de mezclarlo todo en un solo proyecto.** Cada uno tiene su propio gestor de dependencias, su propio ciclo de vida y se puede levantar de forma independiente, que es como suele estar montado en la práctica un proyecto con API propia y cliente web.
- **JWT con Spring Security y roles, en vez de sesiones con cookies.** Quería practicar el patrón que se usa cuando el backend no sabe (ni le interesa) quién lo está consumiendo: en este caso, la API alimenta tanto a este frontend web como a la app móvil Android (KMM).
- **PostgreSQL en Docker para desarrollo, en vez de H2 en memoria.** Empecé con H2 porque arranca sin nada más instalado, pero cambié a Postgres levantado con `docker-compose` antes de terminar, para trabajar contra una base de datos persistente de verdad (y prepararlo para su despliegue en Render). La dependencia de H2 se quedó por si en algún momento quiero volver a un arranque rápido en memoria.
- **Documentar la API con Swagger/OpenAPI en vez de solo una colección de Postman.** La documentación vive junto al código y se regenera sola cuando cambian los controladores, así no hay riesgo de que quede desactualizada.
- **Diseño UI agnóstico a frameworks de componentes.** En el frontend he optado por CSS puro y diseño a medida en lugar de depender de Bootstrap o Material UI, dándole a ZIVIKO una personalidad visual única.

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
