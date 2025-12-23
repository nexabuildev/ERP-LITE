# 🏢 ERP Lite Fullstack

Sistema de Gestión Empresarial (ERP) ligero desarrollado como **monorepo** con backend Spring Boot y frontend React.

## 📊 Estado del Proyecto

| Componente | Tecnología | Estado |
|------------|------------|--------|
| 🔧 Backend | Spring Boot 3.4.1 + Java 21 | ✅ Funcional |
| 🖥️ Frontend | React 19 + Vite 7 | ✅ Funcional |
| 🔐 Auth | JWT (JSON Web Tokens) | ✅ Implementado |
| 📖 API Docs | Swagger/OpenAPI | ✅ Disponible |
| 🗄️ Base de Datos | H2 (desarrollo) | ✅ En memoria |

## 🚀 Cómo Ejecutar

### Opción 1: Ejecutar por separado

```bash
# Backend (Terminal 1)
cd backend
./mvnw spring-boot:run

# Frontend (Terminal 2)
cd frontend
npm install
npm run dev
```

### URLs

| Servicio | URL |
|----------|-----|
| 🖥️ Frontend | http://localhost:5173 |
| 🔧 Backend API | http://localhost:8080 |
| 📖 Swagger UI | http://localhost:8080/swagger-ui.html |
| 🗄️ H2 Console | http://localhost:8080/h2-console |

## 🔐 Credenciales de Prueba

| Email | Password | Rol |
|-------|----------|-----|
| ruben@erplite.com | 1234 | ADMIN |
| ana@erplite.com | 1234 | USER |

## 🗂️ Estructura del Proyecto

```
erp-lite-fullstack/
├── backend/                 # Spring Boot API
│   ├── src/main/java/       # Código fuente Java
│   ├── pom.xml              # Dependencias Maven
│   └── README.md            # Documentación backend
│
├── frontend/                # React SPA
│   ├── src/                 # Código fuente React
│   ├── package.json         # Dependencias npm
│   └── README.md            # Documentación frontend
│
├── .gitignore               # Archivos ignorados por Git
└── README.md                # Este archivo
```

## 📦 Tecnologías

### Backend
- **Spring Boot 3.4.1** - Framework
- **Java 21** - Lenguaje
- **Spring Security + JWT** - Autenticación
- **Spring Data JPA** - Persistencia
- **H2 Database** - BD en memoria
- **Lombok** - Reducción de boilerplate
- **SpringDoc OpenAPI** - Documentación API

### Frontend
- **React 19** - Biblioteca UI
- **Vite 7** - Build tool
- **ESLint** - Linting

## 🌐 Funcionalidades

- ✅ Login con JWT
- ✅ Dashboard de gestión
- ✅ CRUD de Empleados
- ✅ CRUD de Departamentos
- ✅ CRUD de Productos
- ✅ Roles (ADMIN/USER)
- ✅ Documentación API automática

## 📋 Documentación Detallada

- [📖 README Backend](backend/README.md)
- [📖 README Frontend](frontend/README.md)

---

Desarrollado por **Ruben Simon** 🚀
