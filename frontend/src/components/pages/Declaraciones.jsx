import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import {
  getMisDeclaraciones,
  registrarDeclaracionPresentada,
  getMisDeclaracionesPresentadas,
  eliminarDeclaracionPresentada,
} from '../../api'

const hoyISO = () => new Date().toISOString().slice(0, 10)

function Declaraciones() {
  const { token } = useOutletContext()
  const [declaraciones, setDeclaraciones] = useState([])
  const [presentadas, setPresentadas] = useState([])
  const [modelo, setModelo] = useState('')
  const [periodo, setPeriodo] = useState('')
  const [fechaPresentacion, setFechaPresentacion] = useState(hoyISO())
  const [importe, setImporte] = useState('')
  const [notas, setNotas] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const cargar = useCallback(() => {
    getMisDeclaraciones(token).then(setDeclaraciones).catch((err) => setError(err.message))
    getMisDeclaracionesPresentadas(token).then(setPresentadas).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await registrarDeclaracionPresentada(token, {
        modelo,
        periodo,
        fechaPresentacion,
        importe: importe === '' ? null : Number(importe),
        notas,
      })
      setModelo('')
      setPeriodo('')
      setImporte('')
      setNotas('')
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const borrar = async (id) => {
    setError(null)
    try {
      await eliminarDeclaracionPresentada(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Obligaciones fiscales</span>
        <h1>Declaraciones</h1>
        <p>Próximos vencimientos según tu situación, y tu propio historial de declaraciones presentadas.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="timeline">
        {declaraciones.map((d, i) => (
          <div key={i} className="timeline-item">
            <span className="timeline-days">{d.diasRestantes}d</span>
            <div>
              <h3>{d.nombre}</h3>
              <p className="muted">{d.descripcion}</p>
              <p className="muted">Fecha límite: {d.fechaLimite}</p>
            </div>
          </div>
        ))}
      </div>

      <div className="card" style={{ marginTop: 24 }}>
        <h2>Registrar declaración presentada</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field">
            <label>Modelo</label>
            <input type="text" value={modelo} onChange={(e) => setModelo(e.target.value)} placeholder="130, 303, Renta..." required />
          </div>
          <div className="field">
            <label>Periodo</label>
            <input type="text" value={periodo} onChange={(e) => setPeriodo(e.target.value)} placeholder="2026 - T2" required />
          </div>
          <div className="field">
            <label>Fecha de presentación</label>
            <input type="date" value={fechaPresentacion} onChange={(e) => setFechaPresentacion(e.target.value)} required />
          </div>
          <div className="field">
            <label>Importe (opcional)</label>
            <input type="number" step="0.01" value={importe} onChange={(e) => setImporte(e.target.value)} />
          </div>
          <div className="field field-grow">
            <label>Notas (opcional)</label>
            <input type="text" value={notas} onChange={(e) => setNotas(e.target.value)} />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Registrar'}
          </button>
        </form>
      </div>

      {presentadas.length > 0 && (
        <div className="card">
          <h2>Mis declaraciones presentadas</h2>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Modelo</th>
                  <th>Periodo</th>
                  <th>Fecha</th>
                  <th>Importe</th>
                  <th>Notas</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {presentadas.map((p) => (
                  <tr key={p.id}>
                    <td className="bold">{p.modelo}</td>
                    <td>{p.periodo}</td>
                    <td>{p.fechaPresentacion}</td>
                    <td>{p.importe != null ? `${p.importe.toFixed(2)} €` : '—'}</td>
                    <td className="muted">{p.notas || '—'}</td>
                    <td className="actions">
                      <button className="btn-small btn-danger" onClick={() => borrar(p.id)}>
                        Quitar
                      </button>
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

export default Declaraciones
