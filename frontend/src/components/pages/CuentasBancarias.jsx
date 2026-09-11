import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { anadirCuentaBancaria, getMisCuentasBancarias, eliminarCuentaBancaria } from '../../api'

function CuentasBancarias() {
  const { token } = useOutletContext()
  const [cuentas, setCuentas] = useState([])
  const [alias, setAlias] = useState('')
  const [iban, setIban] = useState('')
  const [banco, setBanco] = useState('')
  const [principal, setPrincipal] = useState(false)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const cargar = useCallback(() => {
    getMisCuentasBancarias(token).then(setCuentas).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await anadirCuentaBancaria(token, { alias, iban, banco, principal })
      setAlias('')
      setIban('')
      setBanco('')
      setPrincipal(false)
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
      await eliminarCuentaBancaria(token, id)
      cargar()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Mis cuentas</span>
        <h1>Cuentas bancarias</h1>
        <p>Guarda tus IBAN para tener a mano dónde recibes tu nómina y tus ahorros.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="card">
        <h2>Añadir cuenta</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field">
            <label>Alias</label>
            <input type="text" value={alias} onChange={(e) => setAlias(e.target.value)} placeholder="Cuenta principal" required />
          </div>
          <div className="field field-grow">
            <label>IBAN</label>
            <input type="text" value={iban} onChange={(e) => setIban(e.target.value)} placeholder="ES91 2100 0418..." required />
          </div>
          <div className="field">
            <label>Banco</label>
            <input type="text" value={banco} onChange={(e) => setBanco(e.target.value)} placeholder="Opcional" />
          </div>
          <div className="field field-checkbox">
            <label>
              <input type="checkbox" checked={principal} onChange={(e) => setPrincipal(e.target.checked)} /> Principal
            </label>
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Añadir'}
          </button>
        </form>
      </div>

      {cuentas.length === 0 && !error && <p className="empty-state">Todavía no has añadido ninguna cuenta.</p>}

      {cuentas.length > 0 && (
        <div className="card table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Alias</th>
                <th>IBAN</th>
                <th>Banco</th>
                <th></th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {cuentas.map((c) => (
                <tr key={c.id}>
                  <td className="bold">{c.alias}</td>
                  <td className="muted">{c.iban}</td>
                  <td className="muted">{c.banco || '—'}</td>
                  <td>{c.principal && <span className="badge badge-aprobada">Principal</span>}</td>
                  <td className="actions">
                    <button className="btn-small btn-danger" onClick={() => borrar(c.id)}>
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default CuentasBancarias
