import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { crearEmpresa, editarEmpresa, eliminarEmpresa, getMisEmpresas } from '../../api'
import { formatDate, hoyISO } from '../../utils/date'

const VACIO = { nombre: '', cif: '', direccion: '', telefono: '', sigueAhi: true, fechaInicio: hoyISO(), fechaFin: '' }

function Empresas() {
  const { token } = useOutletContext()
  const [empresas, setEmpresas] = useState([])
  const [form, setForm] = useState(VACIO)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const [editandoId, setEditandoId] = useState(null)
  const [editForm, setEditForm] = useState(null)
  const [guardandoEdicion, setGuardandoEdicion] = useState(false)

  const cargar = useCallback(() => {
    getMisEmpresas(token).then(setEmpresas).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await crearEmpresa(token, { ...form, fechaFin: form.sigueAhi ? null : form.fechaFin })
      setForm(VACIO)
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const empezarEdicion = (e) => {
    setEditandoId(e.id)
    setEditForm({
      nombre: e.nombre,
      cif: e.cif || '',
      direccion: e.direccion || '',
      telefono: e.telefono || '',
      sigueAhi: e.sigueAhi,
      fechaInicio: e.fechaInicio,
      fechaFin: e.fechaFin || '',
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
      await editarEmpresa(token, id, { ...editForm, fechaFin: editForm.sigueAhi ? null : editForm.fechaFin })
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
      await eliminarEmpresa(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Historial laboral</span>
        <h1>Empresas</h1>
        <p>Empresas en las que has trabajado, con sus datos fiscales y fechas.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="card">
        <h2>Añadir empresa</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field field-grow">
            <label>Nombre</label>
            <input type="text" value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required />
          </div>
          <div className="field">
            <label>CIF</label>
            <input type="text" value={form.cif} onChange={(e) => setForm({ ...form, cif: e.target.value })} />
          </div>
          <div className="field field-grow">
            <label>Dirección</label>
            <input type="text" value={form.direccion} onChange={(e) => setForm({ ...form, direccion: e.target.value })} />
          </div>
          <div className="field">
            <label>Teléfono</label>
            <input type="text" value={form.telefono} onChange={(e) => setForm({ ...form, telefono: e.target.value })} />
          </div>
          <div className="field">
            <label>Fecha de inicio</label>
            <input type="date" value={form.fechaInicio} onChange={(e) => setForm({ ...form, fechaInicio: e.target.value })} required />
          </div>
          <div className="field">
            <label>¿Sigues ahí?</label>
            <select value={form.sigueAhi ? 'si' : 'no'} onChange={(e) => setForm({ ...form, sigueAhi: e.target.value === 'si' })}>
              <option value="si">Sí</option>
              <option value="no">No</option>
            </select>
          </div>
          {!form.sigueAhi && (
            <div className="field">
              <label>Fecha de fin</label>
              <input type="date" value={form.fechaFin} onChange={(e) => setForm({ ...form, fechaFin: e.target.value })} required />
            </div>
          )}
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Añadir'}
          </button>
        </form>
      </div>

      {empresas.length === 0 && <p className="empty-state">Todavía no has añadido ninguna empresa.</p>}

      {empresas.length > 0 && (
        <div className="report-list">
          {empresas.map((e) =>
            editandoId === e.id ? (
              <div key={e.id} className="card report-card">
                <h2>Editando {e.nombre}</h2>
                <div className="inline-form" style={{ marginTop: 12 }}>
                  <div className="field field-grow">
                    <label>Nombre</label>
                    <input type="text" value={editForm.nombre} onChange={(ev) => setEditForm({ ...editForm, nombre: ev.target.value })} />
                  </div>
                  <div className="field">
                    <label>CIF</label>
                    <input type="text" value={editForm.cif} onChange={(ev) => setEditForm({ ...editForm, cif: ev.target.value })} />
                  </div>
                  <div className="field field-grow">
                    <label>Dirección</label>
                    <input type="text" value={editForm.direccion} onChange={(ev) => setEditForm({ ...editForm, direccion: ev.target.value })} />
                  </div>
                  <div className="field">
                    <label>Teléfono</label>
                    <input type="text" value={editForm.telefono} onChange={(ev) => setEditForm({ ...editForm, telefono: ev.target.value })} />
                  </div>
                  <div className="field">
                    <label>Fecha de inicio</label>
                    <input type="date" value={editForm.fechaInicio} onChange={(ev) => setEditForm({ ...editForm, fechaInicio: ev.target.value })} />
                  </div>
                  <div className="field">
                    <label>¿Sigues ahí?</label>
                    <select
                      value={editForm.sigueAhi ? 'si' : 'no'}
                      onChange={(ev) => setEditForm({ ...editForm, sigueAhi: ev.target.value === 'si' })}
                    >
                      <option value="si">Sí</option>
                      <option value="no">No</option>
                    </select>
                  </div>
                  {!editForm.sigueAhi && (
                    <div className="field">
                      <label>Fecha de fin</label>
                      <input type="date" value={editForm.fechaFin} onChange={(ev) => setEditForm({ ...editForm, fechaFin: ev.target.value })} />
                    </div>
                  )}
                  <button className="btn-small" disabled={guardandoEdicion} onClick={() => guardarEdicion(e.id)}>
                    Guardar
                  </button>
                  <button className="btn-small" onClick={cancelarEdicion}>
                    Cancelar
                  </button>
                </div>
              </div>
            ) : (
              <div key={e.id} className="card report-card">
                <div className="report-card-header">
                  <h2>{e.nombre}</h2>
                  <span className={`badge ${e.sigueAhi ? 'badge-aprobada' : 'badge-normal'}`}>
                    {e.sigueAhi ? 'Actual' : 'Anterior'}
                  </span>
                </div>
                <div className="report-grid">
                  <div>
                    <span className="muted">CIF</span>
                    <strong>{e.cif || '—'}</strong>
                  </div>
                  <div>
                    <span className="muted">Dirección</span>
                    <strong>{e.direccion || '—'}</strong>
                  </div>
                  <div>
                    <span className="muted">Teléfono</span>
                    <strong>{e.telefono || '—'}</strong>
                  </div>
                  <div>
                    <span className="muted">Periodo</span>
                    <strong>
                      {formatDate(e.fechaInicio)} — {e.sigueAhi ? 'actualidad' : formatDate(e.fechaFin)}
                    </strong>
                  </div>
                </div>
                <div className="inline-form" style={{ marginTop: 16 }}>
                  <button className="btn-small" onClick={() => empezarEdicion(e)}>
                    Editar
                  </button>
                  <button className="btn-small btn-danger" onClick={() => borrar(e.id)}>
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

export default Empresas
