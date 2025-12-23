# 🖥️ ERP Lite Frontend

Interfaz de usuario para el sistema ERP Lite desarrollada con **React 19** y **Vite 7**.

## 📊 Estado del Proyecto

| Aspecto | Estado | Observaciones |
|---------|--------|---------------|
| ⚛️ Framework | ✅ React 19 | Última versión |
| ⚡ Bundler | ✅ Vite 7 | Build ultrarrápido |
| 🔐 Autenticación | ✅ JWT | Integración con backend |
| 🎨 Estilos | ✅ CSS-in-JS | Estilos inline |
| 📱 Responsive | ⚠️ Básico | Funcional pero mejorable |
| 🧩 Componentes | ⚠️ Monolítico | Todo en App.jsx |

## 🚀 Cómo Ejecutar

```bash
# Instalar dependencias
npm install

# Ejecutar en desarrollo
npm run dev

# Construir para producción
npm run build

# Previsualizar build
npm run preview
```

**Servidor de desarrollo:** http://localhost:5173

## 🔐 Conexión con Backend

El frontend se conecta al backend en `http://localhost:8080`. Asegúrate de que el backend esté ejecutándose antes de usar la aplicación.

### Flujo de Autenticación

1. Usuario introduce email y contraseña
2. Se hace POST a `/api/v1/auth/login`
3. Se recibe token JWT
4. Token se usa en cabecera `Authorization: Bearer <token>`

### Credenciales de Prueba

| Email | Password | Rol |
|-------|----------|-----|
| ruben@erplite.com | 1234 | ADMIN |
| ana@erplite.com | 1234 | USER |

## 📦 Tecnologías Usadas

- **React 19.2.0** - Biblioteca UI
- **Vite 7.2.4** - Build tool y dev server
- **ESLint 9** - Linting de código

## 🗂️ Estructura del Proyecto

```
frontend/
├── public/              # Archivos estáticos
├── src/
│   ├── assets/          # Recursos (imágenes, iconos)
│   ├── App.jsx          # Componente principal (Login + Dashboard)
│   ├── App.css          # Estilos del componente App
│   ├── index.css        # Estilos globales
│   └── main.jsx         # Punto de entrada
├── index.html           # Template HTML
├── package.json         # Dependencias y scripts
├── vite.config.js       # Configuración de Vite
└── eslint.config.js     # Configuración de ESLint
```

## 🌐 Funcionalidades

### 🔓 Pantalla de Login

- Formulario de autenticación con email y contraseña
- Validación de errores
- Diseño moderno con gradiente

### 🔒 Dashboard (requiere login)

- Navbar con botón de cerrar sesión
- Gestión de empleados
- Tabla dinámica con datos del backend
- Badges de rol (ADMIN/USER)

## 🎨 Diseño

La aplicación usa un esquema de colores profesional:

| Elemento | Color | Uso |
|----------|-------|-----|
| Primary | `#764ba2` | Botones principales, gradientes |
| Secondary | `#667eea` | Gradientes |
| Navbar | `#2c3e50` | Barra superior |
| Success | `#27ae60` | Badges USER |
| Danger | `#c0392b` | Badges ADMIN, errores |
| Info | `#3498db` | Botones secundarios |

## 📋 Mejoras Futuras

- [ ] Separar componentes (Login, Dashboard, Navbar, etc.)
- [ ] Añadir React Router para navegación
- [ ] Implementar gestión de estado global (Context/Redux)
- [ ] Añadir más módulos (Departamentos, Productos)
- [ ] Mejorar responsive design
- [ ] Añadir tests con Vitest

---

Desarrollado por **Ruben Simon** 🚀
