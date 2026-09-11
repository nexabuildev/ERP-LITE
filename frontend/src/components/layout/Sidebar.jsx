import { NavLink } from 'react-router-dom'

const NAV_ITEMS = [
  { to: '/', label: 'Resumen', num: '00', end: true },
  { to: '/fichajes', label: 'Fichajes', num: '01' },
  { to: '/vacaciones', label: 'Vacaciones', num: '02' },
  { to: '/nominas', label: 'Nóminas', num: '03' },
  { to: '/registro-retributivo', label: 'Registro retributivo', num: '04' },
  { to: '/declaraciones', label: 'Declaraciones', num: '05' },
  { to: '/teletrabajo', label: 'Teletrabajo', num: '06' },
  { to: '/perfil', label: 'Perfil', num: '07' },
]

function Sidebar({ perfil, onLogout }) {
  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="sidebar-mark">LEGAJO</span>
        <span className="sidebar-sub">Portal del empleado</span>
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
          </NavLink>
        ))}

        {perfil?.role === 'ADMIN' && (
          <NavLink
            to="/admin/vacaciones"
            className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}
          >
            <span className="sidebar-num">08</span>
            Aprobar vacaciones
          </NavLink>
        )}
      </nav>

      <div className="sidebar-footer">
        {perfil && <p className="sidebar-user">{perfil.nombre}</p>}
        <button className="btn-outline btn-block" onClick={onLogout}>
          Cerrar sesión
        </button>
      </div>
    </aside>
  )
}

export default Sidebar
