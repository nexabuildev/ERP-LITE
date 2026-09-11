import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { crearAlerta, editarAlerta, eliminarAlerta, getResumenAlertas } from '../../api'

const hoyISO = () => new Date().toISOString().slice(0, 10)

function Alertas() {
  const { token } = useOutletContext()
  const [resumen, setResumen] = useState(null)
  const [titulo, setTitulo] = useState('')
  const [fechaVencimiento, setFechaVencimiento] = useState(hoyISO())
  const [notas, setNotas] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const [editandoId, setEditandoId] = useState(null)
  const [editForm, setEditForm] = useState(null)
  const [guardandoEdicion, setGuardandoEdicion] = useState(false)

  const cargar = useCallback(() => {
    getResumenAlertas(token).then(setResumen).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await crearAlerta(token, { titulo, fechaVencimiento, notas })
      setTitulo('')
      setNotas('')
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const empezarEdicion = (a) => {
    setEditandoId(a.id)
    setEditForm({ titulo: a.titulo, fechaVencimiento: a.fechaVencimiento, notas: a.notas || '' })
  }

  const cancelarEdicion = () => {
    setEditandoId(null)
    setEditForm(null)
  }

  const guardarEdicion = async (id) => {
    setError(null)
    setGuardandoEdicion(true)
    try {
      await editarAlerta(token, id, editForm)
      cancelarEdicion()
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setGuardandoEdicion(false)
    }
  }

  const borrar = async (id) => {
    setError(null)
    try {
      await eliminarAlerta(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Renovaciones y caducidades</span>
        <h1>Alertas</h1>
        <p>DNI, carnet de conducir, ITV, pasaporte... todo lo que caduca, en un solo sitio.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      {resumen && (resumen.vencidas > 0 || resumen.proximas > 0) && (
        <div className="stats-grid stats-grid-compact">
          <div className="stat-block">
            <span className="stat-block-label">Vencidas</span>
            <span className="stat-block-value amount-negative">{resumen.vencidas}</span>
          </div>
          <div className="stat-block">
            <span className="stat-block-label">Próximas (30 días)</span>
            <span className="stat-block-value">{resumen.proximas}</span>
          </div>
        </div>
      )}

      <div className="card">
        <h2>Nueva alerta</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field field-grow">
            <label>¿Qué hay que renovar?</label>
            <input
              type="text"
              value={titulo}
              onChange={(e) => setTitulo(e.target.value)}
              placeholder="Ej: Renovar DNI, ITV del coche..."
              required
            />
          </div>
          <div className="field">
            <label>Fecha de vencimiento</label>
            <input type="date" value={fechaVencimiento} onChange={(e) => setFechaVencimiento(e.target.value)} required />
          </div>
          <div className="field field-grow">
            <label>Notas (opcional)</label>
            <input type="text" value={notas} onChange={(e) => setNotas(e.target.value)} placeholder="Ej: pedir cita previa" />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Añadir'}
          </button>
        </form>
      </div>

      {resumen?.alertas?.length === 0 && <p className="empty-state">No tienes alertas guardadas todavía.</p>}

      {resumen?.alertas?.length > 0 && (
        <div className="report-list">
          {resumen.alertas.map((a) =>
            editandoId === a.id ? (
              <div key={a.id} className="card report-card">
                <h2>Editando alerta</h2>
                <div className="inline-form" style={{ marginTop: 12 }}>
                  <div className="field field-grow">
                    <label>¿Qué hay que renovar?</label>
                    <input type="text" value={editForm.titulo} onChange={(e) => setEditForm({ ...editForm, titulo: e.target.value })} />
                  </div>
                  <div className="field">
                    <label>Fecha de vencimiento</label>
                    <input
                      type="date"
                      value={editForm.fechaVencimiento}
                      onChange={(e) => setEditForm({ ...editForm, fechaVencimiento: e.target.value })}
                    />
                  </div>
                  <div className="field field-grow">
                    <label>Notas</label>
                    <input type="text" value={editForm.notas} onChange={(e) => setEditForm({ ...editForm, notas: e.target.value })} />
                  </div>
                  <button className="btn-small" disabled={guardandoEdicion} onClick={() => guardarEdicion(a.id)}>
                    Guardar
                  </button>
                  <button className="btn-small" onClick={cancelarEdicion}>
                    Cancelar
                  </button>
                </div>
              </div>
            ) : (
              <div key={a.id} className="card report-card">
                <div className="report-card-header">
                  <h2>{a.titulo}</h2>
                  <span
                    className={`badge badge-${a.estado.toLowerCase() === 'vencida' ? 'rechazada' : a.estado.toLowerCase() === 'proxima' ? 'pendiente' : 'aprobada'}`}
                  >
                    {a.estado === 'VENCIDA'
                      ? `Vencida hace ${Math.abs(a.diasRestantes)} días`
                      : a.estado === 'PROXIMA'
                        ? `En ${a.diasRestantes} días`
                        : 'Vigente'}
                  </span>
                </div>
                <p className="muted">
                  Vence el {a.fechaVencimiento}
                  {a.notas ? ` · ${a.notas}` : ''}
                </p>
                <div className="inline-form" style={{ marginTop: 12 }}>
                  <button className="btn-small" onClick={() => empezarEdicion(a)}>
                    Editar
                  </button>
                  <button className="btn-small btn-danger" onClick={() => borrar(a.id)}>
                    Quitar
                  </button>
                </div>
              </div>
            )
          )}
        </div>
      )}
    </div>
  )
}

export default Alertas
