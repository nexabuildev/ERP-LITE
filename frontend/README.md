# 🏛️ ZIVIKO Web

**Portal del Empleado / Ciudadano** diseñado para centralizar y simplificar la gestión de la vida laboral, financiera y administrativa. ZIVIKO no es un ERP tradicional, es un centro de mandos personal con una estética brutalista y minimalista.

## 📊 Estado del Proyecto

| Aspecto | Estado | Observaciones |
|---------|--------|---------------|
| ⚛️ Framework | ✅ React 19 | Interfaz rápida y reactiva |
| ⚡ Bundler | ✅ Vite 7 | Build ultrarrápido |
| 🔐 Autenticación | ✅ JWT | Autenticación robusta y segura |
| 🎨 Estilos | ✅ CSS Puro | Diseño brutalista/minimalista |
| 📱 Responsive | ✅ Adaptable | Optimizado para web y móvil |
| 🧩 Arquitectura | ✅ Modular | Separado por áreas de negocio |

## 🚀 Cómo Ejecutar

```bash
# Instalar dependencias
npm install

# Ejecutar en desarrollo
npm run dev

# Construir para producción
npm run build
```

**Servidor de desarrollo:** `http://localhost:5173`

## 🔐 Conexión con el Ecosistema

El frontend web se comunica con el backend centralizado alojado en `https://erplite.onrender.com` (o en local vía `http://localhost:8080`). 

### Credenciales de Prueba

| Email | Password | Rol |
|-------|----------|-----|
| ruben@erplite.com | 1234 | ADMIN |
| ana@erplite.com | 1234 | USER |

## 🌟 Funcionalidades Principales

ZIVIKO se divide en cuatro grandes pilares para el usuario:

### 💼 Trabajo
- **Fichajes**: Registro de entradas, salidas y cómputo de horas mensuales/semanales.
- **Vacaciones**: Consulta del saldo anual, días disfrutados, días restantes y estado de las solicitudes.
- **Nóminas**: Archivo histórico de nóminas con desglose de salario bruto, neto, retenciones y Seguridad Social.
- **Teletrabajo**: Registro mensual de días teletrabajados y cálculo de compensación económica.
- **Registro Retributivo**: Análisis de brecha salarial (acceso especial/administrador).

### 💰 Dinero
- **Cuentas Bancarias**: Visión general del patrimonio, IBANs, titulares y saldo por cuenta.
- **Movimientos**: Registro de ingresos y gastos, con balance mensual y gráficos de evolución.
- **Metas de Ahorro**: Creación y seguimiento de objetivos financieros con barras de progreso.

### 🏛️ Trámites y Alertas
- **Declaraciones**: Calendario de obligaciones tributarias (renta, IVA, etc.), vencimientos y registro de presentaciones.
- **Alertas**: Notificaciones automáticas sobre trámites a punto de caducar o vencidos.

## 🎨 Diseño e Identidad Visual

ZIVIKO abandona el diseño corporativo aburrido en favor de un enfoque **Brutalista / Neo-Retro**.
Se caracteriza por fondos color papel, bordes negros gruesos y bloques de color muy saturados y elegantes.

**Paleta de Colores (CSS Variables):**
- `--paper`: `#F4F1EA` (Fondo principal, tono papel pergamino)
- `--ink`: `#14110F` (Texto y bordes gruesos)
- `--accent`: `#7A2E2E` (Rojo Vino - Primario)
- `--ochre`: `#B8860B` (Ocre - Secundario)
- `--forest`: `#2F5233` (Verde Bosque - Éxito/Balances positivos)

## 🗂️ Estructura del Proyecto

```
frontend/
├── public/              # Archivos estáticos y favicons
├── src/
│   ├── api.js           # Capa de red y endpoints (Fetch API)
│   ├── App.jsx          # Enrutador principal y layout
│   ├── index.css        # Sistema de diseño global y variables CSS
│   ├── main.jsx         # Punto de entrada React
│   └── components/      # Componentes modulares
│       ├── Login.jsx            
│       ├── Dashboard.jsx
│       ├── AlertasHub.jsx
│       ├── Perfil.jsx
│       ├── Dinero/      # Submódulos financieros
│       ├── Trabajo/     # Submódulos laborales
│       └── UI/          # Botones, Cards, Loaders reutilizables
```

---

Desarrollado como núcleo de la plataforma **ZIVIKO** 🚀
