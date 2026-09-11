import { useCallback, useEffect, useMemo, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getMisNominas, subirNomina, eliminarNomina, abrirArchivoNomina } from '../../api'

const MESES = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
]

const ORDENES = {
  fecha_desc: { label: 'Más reciente primero', fn: (a, b) => b.anio - a.anio || b.mes - a.mes },
  fecha_asc: { label: 'Más antigua primero', fn: (a, b) => a.anio - b.anio || a.mes - b.mes },
  neto_desc: { label: 'Neto: mayor a menor', fn: (a, b) => b.salarioNeto - a.salarioNeto },
  neto_asc: { label: 'Neto: menor a mayor', fn: (a, b) => a.salarioNeto - b.salarioNeto },
}

function Nominas() {
  const { token } = useOutletContext()
  const [nominas, setNominas] = useState([])
  const [orden, setOrden] = useState('fecha_desc')
  const [form, setForm] = useState({ mes: 1, anio: new Date().getFullYear(), salarioBruto: '', deducciones: '', salarioNeto: '', fechaPago: '' })
  const [archivo, setArchivo] = useState(null)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const cargar = useCallback(() => {
    getMisNominas(token).then(setNominas).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const nominasOrdenadas = useMemo(() => [...nominas].sort(ORDENES[orden].fn), [nominas, orden])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const formData = new FormData()
      formData.append('mes', form.mes)
      formData.append('anio', form.anio)
      formData.append('salarioBruto', form.salarioBruto)
      formData.append('deducciones', form.deducciones)
      formData.append('salarioNeto', form.salarioNeto)
      formData.append('fechaPago', form.fechaPago)
      if (archivo) formData.append('archivo', archivo)

      await subirNomina(token, formData)
      setForm({ mes: 1, anio: new Date().getFullYear(), salarioBruto: '', deducciones: '', salarioNeto: '', fechaPago: '' })
      setArchivo(null)
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
      await eliminarNomina(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  const verArchivo = async (id) => {
    try {
      const url = await abrirArchivoNomina(token, id)
      window.open(url, '_blank')
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Cobros</span>
        <h1>Nóminas</h1>
        <p>Sube tus nóminas, guarda el archivo y ordénalas como quieras.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="card">
        <h2>Subir nómina</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field">
            <label>Mes</label>
            <select value={form.mes} onChange={(e) => setForm({ ...form, mes: Number(e.target.value) })}>
              {MESES.map((m, i) => (
                <option key={m} value={i + 1}>
                  {m}
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label>Año</label>
            <input type="number" value={form.anio} onChange={(e) => setForm({ ...form, anio: Number(e.target.value) })} />
          </div>
          <div className="field">
            <label>Bruto (€)</label>
            <input type="number" step="0.01" value={form.salarioBruto} onChange={(e) => setForm({ ...form, salarioBruto: e.target.value })} required />
          </div>
          <div className="field">
            <label>Deducciones (€)</label>
            <input type="number" step="0.01" value={form.deducciones} onChange={(e) => setForm({ ...form, deducciones: e.target.value })} required />
          </div>
          <div className="field">
            <label>Neto (€)</label>
            <input type="number" step="0.01" value={form.salarioNeto} onChange={(e) => setForm({ ...form, salarioNeto: e.target.value })} required />
          </div>
          <div className="field">
            <label>Fecha de pago</label>
            <input type="date" value={form.fechaPago} onChange={(e) => setForm({ ...form, fechaPago: e.target.value })} required />
          </div>
          <div className="field field-grow">
            <label>Archivo (PDF/imagen, opcional)</label>
            <input type="file" accept="application/pdf,image/*" onChange={(e) => setArchivo(e.target.files?.[0] ?? null)} />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Subiendo...' : 'Guardar nómina'}
          </button>
        </form>
      </div>

      {nominas.length === 0 && !error && <p className="empty-state">Todavía no hay nóminas registradas.</p>}

      {nominas.length > 0 && (
        <div className="field" style={{ maxWidth: 260, marginBottom: 16 }}>
          <label>Ordenar por</label>
          <select value={orden} onChange={(e) => setOrden(e.target.value)}>
            {Object.entries(ORDENES).map(([key, o]) => (
              <option key={key} value={key}>
                {o.label}
              </option>
            ))}
          </select>
        </div>
      )}

      <div className="report-list">
        {nominasOrdenadas.map((n) => (
          <div key={n.id} className="card report-card">
            <div className="report-card-header">
              <h2>
                {MESES[n.mes - 1]} {n.anio}
              </h2>
              <span className="muted">Pagada el {n.fechaPago}</span>
            </div>
            <div className="report-grid">
              <div>
                <span className="muted">Bruto</span>
                <strong>{n.salarioBruto.toFixed(2)} €</strong>
              </div>
              <div>
                <span className="muted">Deducciones</span>
                <strong>-{n.deducciones.toFixed(2)} €</strong>
              </div>
              <div>
                <span className="muted">Neto</span>
                <strong className="accent">{n.salarioNeto.toFixed(2)} €</strong>
              </div>
            </div>
            <div className="inline-form" style={{ marginTop: 16 }}>
              {n.tieneArchivo && (
                <button type="button" className="btn-small" onClick={() => verArchivo(n.id)}>
                  📎 Ver {n.archivoNombre}
                </button>
              )}
              <button type="button" className="btn-small btn-danger" onClick={() => borrar(n.id)}>
                Eliminar
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default Nominas
