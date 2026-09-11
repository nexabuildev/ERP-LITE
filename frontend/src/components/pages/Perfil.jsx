import { useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getPerfil, updatePerfil } from '../../api'

function Perfil() {
  const { token, refreshPerfil } = useOutletContext()
  const [perfil, setPerfil] = useState(null)
  const [form, setForm] = useState(null)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    getPerfil(token)
      .then((p) => {
        setPerfil(p)
        setForm({ ...p, tipoTrabajador: p.tipoTrabajador || 'ASALARIADO' })
      })
      .catch((err) => setError(err.message))
  }, [token])

  if (!form) {
    return error ? <div className="alert alert-error">⚠️ {error}</div> : <p className="empty-state">Cargando...</p>
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setSuccess(false)
    setLoading(true)
    try {
      const actualizado = await updatePerfil(token, {
        nombre: form.nombre,
        tipoTrabajador: form.tipoTrabajador,
        genero: form.genero || null,
        nif: form.nif,
        epigrafeIae: form.epigrafeIae,
        fechaAltaAutonomo: form.fechaAltaAutonomo,
      })
      setPerfil(actualizado)
      setForm(actualizado)
      setSuccess(true)
      refreshPerfil?.()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const esAutonomo = form.tipoTrabajador === 'AUTONOMO'

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Expediente Nº {perfil.id}</span>
        <h1>Mi perfil</h1>
        <p>
          {perfil.email} · {perfil.departamentoNombre ?? 'Sin departamento'}
        </p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}
      {success && <div className="alert alert-success">Perfil actualizado correctamente</div>}

      <div className="card">
        <form onSubmit={handleSubmit} className="stacked-form">
          <div className="field">
            <label>Nombre completo</label>
            <input value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required />
          </div>

          <div className="field-row">
            <div className="field">
              <label>Tipo de trabajador</label>
              <select
                value={form.tipoTrabajador || 'ASALARIADO'}
                onChange={(e) => setForm({ ...form, tipoTrabajador: e.target.value })}
              >
                <option value="ASALARIADO">Asalariado</option>
                <option value="AUTONOMO">Autónomo / cuenta propia</option>
              </select>
            </div>
            <div className="field">
              <label>Género (para el registro retributivo)</label>
              <select value={form.genero || ''} onChange={(e) => setForm({ ...form, genero: e.target.value })}>
                <option value="">Prefiero no decirlo</option>
                <option value="HOMBRE">Hombre</option>
                <option value="MUJER">Mujer</option>
                <option value="OTRO">Otro</option>
              </select>
            </div>
          </div>

          {esAutonomo && (
            <div className="field-row">
              <div className="field">
                <label>NIF</label>
                <input value={form.nif || ''} onChange={(e) => setForm({ ...form, nif: e.target.value })} />
              </div>
              <div className="field">
                <label>Epígrafe IAE</label>
                <input value={form.epigrafeIae || ''} onChange={(e) => setForm({ ...form, epigrafeIae: e.target.value })} />
              </div>
              <div className="field">
                <label>Fecha de alta</label>
                <input
                  type="date"
                  value={form.fechaAltaAutonomo || ''}
                  onChange={(e) => setForm({ ...form, fechaAltaAutonomo: e.target.value })}
                />
              </div>
            </div>
          )}

          <div className="report-grid">
            <div>
              <span className="muted">Categoría profesional</span>
              <strong>{perfil.categoriaProfesional ?? '—'}</strong>
            </div>
            <div>
              <span className="muted">Salario bruto anual</span>
              <strong>{perfil.salarioBrutoAnual != null ? `${perfil.salarioBrutoAnual.toLocaleString('es-ES')} €` : '—'}</strong>
            </div>
          </div>
          <p className="muted small">La categoría y el salario los gestiona RRHH y no son editables desde aquí.</p>

          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Guardar cambios'}
          </button>
        </form>
      </div>
    </div>
  )
}

export default Perfil
