import { useEffect, useState } from 'react'
import { NavLink, useLocation } from 'react-router-dom'
import ThemeToggle from '../ThemeToggle'

const NAV_ENTRIES = [
  { type: 'link', to: '/', label: 'Resumen', num: '00', end: true },
  { type: 'link', to: '/alertas', label: 'Alertas', num: '01' },
  {
    type: 'group',
    id: 'trabajo',
    label: 'Trabajo',
    items: [
      { to: '/fichajes', label: 'Fichajes', num: '02' },
      { to: '/vacaciones', label: 'Vacaciones', num: '03' },
      { to: '/nominas', label: 'Nóminas', num: '04' },
      { to: '/registro-retributivo', label: 'Registro retributivo', num: '05' },
      { to: '/declaraciones', label: 'Declaraciones', num: '06' },
      { to: '/teletrabajo', label: 'Teletrabajo', num: '07' },
      { to: '/empresas', label: 'Empresas', num: '08' },
      { to: '/paro', label: 'Paro', num: '09' },
    ],
  },
  {
    type: 'group',
    id: 'dinero',
    label: 'Dinero',
    items: [
      { to: '/movimientos', label: 'Movimientos', num: '10' },
      { to: '/ahorros', label: 'Ahorros', num: '11' },
      { to: '/cuentas-bancarias', label: 'Cuentas y efectivo', num: '12' },
      { to: '/metodos-pago', label: 'Métodos de pago', num: '13' },
      { to: '/simulador', label: 'Simulador de sueldo', num: '14' },
    ],
  },
  { type: 'link', to: '/perfil', label: 'Perfil', num: '15' },
]

function grupoDeRuta(pathname) {
  for (const entry of NAV_ENTRIES) {
    if (entry.type === 'group' && entry.items.some((item) => pathname.startsWith(item.to))) {
      return entry.id
    }
  }
  return null
}

function Sidebar({ perfil, onLogout, alertasPendientes = 0 }) {
  const location = useLocation()
  const [abiertos, setAbiertos] = useState(() => {
    const activo = grupoDeRuta(location.pathname)
    return activo ? new Set([activo]) : new Set()
  })

  useEffect(() => {
    const activo = grupoDeRuta(location.pathname)
    if (activo) {
      setAbiertos((prev) => (prev.has(activo) ? prev : new Set(prev).add(activo)))
    }
  }, [location.pathname])

  const toggleGrupo = (id) => {
    setAbiertos((prev) => {
      const next = new Set(prev)
      if (next.has(id)) next.delete(id)
      else next.add(id)
      return next
    })
  }

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <span className="sidebar-mark">ZIVIKO</span>
        <span className="sidebar-sub">Portal del ciudadano</span>
      </div>

      <nav className="sidebar-nav">
        {NAV_ENTRIES.map((entry) => {
          if (entry.type === 'link') {
            return (
              <NavLink
                key={entry.to}
                to={entry.to}
                end={entry.end}
                className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}
              >
                <span className="sidebar-num">{entry.num}</span>
                {entry.label}
                {entry.to === '/alertas' && alertasPendientes > 0 && (
                  <span className="sidebar-badge">{alertasPendientes}</span>
                )}
              </NavLink>
            )
          }

          const abierto = abiertos.has(entry.id)
          return (
            <div key={entry.id} className="sidebar-group">
              <button
                type="button"
                className="sidebar-group-toggle"
                onClick={() => toggleGrupo(entry.id)}
                aria-expanded={abierto}
              >
                <span>{entry.label}</span>
                <span className={`sidebar-group-chevron${abierto ? ' open' : ''}`}>▾</span>
              </button>
              <div className={`sidebar-group-links${abierto ? '' : ' closed'}`}>
                {entry.items.map((item) => (
                  <NavLink
                    key={item.to}
                    to={item.to}
                    className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}
                  >
                    <span className="sidebar-num">{item.num}</span>
                    {item.label}
                  </NavLink>
                ))}
              </div>
            </div>
          )
        })}

        {perfil?.role === 'ADMIN' && (
          <NavLink
            to="/admin/vacaciones"
            className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}
          >
            <span className="sidebar-num">16</span>
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
