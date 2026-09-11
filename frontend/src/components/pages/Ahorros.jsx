import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { crearMetaAhorro, getMisMetasAhorro, aportarAhorro, eliminarMetaAhorro } from '../../api'

function Ahorros() {
  const { token } = useOutletContext()
  const [metas, setMetas] = useState([])
  const [nombre, setNombre] = useState('')
  const [montoObjetivo, setMontoObjetivo] = useState('')
  const [fechaObjetivo, setFechaObjetivo] = useState('')
  const [aportaciones, setAportaciones] = useState({})
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const cargar = useCallback(() => {
    getMisMetasAhorro(token).then(setMetas).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await crearMetaAhorro(token, { nombre, montoObjetivo: Number(montoObjetivo), fechaObjetivo: fechaObjetivo || null })
      setNombre('')
      setMontoObjetivo('')
      setFechaObjetivo('')
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const aportar = async (id) => {
    const monto = Number(aportaciones[id])
    if (!monto || monto <= 0) return
    setError(null)
    try {
      await aportarAhorro(token, id, monto)
      setAportaciones({ ...aportaciones, [id]: '' })
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  const borrar = async (id) => {
    setError(null)
    try {
      await eliminarMetaAhorro(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Zona de ahorros</span>
        <h1>Tus metas</h1>
        <p>Crea objetivos de ahorro y ve aportando dinero a cada uno.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="card">
        <h2>Nueva meta</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field field-grow">
            <label>Nombre</label>
            <input type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} placeholder="Ej: Fondo de emergencia" required />
          </div>
          <div className="field">
            <label>Objetivo (€)</label>
            <input type="number" min="1" step="0.01" value={montoObjetivo} onChange={(e) => setMontoObjetivo(e.target.value)} required />
          </div>
          <div className="field">
            <label>Fecha objetivo (opcional)</label>
            <input type="date" value={fechaObjetivo} onChange={(e) => setFechaObjetivo(e.target.value)} />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Creando...' : 'Crear meta'}
          </button>
        </form>
      </div>

      {metas.length === 0 && !error && <p className="empty-state">Todavía no tienes metas de ahorro.</p>}

      <div className="report-list">
        {metas.map((m) => (
          <div key={m.id} className="card report-card">
            <div className="report-card-header">
              <h2>{m.nombre}</h2>
              <span className="muted">{m.fechaObjetivo ? `Objetivo: ${m.fechaObjetivo}` : 'Sin fecha límite'}</span>
            </div>

            <div className="progress-bar">
              <div className="progress-fill" style={{ width: `${m.porcentajeCompletado}%` }} />
            </div>
            <p className="muted small" style={{ marginTop: 8 }}>
              {m.montoActual.toFixed(2)} € de {m.montoObjetivo.toFixed(2)} € ({m.porcentajeCompletado}%)
            </p>

            <div className="inline-form" style={{ marginTop: 16 }}>
              <div className="field">
                <label>Aportar (€)</label>
                <input
                  type="number"
                  min="0"
                  step="0.01"
                  value={aportaciones[m.id] || ''}
                  onChange={(e) => setAportaciones({ ...aportaciones, [m.id]: e.target.value })}
                />
              </div>
              <button className="btn-primary" onClick={() => aportar(m.id)}>
                Aportar
              </button>
              <button className="btn-small btn-danger" onClick={() => borrar(m.id)}>
                Eliminar meta
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default Ahorros
