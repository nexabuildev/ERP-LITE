import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import {
  solicitarVacaciones,
  getMisVacaciones,
  getSaldoVacaciones,
  ajustarVacaciones,
  getDesgloseVacaciones,
  editarSolicitudVacaciones,
} from '../../api'
import { formatDate } from '../../utils/date'

function diasEntre(desde, hasta) {
  const inicio = new Date(`${desde}T00:00:00`)
  const fin = new Date(`${hasta}T00:00:00`)
  return Math.round((fin - inicio) / (1000 * 60 * 60 * 24)) + 1
}

function Vacaciones() {
  const { token } = useOutletContext()
  const [saldo, setSaldo] = useState(null)
  const [solicitudes, setSolicitudes] = useState([])
  const [desglose, setDesglose] = useState([])
  const [fechaInicio, setFechaInicio] = useState('')
  const [fechaFin, setFechaFin] = useState('')
  const [motivo, setMotivo] = useState('')
  const [ajusteDias, setAjusteDias] = useState('')
  const [ajusteConcepto, setAjusteConcepto] = useState('')
  const [disfruteInicio, setDisfruteInicio] = useState('')
  const [disfruteFin, setDisfruteFin] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const [ajustando, setAjustando] = useState(false)
  const [marcandoDisfrute, setMarcandoDisfrute] = useState(false)

  const [editandoId, setEditandoId] = useState(null)
  const [editFechaInicio, setEditFechaInicio] = useState('')
  const [editFechaFin, setEditFechaFin] = useState('')
  const [editMotivo, setEditMotivo] = useState('')
  const [editError, setEditError] = useState(null)
  const [editLoading, setEditLoading] = useState(false)

  const cargar = useCallback(() => {
    Promise.all([getSaldoVacaciones(token), getMisVacaciones(token), getDesgloseVacaciones(token)])
      .then(([s, v, d]) => {
        setSaldo(s)
        setSolicitudes(v)
        setDesglose(d)
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

  const empezarEdicion = (s) => {
    setEditandoId(s.id)
    setEditFechaInicio(s.fechaInicio)
    setEditFechaFin(s.fechaFin)
    setEditMotivo(s.motivo || '')
    setEditError(null)
  }

  const cancelarEdicion = () => {
    setEditandoId(null)
    setEditError(null)
  }

  const guardarEdicion = async (id) => {
    setEditError(null)
    setEditLoading(true)
    try {
      await editarSolicitudVacaciones(token, id, {
        fechaInicio: editFechaInicio,
        fechaFin: editFechaFin,
        motivo: editMotivo,
      })
      setEditandoId(null)
      cargar()
    } catch (err) {
      setEditError(err.message)
    } finally {
      setEditLoading(false)
    }
  }

  const handleMarcarDisfrutados = async (e) => {
    e.preventDefault()
    setError(null)
    setMarcandoDisfrute(true)
    try {
      const dias = diasEntre(disfruteInicio, disfruteFin)
      if (dias <= 0) throw new Error('La fecha de fin debe ser posterior o igual a la de inicio')
      await ajustarVacaciones(token, {
        dias: -dias,
        concepto: `Días disfrutados (${formatDate(disfruteInicio)} a ${formatDate(disfruteFin)})`,
      })
      setDisfruteInicio('')
      setDisfruteFin('')
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setMarcandoDisfrute(false)
    }
  }

  const handleAjuste = async (e) => {
    e.preventDefault()
    setError(null)
    setAjustando(true)
    try {
      await ajustarVacaciones(token, { dias: Number(ajusteDias), concepto: ajusteConcepto })
      setAjusteDias('')
      setAjusteConcepto('')
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setAjustando(false)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Ausencias</span>
        <h1>Vacaciones</h1>
        <p>Solicita tus días libres, ajusta tu saldo a mano y consulta el desglose.</p>
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

      <div className="card">
        <h2>Marcar días disfrutados</h2>
        <p className="muted small" style={{ marginTop: -8 }}>
          Elige el rango de fechas que ya has disfrutado y se restan del saldo automáticamente.
        </p>
        <form onSubmit={handleMarcarDisfrutados} className="inline-form">
          <div className="field">
            <label>Desde</label>
            <input type="date" value={disfruteInicio} onChange={(e) => setDisfruteInicio(e.target.value)} required />
          </div>
          <div className="field">
            <label>Hasta</label>
            <input type="date" value={disfruteFin} onChange={(e) => setDisfruteFin(e.target.value)} required />
          </div>
          <button className="btn-primary" disabled={marcandoDisfrute}>
            {marcandoDisfrute ? 'Guardando...' : 'Marcar disfrutados'}
          </button>
        </form>
      </div>

      <div className="card">
        <h2>Ajustar saldo a mano</h2>
        <p className="muted small" style={{ marginTop: -8 }}>
          Suma días extra (ej: convenio, compensación) o resta (ej: permiso sin sueldo).
        </p>
        <form onSubmit={handleAjuste} className="inline-form">
          <div className="field">
            <label>Días (+/-)</label>
            <input type="number" value={ajusteDias} onChange={(e) => setAjusteDias(e.target.value)} placeholder="Ej: 2 o -1" required />
          </div>
          <div className="field field-grow">
            <label>Concepto</label>
            <input
              type="text"
              value={ajusteConcepto}
              onChange={(e) => setAjusteConcepto(e.target.value)}
              placeholder="Ej: día por convenio"
              required
            />
          </div>
          <button className="btn-primary" disabled={ajustando}>
            {ajustando ? 'Guardando...' : 'Ajustar'}
          </button>
        </form>
      </div>

      {solicitudes.length > 0 && (
        <div className="card">
          <h2>Solicitudes</h2>
          {editError && <div className="alert alert-error">⚠️ {editError}</div>}
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Desde</th>
                  <th>Hasta</th>
                  <th>Motivo</th>
                  <th>Estado</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {solicitudes.map((s) =>
                  editandoId === s.id ? (
                    <tr key={s.id}>
                      <td>
                        <input type="date" value={editFechaInicio} onChange={(e) => setEditFechaInicio(e.target.value)} />
                      </td>
                      <td>
                        <input type="date" value={editFechaFin} onChange={(e) => setEditFechaFin(e.target.value)} />
                      </td>
                      <td>
                        <input
                          type="text"
                          value={editMotivo}
                          onChange={(e) => setEditMotivo(e.target.value)}
                          placeholder="Motivo (opcional)"
                        />
                      </td>
                      <td>
                        <span className={`badge badge-${s.estado.toLowerCase()}`}>{s.estado}</span>
                      </td>
                      <td style={{ display: 'flex', gap: 8 }}>
                        <button className="btn-small" onClick={() => guardarEdicion(s.id)} disabled={editLoading}>
                          {editLoading ? 'Guardando...' : 'Guardar'}
                        </button>
                        <button className="btn-small" onClick={cancelarEdicion} disabled={editLoading}>
                          Cancelar
                        </button>
                      </td>
                    </tr>
                  ) : (
                    <tr key={s.id}>
                      <td>{formatDate(s.fechaInicio)}</td>
                      <td>{formatDate(s.fechaFin)}</td>
                      <td className="muted">{s.motivo || '—'}</td>
                      <td>
                        <span className={`badge badge-${s.estado.toLowerCase()}`}>{s.estado}</span>
                      </td>
                      <td>
                        {s.estado === 'PENDIENTE' && (
                          <button className="btn-small" onClick={() => empezarEdicion(s)}>
                            Editar
                          </button>
                        )}
                      </td>
                    </tr>
                  )
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {desglose.length > 0 && (
        <div className="card">
          <h2>Desglose del saldo</h2>
          <div className="ledger">
            {desglose.map((d, i) => (
              <div key={i} className="ledger-row">
                <div className="ledger-info">
                  <span className="ledger-concepto">{d.concepto}</span>
                  <span className="muted small">
                    {formatDate(d.fecha)} · <span className="badge badge-normal">{d.tipo}</span>
                  </span>
                </div>
                <span className={d.dias >= 0 ? 'amount-positive bold' : 'amount-negative bold'}>
                  {d.dias >= 0 ? '+' : ''}
                  {d.dias} días
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default Vacaciones
