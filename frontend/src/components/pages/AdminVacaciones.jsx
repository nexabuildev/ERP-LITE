import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getVacacionesPendientes, aprobarVacaciones, rechazarVacaciones } from '../../api'

function AdminVacaciones() {
  const { token } = useOutletContext()
  const [pendientes, setPendientes] = useState([])
  const [error, setError] = useState(null)
  const [loadingId, setLoadingId] = useState(null)

  const cargar = useCallback(() => {
    getVacacionesPendientes(token).then(setPendientes).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const resolver = async (id, accion) => {
    setLoadingId(id)
    setError(null)
    try {
      if (accion === 'aprobar') await aprobarVacaciones(token, id)
      else await rechazarVacaciones(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoadingId(null)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Administración</span>
        <h1>Solicitudes de vacaciones</h1>
        <p>Aprueba o rechaza las peticiones pendientes del equipo.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      {pendientes.length === 0 && !error && <p className="empty-state">No hay solicitudes pendientes.</p>}

      {pendientes.length > 0 && (
        <div className="card table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Empleado</th>
                <th>Desde</th>
                <th>Hasta</th>
                <th>Motivo</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {pendientes.map((s) => (
                <tr key={s.id}>
                  <td className="bold">{s.empleadoNombre}</td>
                  <td>{s.fechaInicio}</td>
                  <td>{s.fechaFin}</td>
                  <td className="muted">{s.motivo || '—'}</td>
                  <td className="actions">
                    <button
                      className="btn-small btn-success"
                      disabled={loadingId === s.id}
                      onClick={() => resolver(s.id, 'aprobar')}
                    >
                      Aprobar
                    </button>
                    <button
                      className="btn-small btn-danger"
                      disabled={loadingId === s.id}
                      onClick={() => resolver(s.id, 'rechazar')}
                    >
                      Rechazar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default AdminVacaciones
