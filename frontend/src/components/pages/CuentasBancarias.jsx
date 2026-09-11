import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { anadirCuentaBancaria, editarCuentaBancaria, eliminarCuentaBancaria, getResumenCuentas } from '../../api'

const VACIO = { alias: '', categoria: 'BANCO', tipoCuenta: 'PRINCIPAL', iban: '', banco: '', saldoActual: '' }

const ETIQUETAS_CATEGORIA = { BANCO: 'Banco', EFECTIVO: 'Efectivo', PAYPAL: 'PayPal' }

function CuentasBancarias() {
  const { token } = useOutletContext()
  const [resumen, setResumen] = useState(null)
  const [form, setForm] = useState(VACIO)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const [editandoId, setEditandoId] = useState(null)
  const [editForm, setEditForm] = useState(null)
  const [guardandoEdicion, setGuardandoEdicion] = useState(false)

  const cargar = useCallback(() => {
    getResumenCuentas(token).then(setResumen).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await anadirCuentaBancaria(token, { ...form, saldoActual: Number(form.saldoActual) || 0 })
      setForm(VACIO)
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const empezarEdicion = (c) => {
    setEditandoId(c.id)
    setEditForm({
      alias: c.alias,
      categoria: c.categoria,
      tipoCuenta: c.tipoCuenta,
      iban: c.iban || '',
      banco: c.banco || '',
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
      // El saldo no se edita aquí: solo cambia por movimientos, ajustes y aportaciones de ahorro.
      await editarCuentaBancaria(token, id, editForm)
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
      await eliminarCuentaBancaria(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  const esBanco = form.categoria === 'BANCO'

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">¿Dónde tengo mi dinero?</span>
        <h1>Cuentas y efectivo</h1>
        <p>Cuentas bancarias, efectivo en cartera... todo con su saldo, para saber cuánto tienes en total.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      {resumen && (
        <div className="stats-grid stats-grid-compact">
          <div className="stat-block">
            <span className="stat-block-label">Total</span>
            <span className="stat-block-value">{resumen.totalGeneral.toFixed(2)} €</span>
          </div>
          <div className="stat-block">
            <span className="stat-block-label">En bancos</span>
            <span className="stat-block-value">{resumen.totalBanco.toFixed(2)} €</span>
          </div>
          <div className="stat-block">
            <span className="stat-block-label">En efectivo</span>
            <span className="stat-block-value">{resumen.totalEfectivo.toFixed(2)} €</span>
          </div>
          <div className="stat-block">
            <span className="stat-block-label">En PayPal</span>
            <span className="stat-block-value">{resumen.totalPaypal.toFixed(2)} €</span>
          </div>
        </div>
      )}

      <div className="card">
        <h2>Añadir cuenta o efectivo</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field">
            <label>Categoría</label>
            <select value={form.categoria} onChange={(e) => setForm({ ...form, categoria: e.target.value })}>
              <option value="BANCO">Cuenta bancaria</option>
              <option value="EFECTIVO">Efectivo</option>
              <option value="PAYPAL">PayPal</option>
            </select>
          </div>
          <div className="field">
            <label>Tipo</label>
            <select value={form.tipoCuenta} onChange={(e) => setForm({ ...form, tipoCuenta: e.target.value })}>
              <option value="PRINCIPAL">Principal</option>
              <option value="SECUNDARIA">Secundaria</option>
              <option value="OTRA">Otra</option>
            </select>
          </div>
          <div className="field field-grow">
            <label>Alias</label>
            <input type="text" value={form.alias} onChange={(e) => setForm({ ...form, alias: e.target.value })} placeholder="Cuenta nómina" required />
          </div>
          {esBanco && (
            <>
              <div className="field field-grow">
                <label>IBAN</label>
                <input type="text" value={form.iban} onChange={(e) => setForm({ ...form, iban: e.target.value })} placeholder="ES91 2100 0418..." required />
              </div>
              <div className="field">
                <label>Banco</label>
                <input type="text" value={form.banco} onChange={(e) => setForm({ ...form, banco: e.target.value })} placeholder="Opcional" />
              </div>
            </>
          )}
          <div className="field">
            <label>Saldo actual (€)</label>
            <input type="number" step="0.01" value={form.saldoActual} onChange={(e) => setForm({ ...form, saldoActual: e.target.value })} required />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Añadir'}
          </button>
        </form>
      </div>

      {resumen?.cuentas?.length === 0 && <p className="empty-state">Todavía no has añadido ninguna cuenta.</p>}

      {resumen?.cuentas?.length > 0 && (
        <div className="report-list">
          {resumen.cuentas.map((c) =>
            editandoId === c.id ? (
              <div key={c.id} className="card report-card">
                <h2>Editando {c.alias}</h2>
                <div className="inline-form" style={{ marginTop: 12 }}>
                  <div className="field">
                    <label>Categoría</label>
                    <select value={editForm.categoria} onChange={(e) => setEditForm({ ...editForm, categoria: e.target.value })}>
                      <option value="BANCO">Cuenta bancaria</option>
                      <option value="EFECTIVO">Efectivo</option>
                      <option value="PAYPAL">PayPal</option>
                    </select>
                  </div>
                  <div className="field">
                    <label>Tipo</label>
                    <select value={editForm.tipoCuenta} onChange={(e) => setEditForm({ ...editForm, tipoCuenta: e.target.value })}>
                      <option value="PRINCIPAL">Principal</option>
                      <option value="SECUNDARIA">Secundaria</option>
                      <option value="OTRA">Otra</option>
                    </select>
                  </div>
                  <div className="field field-grow">
                    <label>Alias</label>
                    <input type="text" value={editForm.alias} onChange={(e) => setEditForm({ ...editForm, alias: e.target.value })} />
                  </div>
                  {editForm.categoria === 'BANCO' && (
                    <>
                      <div className="field field-grow">
                        <label>IBAN</label>
                        <input type="text" value={editForm.iban} onChange={(e) => setEditForm({ ...editForm, iban: e.target.value })} />
                      </div>
                      <div className="field">
                        <label>Banco</label>
                        <input type="text" value={editForm.banco} onChange={(e) => setEditForm({ ...editForm, banco: e.target.value })} />
                      </div>
                    </>
                  )}
                  <p className="muted small" style={{ width: '100%', margin: 0 }}>
                    Saldo actual: <strong>{c.saldoActual.toFixed(2)} €</strong> — solo cambia con movimientos, ajustes o aportaciones de ahorro.
                  </p>
                  <button className="btn-small" disabled={guardandoEdicion} onClick={() => guardarEdicion(c.id)}>
                    Guardar
                  </button>
                  <button className="btn-small" onClick={cancelarEdicion}>
                    Cancelar
                  </button>
                </div>
              </div>
            ) : (
              <div key={c.id} className="card report-card">
                <div className="report-card-header">
                  <h2>{c.alias}</h2>
                  <div style={{ display: 'flex', gap: 8 }}>
                    <span className="badge badge-normal">{ETIQUETAS_CATEGORIA[c.categoria] || c.categoria}</span>
                    <span className={`badge ${c.tipoCuenta === 'PRINCIPAL' ? 'badge-aprobada' : 'badge-pendiente'}`}>{c.tipoCuenta}</span>
                  </div>
                </div>
                <div className="report-grid">
                  {c.categoria === 'BANCO' && (
                    <>
                      <div>
                        <span className="muted">IBAN</span>
                        <strong className="mono">{c.iban}</strong>
                      </div>
                      <div>
                        <span className="muted">Banco</span>
                        <strong>{c.banco || '—'}</strong>
                      </div>
                    </>
                  )}
                  <div>
                    <span className="muted">Saldo actual</span>
                    <strong className="accent">{c.saldoActual.toFixed(2)} €</strong>
                  </div>
                </div>
                <div className="inline-form" style={{ marginTop: 16 }}>
                  <button className="btn-small" onClick={() => empezarEdicion(c)}>
                    Editar
                  </button>
                  <button className="btn-small btn-danger" onClick={() => borrar(c.id)}>
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

export default CuentasBancarias
