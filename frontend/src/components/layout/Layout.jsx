import { useCallback, useEffect, useState } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import Sidebar from './Sidebar'
import { getPerfil } from '../../api'

function Layout({ token, onLogout }) {
  const [perfil, setPerfil] = useState(null)
  const [error, setError] = useState(null)
  const location = useLocation()

  const cargarPerfil = useCallback(() => {
    return getPerfil(token)
      .then(setPerfil)
      .catch((err) => setError(err.message))
  }, [token])

  useEffect(() => {
    cargarPerfil()
  }, [cargarPerfil])

  return (
    <div className="portal">
      <Sidebar perfil={perfil} onLogout={onLogout} />
      <main className="portal-content">
        {error && <div className="alert alert-error">⚠️ {error}</div>}
        <div className="page" key={location.pathname}>
          <Outlet context={{ token, perfil, refreshPerfil: cargarPerfil }} />
        </div>
      </main>
    </div>
  )
}

export default Layout
