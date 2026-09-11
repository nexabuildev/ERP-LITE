import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { solicitarVacaciones, getMisVacaciones, getSaldoVacaciones } from '../../api'

function Vacaciones() {
  const { token } = useOutletContext()
  const [saldo, setSaldo] = useState(null)
  const [solicitudes, setSolicitudes] = useState([])
  const [fechaInicio, setFechaInicio] = useState('')
  const [fechaFin, setFechaFin] = useState('')
  const [motivo, setMotivo] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const cargar = useCallback(() => {
    Promise.all([getSaldoVacaciones(token), getMisVacaciones(token)])
      .then(([s, v]) => {
        setSaldo(s)
        setSolicitudes(v)
      })
      .catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await solicitarVacaciones(token, { fechaInicio, fechaFin, motivo })
      setFechaInicio('')
      setFechaFin('')
      setMotivo('')
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Ausencias</span>
        <h1>Vacaciones</h1>
        <p>Solicita tus días libres y consulta tu saldo anual.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      {saldo && (
        <div className="stats-grid stats-grid-compact">
          <div className="stat-block">
            <span className="stat-block-label">Días disponibles</span>
            <span className="stat-block-value">{saldo.diasRestantes}</span>
          </div>
          <div className="stat-block">
            <span className="stat-block-label">Días aprobados</span>
            <span className="stat-block-value">{saldo.diasAprobados}</span>
          </div>
          <div className="stat-block">
            <span className="stat-block-label">Total anual</span>
            <span className="stat-block-value">{saldo.diasAnuales}</span>
          </div>
        </div>
      )}

      <div className="card">
        <h2>Nueva solicitud</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field">
            <label>Desde</label>
            <input type="date" value={fechaInicio} onChange={(e) => setFechaInicio(e.target.value)} required />
          </div>
          <div className="field">
            <label>Hasta</label>
            <input type="date" value={fechaFin} onChange={(e) => setFechaFin(e.target.value)} required />
          </div>
          <div className="field field-grow">
            <label>Motivo (opcional)</label>
            <input
              type="text"
              value={motivo}
              onChange={(e) => setMotivo(e.target.value)}
              placeholder="Ej: viaje familiar"
            />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Enviando...' : 'Solicitar'}
          </button>
        </form>
      </div>

      {solicitudes.length > 0 && (
        <div className="card">
          <h2>Historial</h2>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Desde</th>
                  <th>Hasta</th>
                  <th>Motivo</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody>
                {solicitudes.map((s) => (
                  <tr key={s.id}>
                    <td>{s.fechaInicio}</td>
                    <td>{s.fechaFin}</td>
                    <td className="muted">{s.motivo || '—'}</td>
                    <td>
                      <span className={`badge badge-${s.estado.toLowerCase()}`}>{s.estado}</span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}

export default Vacaciones
