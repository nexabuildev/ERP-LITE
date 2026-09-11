import { useCallback, useEffect, useMemo, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import {
  getMisDeclaraciones,
  registrarDeclaracionPresentada,
  getMisDeclaracionesPresentadas,
  eliminarDeclaracionPresentada,
  abrirArchivoDeclaracion,
} from '../../api'
import DocPreview from '../DocPreview'
import { formatDate, hoyISO } from '../../utils/date'

const MESES = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
]

function Declaraciones() {
  const { token } = useOutletContext()
  const [declaraciones, setDeclaraciones] = useState([])
  const [presentadas, setPresentadas] = useState([])
  const [modelo, setModelo] = useState('')
  const [periodo, setPeriodo] = useState('')
  const [fechaPresentacion, setFechaPresentacion] = useState(hoyISO())
  const [importe, setImporte] = useState('')
  const [notas, setNotas] = useState('')
  const [archivo, setArchivo] = useState(null)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const cargar = useCallback(() => {
    getMisDeclaraciones(token).then(setDeclaraciones).catch((err) => setError(err.message))
    getMisDeclaracionesPresentadas(token).then(setPresentadas).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const gruposPorAnio = useMemo(() => {
    const porAnio = new Map()
    for (const p of presentadas) {
      const fecha = new Date(p.fechaPresentacion)
      const anio = fecha.getFullYear()
      if (!porAnio.has(anio)) porAnio.set(anio, [])
      porAnio.get(anio).push({ ...p, _mes: fecha.getMonth() })
    }
    return [...porAnio.entries()]
      .sort((a, b) => b[0] - a[0])
      .map(([anio, items]) => ({
        anio,
        items: items.sort((a, b) => new Date(b.fechaPresentacion) - new Date(a.fechaPresentacion)),
      }))
  }, [presentadas])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const formData = new FormData()
      formData.append('modelo', modelo)
      formData.append('periodo', periodo)
      formData.append('fechaPresentacion', fechaPresentacion)
      if (importe !== '') formData.append('importe', importe)
      if (notas) formData.append('notas', notas)
      if (archivo) formData.append('archivo', archivo)

      await registrarDeclaracionPresentada(token, formData)
      setModelo('')
      setPeriodo('')
      setImporte('')
      setNotas('')
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
      await eliminarDeclaracionPresentada(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  const verArchivo = async (id) => {
    try {
      const url = await abrirArchivoDeclaracion(token, id)
      window.open(url, '_blank')
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
              <p className="muted">Fecha límite: {formatDate(d.fechaLimite)}</p>
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
          <div className="field field-grow">
            <label>PDF de la declaración (opcional)</label>
            <input type="file" accept="application/pdf,image/*" onChange={(e) => setArchivo(e.target.files?.[0] ?? null)} />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Registrar'}
          </button>
        </form>
      </div>

      {presentadas.length === 0 && !error && <p className="empty-state">Todavía no hay declaraciones presentadas registradas.</p>}

      {gruposPorAnio.map(({ anio, items }) => (
        <div key={anio} className="doc-year-group">
          <h2 className="doc-year-heading">Año {anio}</h2>
          <div className="doc-row">
            {items.map((p) => (
              <div key={p.id} className="doc-card">
                <DocPreview
                  tieneArchivo={p.tieneArchivo}
                  archivoTipo={p.archivoTipo}
                  cargarUrl={() => abrirArchivoDeclaracion(token, p.id)}
                  onAbrir={() => verArchivo(p.id)}
                />
                <div className="doc-card-label">
                  <strong>
                    {MESES[p._mes]} · {p.modelo}
                  </strong>
                  <span className="muted small">{p.periodo}</span>
                  {p.importe != null && <span className="muted small">{p.importe.toFixed(2)} €</span>}
                </div>
                <div className="doc-card-actions">
                  {p.tieneArchivo && (
                    <button type="button" className="btn-small" onClick={() => verArchivo(p.id)}>
                      Ver
                    </button>
                  )}
                  <button type="button" className="btn-small btn-danger" onClick={() => borrar(p.id)}>
                    Quitar
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

export default Declaraciones
