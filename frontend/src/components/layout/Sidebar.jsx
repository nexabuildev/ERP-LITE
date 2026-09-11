import { useState } from 'react'
import { NavLink, useLocation } from 'react-router-dom'
import ThemeToggle from '../ThemeToggle'
import { NAV_ENTRIES, grupoDeRuta } from './navEntries'

function Sidebar({ perfil, onLogout, alertasPendientes = 0 }) {
  const location = useLocation()
  const grupoActivo = grupoDeRuta(location.pathname)
  // Solo guardamos los cambios manuales del usuario; el grupo de la página
  // activa se considera abierto por defecto salvo que lo haya cerrado a mano.
  const [overrides, setOverrides] = useState(() => new Map())

  const estaAbierto = (id) => {
    if (overrides.has(id)) return overrides.get(id)
    return id === grupoActivo
  }

  const toggleGrupo = (id) => {
    setOverrides((prev) => {
      const next = new Map(prev)
      next.set(id, !estaAbierto(id))
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

          const abierto = estaAbierto(entry.id)
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
