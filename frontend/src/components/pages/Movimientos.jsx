import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import {
  registrarMovimiento,
  editarMovimiento,
  eliminarMovimiento,
  getResumenMovimientos,
  getMisCuentasBancarias,
  getMisMetodosPago,
} from '../../api'
import { formatDate, hoyISO } from '../../utils/date'

const MESES = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
]
const ANIO_ACTUAL = new Date().getFullYear()
const ANIOS_FILTRO = Array.from({ length: 6 }, (_, i) => ANIO_ACTUAL - i)

// El origen se guarda como "cuenta:<id>" o "metodo:<id>" en un único <select>
function origenAValor(cuentaBancariaId, metodoPagoId) {
  if (metodoPagoId) return `metodo:${metodoPagoId}`
  if (cuentaBancariaId) return `cuenta:${cuentaBancariaId}`
  return ''
}

function valorAOrigen(valor) {
  if (!valor) return { cuentaBancariaId: null, metodoPagoId: null }
  const [tipo, id] = valor.split(':')
  return {
    cuentaBancariaId: tipo === 'cuenta' ? Number(id) : null,
    metodoPagoId: tipo === 'metodo' ? Number(id) : null,
  }
}

function SelectorOrigen({ value, onChange, cuentas, metodos }) {
  return (
    <div className="field field-grow">
      <label>¿De dónde sale/entra el dinero? (opcional)</label>
      <select value={value} onChange={(e) => onChange(e.target.value)}>
        <option value="">Sin vincular</option>
        {cuentas.length > 0 && (
          <optgroup label="Cuentas y efectivo">
            {cuentas.map((c) => (
              <option key={`cuenta:${c.id}`} value={`cuenta:${c.id}`}>
                {c.alias}
              </option>
            ))}
          </optgroup>
        )}
        {metodos.length > 0 && (
          <optgroup label="Tarjetas / PayPal">
            {metodos.map((m) => (
              <option key={`metodo:${m.id}`} value={`metodo:${m.id}`}>
                {m.alias}
              </option>
            ))}
          </optgroup>
        )}
      </select>
    </div>
  )
}

function Movimientos() {
  const { token } = useOutletContext()
  const [resumen, setResumen] = useState(null)
  const [cuentas, setCuentas] = useState([])
  const [metodos, setMetodos] = useState([])
  const [concepto, setConcepto] = useState('')
  const [importe, setImporte] = useState('')
  const [tipo, setTipo] = useState('GASTO')
  const [medioPago, setMedioPago] = useState('NORMAL')
  const [fecha, setFecha] = useState(hoyISO())
  const [origen, setOrigen] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const [filtroMes, setFiltroMes] = useState('')
  const [filtroAnio, setFiltroAnio] = useState('')

  const [editandoId, setEditandoId] = useState(null)
  const [editForm, setEditForm] = useState(null)
  const [guardandoEdicion, setGuardandoEdicion] = useState(false)

  const cargar = useCallback(() => {
    const filtro = filtroMes && filtroAnio ? { mes: Number(filtroMes), anio: Number(filtroAnio) } : {}
    getResumenMovimientos(token, filtro).then(setResumen).catch((err) => setError(err.message))
    getMisCuentasBancarias(token).then(setCuentas).catch(() => {})
    getMisMetodosPago(token).then(setMetodos).catch(() => {})
  }, [token, filtroMes, filtroAnio])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await registrarMovimiento(token, { concepto, importe: Number(importe), tipo, medioPago, fecha, ...valorAOrigen(origen) })
      setConcepto('')
      setImporte('')
      setOrigen('')
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
      concepto: m.concepto,
      importe: m.importe,
      tipo: m.tipo,
      medioPago: m.medioPago,
      fecha: m.fecha,
      origen: origenAValor(m.cuentaBancariaId, m.metodoPagoId),
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
      const { origen: origenForm, ...resto } = editForm
      await editarMovimiento(token, id, { ...resto, importe: Number(editForm.importe), ...valorAOrigen(origenForm) })
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
        <p>Tu saldo real: ingresos, Bizums y gastos, todo en un mismo extracto. Vincula un movimiento a una cuenta o tarjeta y su saldo se actualiza solo.</p>
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
          <SelectorOrigen value={origen} onChange={setOrigen} cuentas={cuentas} metodos={metodos} />
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Añadir'}
          </button>
        </form>
      </div>

      <div className="card">
        <h2>Filtrar por mes</h2>
        <div className="inline-form">
          <div className="field">
            <label>Mes</label>
            <select value={filtroMes} onChange={(e) => setFiltroMes(e.target.value)}>
              <option value="">Todos</option>
              {MESES.map((nombre, i) => (
                <option key={nombre} value={i + 1}>
                  {nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label>Año</label>
            <select value={filtroAnio} onChange={(e) => setFiltroAnio(e.target.value)}>
              <option value="">Todos</option>
              {ANIOS_FILTRO.map((a) => (
                <option key={a} value={a}>
                  {a}
                </option>
              ))}
            </select>
          </div>
        </div>
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
                    <SelectorOrigen
                      value={editForm.origen}
                      onChange={(v) => setEditForm({ ...editForm, origen: v })}
                      cuentas={cuentas}
                      metodos={metodos}
                    />
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
                      {formatDate(m.fecha)} · <span className={`badge badge-${m.medioPago.toLowerCase()}`}>{m.medioPago}</span>
                      {(m.cuentaBancariaAlias || m.metodoPagoAlias) && ` · ${m.cuentaBancariaAlias || m.metodoPagoAlias}`}
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
        <p className="empty-state">
          {filtroMes && filtroAnio ? 'No hay movimientos en ese mes.' : 'Todavía no has registrado movimientos.'}
        </p>
      )}
    </div>
  )
}

export default Movimientos
