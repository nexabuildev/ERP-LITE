import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { registrarMovimiento, editarMovimiento, eliminarMovimiento, getResumenMovimientos } from '../../api'

const hoyISO = () => new Date().toISOString().slice(0, 10)

function Movimientos() {
  const { token } = useOutletContext()
  const [resumen, setResumen] = useState(null)
  const [concepto, setConcepto] = useState('')
  const [importe, setImporte] = useState('')
  const [tipo, setTipo] = useState('GASTO')
  const [medioPago, setMedioPago] = useState('NORMAL')
  const [fecha, setFecha] = useState(hoyISO())
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const [editandoId, setEditandoId] = useState(null)
  const [editForm, setEditForm] = useState(null)
  const [guardandoEdicion, setGuardandoEdicion] = useState(false)

  const cargar = useCallback(() => {
    getResumenMovimientos(token).then(setResumen).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await registrarMovimiento(token, { concepto, importe: Number(importe), tipo, medioPago, fecha })
      setConcepto('')
      setImporte('')
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const empezarEdicion = (m) => {
    setEditandoId(m.id)
    setEditForm({ concepto: m.concepto, importe: m.importe, tipo: m.tipo, medioPago: m.medioPago, fecha: m.fecha })
  }

  const cancelarEdicion = () => {
    setEditandoId(null)
    setEditForm(null)
  }

  const guardarEdicion = async (id) => {
    setError(null)
    setGuardandoEdicion(true)
    try {
      await editarMovimiento(token, id, { ...editForm, importe: Number(editForm.importe) })
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
      await eliminarMovimiento(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Cuenta personal</span>
        <h1>Movimientos</h1>
        <p>Tu saldo real: ingresos, Bizums y gastos, todo en un mismo extracto.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      {resumen && (
        <div className="stats-grid stats-grid-compact">
          <div className="stat-block">
            <span className="stat-block-label">Saldo</span>
            <span className="stat-block-value">{resumen.saldo.toFixed(2)} €</span>
          </div>
          <div className="stat-block">
            <span className="stat-block-label">Ingresos</span>
            <span className="stat-block-value amount-positive">+{resumen.totalIngresos.toFixed(2)} €</span>
          </div>
          <div className="stat-block">
            <span className="stat-block-label">Gastos</span>
            <span className="stat-block-value amount-negative">-{resumen.totalGastos.toFixed(2)} €</span>
          </div>
        </div>
      )}

      <div className="card">
        <h2>Añadir movimiento</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field field-grow">
            <label>Concepto</label>
            <input
              type="text"
              value={concepto}
              onChange={(e) => setConcepto(e.target.value)}
              placeholder="Ej: Bizum de Marta, compra en Mercadona..."
              required
            />
          </div>
          <div className="field">
            <label>Importe (€)</label>
            <input type="number" min="0" step="0.01" value={importe} onChange={(e) => setImporte(e.target.value)} required />
          </div>
          <div className="field">
            <label>Tipo</label>
            <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
              <option value="GASTO">Gasto</option>
              <option value="INGRESO">Ingreso</option>
            </select>
          </div>
          <div className="field">
            <label>Medio de pago</label>
            <select value={medioPago} onChange={(e) => setMedioPago(e.target.value)}>
              <option value="NORMAL">Normal</option>
              <option value="BIZUM">Bizum</option>
            </select>
          </div>
          <div className="field">
            <label>Fecha</label>
            <input type="date" value={fecha} onChange={(e) => setFecha(e.target.value)} required />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Añadir'}
          </button>
        </form>
      </div>

      {resumen?.movimientos?.length > 0 && (
        <div className="card">
          <h2>Extracto</h2>
          <div className="ledger">
            {resumen.movimientos.map((m) =>
              editandoId === m.id ? (
                <div key={m.id} className="ledger-row" style={{ flexWrap: 'wrap' }}>
                  <div className="inline-form" style={{ width: '100%' }}>
                    <div className="field field-grow">
                      <label>Concepto</label>
                      <input type="text" value={editForm.concepto} onChange={(e) => setEditForm({ ...editForm, concepto: e.target.value })} />
                    </div>
                    <div className="field">
                      <label>Importe (€)</label>
                      <input type="number" min="0" step="0.01" value={editForm.importe} onChange={(e) => setEditForm({ ...editForm, importe: e.target.value })} />
                    </div>
                    <div className="field">
                      <label>Tipo</label>
                      <select value={editForm.tipo} onChange={(e) => setEditForm({ ...editForm, tipo: e.target.value })}>
                        <option value="GASTO">Gasto</option>
                        <option value="INGRESO">Ingreso</option>
                      </select>
                    </div>
                    <div className="field">
                      <label>Medio</label>
                      <select value={editForm.medioPago} onChange={(e) => setEditForm({ ...editForm, medioPago: e.target.value })}>
                        <option value="NORMAL">Normal</option>
                        <option value="BIZUM">Bizum</option>
                      </select>
                    </div>
                    <div className="field">
                      <label>Fecha</label>
                      <input type="date" value={editForm.fecha} onChange={(e) => setEditForm({ ...editForm, fecha: e.target.value })} />
                    </div>
                    <button className="btn-small" disabled={guardandoEdicion} onClick={() => guardarEdicion(m.id)}>
                      Guardar
                    </button>
                    <button className="btn-small" onClick={cancelarEdicion}>
                      Cancelar
                    </button>
                  </div>
                </div>
              ) : (
                <div key={m.id} className="ledger-row">
                  <div className="ledger-info">
                    <span className="ledger-concepto">{m.concepto}</span>
                    <span className="muted small">
                      {m.fecha} · <span className={`badge badge-${m.medioPago.toLowerCase()}`}>{m.medioPago}</span>
                    </span>
                  </div>
                  <span className={m.tipo === 'INGRESO' ? 'amount-positive bold' : 'amount-negative bold'}>
                    {m.tipo === 'INGRESO' ? '+' : '-'}
                    {m.importe.toFixed(2)} €
                  </span>
                  <div className="actions">
                    <button className="btn-small" onClick={() => empezarEdicion(m)}>
                      Editar
                    </button>
                    <button className="btn-small btn-danger" onClick={() => borrar(m.id)}>
                      Quitar
                    </button>
                  </div>
                </div>
              )
            )}
          </div>
        </div>
      )}

      {resumen && resumen.movimientos.length === 0 && (
        <p className="empty-state">Todavía no has registrado movimientos.</p>
      )}
    </div>
  )
}

export default Movimientos
