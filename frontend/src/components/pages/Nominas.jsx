import { useCallback, useEffect, useMemo, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getMisNominas, subirNomina, eliminarNomina, abrirArchivoNomina } from '../../api'
import DocPreview from '../DocPreview'

const MESES = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
]

function Nominas() {
  const { token } = useOutletContext()
  const [nominas, setNominas] = useState([])
  const [form, setForm] = useState({ mes: 1, anio: new Date().getFullYear(), salarioBruto: '', deducciones: '', salarioNeto: '', fechaPago: '' })
  const [archivo, setArchivo] = useState(null)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const cargar = useCallback(() => {
    getMisNominas(token).then(setNominas).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const gruposPorAnio = useMemo(() => {
    const porAnio = new Map()
    for (const n of nominas) {
      if (!porAnio.has(n.anio)) porAnio.set(n.anio, [])
      porAnio.get(n.anio).push(n)
    }
    return [...porAnio.entries()]
      .sort((a, b) => b[0] - a[0])
      .map(([anio, items]) => ({
        anio,
        items: items.sort((a, b) => b.mes - a.mes),
      }))
  }, [nominas])

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
        <p>Sube tus nóminas, guarda el archivo y consúltalas agrupadas por año.</p>
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

      {gruposPorAnio.map(({ anio, items }) => (
        <div key={anio} className="doc-year-group">
          <h2 className="doc-year-heading">Año {anio}</h2>
          <div className="doc-row">
            {items.map((n) => (
              <div key={n.id} className="doc-card">
                <DocPreview
                  tieneArchivo={n.tieneArchivo}
                  archivoTipo={n.archivoTipo}
                  cargarUrl={() => abrirArchivoNomina(token, n.id)}
                  onAbrir={() => verArchivo(n.id)}
                />
                <div className="doc-card-label">
                  <strong>{MESES[n.mes - 1]}</strong>
                  <span className="muted small">Neto {n.salarioNeto.toFixed(2)} €</span>
                </div>
                <div className="doc-card-actions">
                  {n.tieneArchivo && (
                    <button type="button" className="btn-small" onClick={() => verArchivo(n.id)}>
                      Ver
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
      ))}
    </div>
  )
}

export default Nominas
