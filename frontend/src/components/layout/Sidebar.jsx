import { NavLink } from 'react-router-dom'
import ThemeToggle from '../ThemeToggle'

const NAV_ITEMS = [
  { to: '/', label: 'Resumen', num: '00', end: true },
  { to: '/alertas', label: 'Alertas', num: '01' },
  { to: '/fichajes', label: 'Fichajes', num: '02' },
  { to: '/vacaciones', label: 'Vacaciones', num: '03' },
  { to: '/nominas', label: 'Nóminas', num: '04' },
  { to: '/registro-retributivo', label: 'Registro retributivo', num: '05' },
  { to: '/declaraciones', label: 'Declaraciones', num: '06' },
  { to: '/teletrabajo', label: 'Teletrabajo', num: '07' },
  { to: '/movimientos', label: 'Movimientos', num: '08' },
  { to: '/ahorros', label: 'Ahorros', num: '09' },
  { to: '/cuentas-bancarias', label: 'Cuentas y efectivo', num: '10' },
  { to: '/metodos-pago', label: 'Métodos de pago', num: '11' },
  { to: '/simulador', label: 'Simulador de sueldo', num: '12' },
  { to: '/perfil', label: 'Perfil', num: '13' },
]

function Sidebar({ perfil, onLogout, alertasPendientes = 0 }) {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="sidebar-mark">LEGAJO</span>
        <span className="sidebar-sub">Portal del ciudadano</span>
      </div>

      <nav className="sidebar-nav">
        {NAV_ITEMS.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.end}
            className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}
          >
            <span className="sidebar-num">{item.num}</span>
            {item.label}
            {item.to === '/alertas' && alertasPendientes > 0 && (
              <span className="sidebar-badge">{alertasPendientes}</span>
            )}
          </NavLink>
        ))}

        {perfil?.role === 'ADMIN' && (
          <NavLink
            to="/admin/vacaciones"
            className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}
          >
            <span className="sidebar-num">14</span>
            Aprobar vacaciones
          </NavLink>
        )}
      </nav>

      <div className="sidebar-footer">
        {perfil && <p className="sidebar-user">{perfil.nombre}</p>}
        <ThemeToggle />
        <button className="btn-outline btn-block" onClick={onLogout}>
          Cerrar sesión
        </button>
      </div>
    </aside>
  )
}

export default Sidebar
