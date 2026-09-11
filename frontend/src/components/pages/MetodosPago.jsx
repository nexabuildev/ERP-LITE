import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import {
  crearMetodoPago,
  getMisMetodosPago,
  editarMetodoPago,
  eliminarMetodoPago,
  getMetodoPagoSensible,
  getMisCuentasBancarias,
} from '../../api'

const VACIO = { tipo: 'TARJETA', alias: '', titular: '', numero: '', fechaCaducidad: '', cvv: '', emailPaypal: '', cuentaVinculadaId: '' }

function MetodosPago() {
  const { token } = useOutletContext()
  const [metodos, setMetodos] = useState([])
  const [cuentas, setCuentas] = useState([])
  const [form, setForm] = useState(VACIO)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const [editandoId, setEditandoId] = useState(null)
  const [editForm, setEditForm] = useState(null)
  const [guardandoEdicion, setGuardandoEdicion] = useState(false)

  const [revelados, setRevelados] = useState({}) // { [id]: { numero, cvv, emailPaypal } | undefined }
  const [cargandoRevelado, setCargandoRevelado] = useState(null)

  const cargar = useCallback(() => {
    getMisMetodosPago(token).then(setMetodos).catch((err) => setError(err.message))
    getMisCuentasBancarias(token).then(setCuentas).catch(() => {})
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await crearMetodoPago(token, { ...form, cuentaVinculadaId: form.cuentaVinculadaId || null })
      setForm(VACIO)
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const empezarEdicion = (m) => {
    setEditandoId(m.id)
    setEditForm({
      tipo: m.tipo,
      alias: m.alias,
      titular: m.titular || '',
      numero: '',
      fechaCaducidad: m.fechaCaducidad || '',
      cvv: '',
      emailPaypal: '',
      cuentaVinculadaId: m.cuentaVinculadaId || '',
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
      await editarMetodoPago(token, id, { ...editForm, cuentaVinculadaId: editForm.cuentaVinculadaId || null })
      cancelarEdicion()
      setRevelados((r) => ({ ...r, [id]: undefined }))
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
      await eliminarMetodoPago(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  const toggleRevelar = async (id) => {
    if (revelados[id]) {
      setRevelados((r) => ({ ...r, [id]: undefined }))
      return
    }
    setError(null)
    setCargandoRevelado(id)
    try {
      const datos = await getMetodoPagoSensible(token, id)
      setRevelados((r) => ({ ...r, [id]: datos }))
    } catch (err) {
      setError(err.message)
    } finally {
      setCargandoRevelado(null)
    }
  }

  const esTarjeta = form.tipo === 'TARJETA'

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Cartera</span>
        <h1>Métodos de pago</h1>
        <p>Guarda tarjetas o cuentas de PayPal. El número completo y el CVV están tapados hasta que pulsas el ojo.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="card">
        <h2>Añadir método</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field">
            <label>Tipo</label>
            <select value={form.tipo} onChange={(e) => setForm({ ...form, tipo: e.target.value })}>
              <option value="TARJETA">Tarjeta</option>
              <option value="PAYPAL">PayPal</option>
            </select>
          </div>
          <div className="field field-grow">
            <label>Alias</label>
            <input type="text" value={form.alias} onChange={(e) => setForm({ ...form, alias: e.target.value })} placeholder="Visa personal" required />
          </div>

          {esTarjeta ? (
            <>
              <div className="field field-grow">
                <label>Titular</label>
                <input type="text" value={form.titular} onChange={(e) => setForm({ ...form, titular: e.target.value })} required />
              </div>
              <div className="field field-grow">
                <label>Número de tarjeta</label>
                <input
                  type="text"
                  inputMode="numeric"
                  value={form.numero}
                  onChange={(e) => setForm({ ...form, numero: e.target.value })}
                  placeholder="4242 4242 4242 4242"
                  required
                />
              </div>
              <div className="field">
                <label>Caducidad (MM/AA)</label>
                <input type="text" value={form.fechaCaducidad} onChange={(e) => setForm({ ...form, fechaCaducidad: e.target.value })} placeholder="09/29" required />
              </div>
              <div className="field">
                <label>CVV</label>
                <input type="password" inputMode="numeric" maxLength={4} value={form.cvv} onChange={(e) => setForm({ ...form, cvv: e.target.value })} required />
              </div>
              <div className="field field-grow">
                <label>Cuenta bancaria vinculada (opcional)</label>
                <select value={form.cuentaVinculadaId} onChange={(e) => setForm({ ...form, cuentaVinculadaId: e.target.value })}>
                  <option value="">Sin vincular</option>
                  {cuentas.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.alias}
                    </option>
                  ))}
                </select>
              </div>
            </>
          ) : (
            <div className="field field-grow">
              <label>Email de PayPal</label>
              <input type="email" value={form.emailPaypal} onChange={(e) => setForm({ ...form, emailPaypal: e.target.value })} required />
            </div>
          )}

          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Añadir'}
          </button>
        </form>
      </div>

      {metodos.length === 0 && !error && <p className="empty-state">Todavía no has guardado ningún método de pago.</p>}

      <div className="report-list">
        {metodos.map((m) =>
          editandoId === m.id ? (
            <div key={m.id} className="card report-card">
              <h2>Editando {m.alias}</h2>
              <div className="inline-form" style={{ marginTop: 12 }}>
                <div className="field field-grow">
                  <label>Alias</label>
                  <input type="text" value={editForm.alias} onChange={(e) => setEditForm({ ...editForm, alias: e.target.value })} />
                </div>
                {editForm.tipo === 'TARJETA' ? (
                  <>
                    <div className="field field-grow">
                      <label>Titular</label>
                      <input type="text" value={editForm.titular} onChange={(e) => setEditForm({ ...editForm, titular: e.target.value })} />
                    </div>
                    <div className="field field-grow">
                      <label>Número nuevo (opcional)</label>
                      <input
                        type="text"
                        value={editForm.numero}
                        onChange={(e) => setEditForm({ ...editForm, numero: e.target.value })}
                        placeholder="Dejar en blanco para no cambiar"
                      />
                    </div>
                    <div className="field">
                      <label>Caducidad</label>
                      <input type="text" value={editForm.fechaCaducidad} onChange={(e) => setEditForm({ ...editForm, fechaCaducidad: e.target.value })} />
                    </div>
                    <div className="field">
                      <label>CVV nuevo (opcional)</label>
                      <input type="password" maxLength={4} value={editForm.cvv} onChange={(e) => setEditForm({ ...editForm, cvv: e.target.value })} />
                    </div>
                    <div className="field field-grow">
                      <label>Cuenta bancaria vinculada</label>
                      <select value={editForm.cuentaVinculadaId} onChange={(e) => setEditForm({ ...editForm, cuentaVinculadaId: e.target.value })}>
                        <option value="">Sin vincular</option>
                        {cuentas.map((c) => (
                          <option key={c.id} value={c.id}>
                            {c.alias}
                          </option>
                        ))}
                      </select>
                    </div>
                  </>
                ) : (
                  <div className="field field-grow">
                    <label>Email nuevo (opcional)</label>
                    <input
                      type="email"
                      value={editForm.emailPaypal}
                      onChange={(e) => setEditForm({ ...editForm, emailPaypal: e.target.value })}
                      placeholder="Dejar en blanco para no cambiar"
                    />
                  </div>
                )}
                <button className="btn-small" disabled={guardandoEdicion} onClick={() => guardarEdicion(m.id)}>
                  Guardar
                </button>
                <button className="btn-small" onClick={cancelarEdicion}>
                  Cancelar
                </button>
              </div>
            </div>
          ) : (
            <div key={m.id} className="card report-card">
              <div className="report-card-header">
                <h2>{m.alias}</h2>
                <span className={`badge ${m.tipo === 'TARJETA' ? 'badge-normal' : 'badge-bizum'}`}>{m.tipo}</span>
              </div>

              {m.tipo === 'TARJETA' ? (
                <div className="report-grid">
                  <div>
                    <span className="muted">Titular</span>
                    <strong>{m.titular}</strong>
                  </div>
                  <div>
                    <span className="muted">Número</span>
                    <strong className="mono">
                      {revelados[m.id] ? revelados[m.id].numero : m.numeroEnmascarado}
                    </strong>
                  </div>
                  <div>
                    <span className="muted">Caducidad</span>
                    <strong>{m.fechaCaducidad}</strong>
                  </div>
                  <div>
                    <span className="muted">CVV</span>
                    <strong className="mono">{revelados[m.id] ? revelados[m.id].cvv : '•••'}</strong>
                  </div>
                  <div>
                    <span className="muted">Cuenta vinculada</span>
                    <strong>{m.cuentaVinculadaAlias || '—'}</strong>
                  </div>
                </div>
              ) : (
                <div className="report-grid">
                  <div>
                    <span className="muted">Email PayPal</span>
                    <strong>{revelados[m.id] ? revelados[m.id].emailPaypal : m.emailEnmascarado}</strong>
                  </div>
                </div>
              )}

              <div className="inline-form" style={{ marginTop: 16 }}>
                <button className="btn-small" disabled={cargandoRevelado === m.id} onClick={() => toggleRevelar(m.id)}>
                  {revelados[m.id] ? '🙈 Ocultar' : '👁️ Ver datos completos'}
                </button>
                <button className="btn-small" onClick={() => empezarEdicion(m)}>
                  Editar
                </button>
                <button className="btn-small btn-danger" onClick={() => borrar(m.id)}>
                  Eliminar
                </button>
              </div>
            </div>
          )
        )}
      </div>
    </div>
  )
}

export default MetodosPago
