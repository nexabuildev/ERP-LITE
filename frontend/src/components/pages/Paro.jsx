import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { crearParo, editarParo, eliminarParo, getMisPeriodosParo } from '../../api'
import { formatDate, hoyISO } from '../../utils/date'

const VACIO = { fechaInicio: hoyISO(), fechaFin: '', dineroRecibido: '', notas: '' }

function Paro() {
  const { token } = useOutletContext()
  const [periodos, setPeriodos] = useState([])
  const [form, setForm] = useState(VACIO)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const [editandoId, setEditandoId] = useState(null)
  const [editForm, setEditForm] = useState(null)
  const [guardandoEdicion, setGuardandoEdicion] = useState(false)

  const cargar = useCallback(() => {
    getMisPeriodosParo(token).then(setPeriodos).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await crearParo(token, {
        fechaInicio: form.fechaInicio,
        fechaFin: form.fechaFin || null,
        dineroRecibido: form.dineroRecibido !== '' ? Number(form.dineroRecibido) : null,
        notas: form.notas,
      })
      setForm(VACIO)
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const empezarEdicion = (p) => {
    setEditandoId(p.id)
    setEditForm({
      fechaInicio: p.fechaInicio,
      fechaFin: p.fechaFin || '',
      dineroRecibido: p.dineroRecibido != null ? p.dineroRecibido : '',
      notas: p.notas || '',
    })
  }

  const cancelarEdicion = () => {
    setEditandoId(null)
    setEditForm(null)
  }

  const guardarEdicion = async (id) => {
    setError(null)
    setGuardandoEdicion(true)
    try {
      await editarParo(token, id, {
        fechaInicio: editForm.fechaInicio,
        fechaFin: editForm.fechaFin || null,
        dineroRecibido: editForm.dineroRecibido !== '' ? Number(editForm.dineroRecibido) : null,
        notas: editForm.notas,
      })
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
      await eliminarParo(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Prestación por desempleo</span>
        <h1>Paro</h1>
        <p>Periodos en los que has estado en paro: dinero recibido, cuánto tiempo y cuánto te queda.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="card">
        <h2>Añadir periodo</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field">
            <label>Fecha de inicio</label>
            <input type="date" value={form.fechaInicio} onChange={(e) => setForm({ ...form, fechaInicio: e.target.value })} required />
          </div>
          <div className="field">
            <label>Fecha de fin (opcional si sigue en curso)</label>
            <input type="date" value={form.fechaFin} onChange={(e) => setForm({ ...form, fechaFin: e.target.value })} />
          </div>
          <div className="field">
            <label>Dinero recibido (€)</label>
            <input
              type="number"
              step="0.01"
              value={form.dineroRecibido}
              onChange={(e) => setForm({ ...form, dineroRecibido: e.target.value })}
            />
          </div>
          <div className="field field-grow">
            <label>Notas (opcional)</label>
            <input type="text" value={form.notas} onChange={(e) => setForm({ ...form, notas: e.target.value })} />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Añadir'}
          </button>
        </form>
      </div>

      {periodos.length === 0 && <p className="empty-state">Todavía no has añadido ningún periodo de paro.</p>}

      {periodos.length > 0 && (
        <div className="report-list">
          {periodos.map((p) =>
            editandoId === p.id ? (
              <div key={p.id} className="card report-card">
                <h2>Editando periodo</h2>
                <div className="inline-form" style={{ marginTop: 12 }}>
                  <div className="field">
                    <label>Fecha de inicio</label>
                    <input type="date" value={editForm.fechaInicio} onChange={(e) => setEditForm({ ...editForm, fechaInicio: e.target.value })} />
                  </div>
                  <div className="field">
                    <label>Fecha de fin</label>
                    <input type="date" value={editForm.fechaFin} onChange={(e) => setEditForm({ ...editForm, fechaFin: e.target.value })} />
                  </div>
                  <div className="field">
                    <label>Dinero recibido (€)</label>
                    <input
                      type="number"
                      step="0.01"
                      value={editForm.dineroRecibido}
                      onChange={(e) => setEditForm({ ...editForm, dineroRecibido: e.target.value })}
                    />
                  </div>
                  <div className="field field-grow">
                    <label>Notas</label>
                    <input type="text" value={editForm.notas} onChange={(e) => setEditForm({ ...editForm, notas: e.target.value })} />
                  </div>
                  <button className="btn-small" disabled={guardandoEdicion} onClick={() => guardarEdicion(p.id)}>
                    Guardar
                  </button>
                  <button className="btn-small" onClick={cancelarEdicion}>
                    Cancelar
                  </button>
                </div>
              </div>
            ) : (
              <div key={p.id} className="card report-card">
                <div className="report-card-header">
                  <h2>
                    {formatDate(p.fechaInicio)} — {p.fechaFin ? formatDate(p.fechaFin) : 'en curso'}
                  </h2>
                  <span className={`badge ${p.enCurso ? 'badge-pendiente' : 'badge-normal'}`}>
                    {p.enCurso ? 'En curso' : 'Finalizado'}
                  </span>
                </div>
                <div className="report-grid">
                  <div>
                    <span className="muted">Dinero recibido</span>
                    <strong>{p.dineroRecibido != null ? `${p.dineroRecibido.toFixed(2)} €` : '—'}</strong>
                  </div>
                  <div>
                    <span className="muted">Días transcurridos</span>
                    <strong>{p.diasTranscurridos}</strong>
                  </div>
                  <div>
                    <span className="muted">Días restantes</span>
                    <strong>{p.diasRestantes != null ? p.diasRestantes : '—'}</strong>
                  </div>
                  {p.notas && (
                    <div>
                      <span className="muted">Notas</span>
                      <strong>{p.notas}</strong>
                    </div>
                  )}
                </div>
                <div className="inline-form" style={{ marginTop: 16 }}>
                  <button className="btn-small" onClick={() => empezarEdicion(p)}>
                    Editar
                  </button>
                  <button className="btn-small btn-danger" onClick={() => borrar(p.id)}>
                    Eliminar
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

export default Paro
