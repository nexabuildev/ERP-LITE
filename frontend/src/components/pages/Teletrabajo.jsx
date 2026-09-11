import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { registrarTeletrabajo, getMisRegistrosTeletrabajo } from '../../api'

const MESES = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
]

function Teletrabajo() {
  const { token } = useOutletContext()
  const hoy = new Date()
  const [mes, setMes] = useState(hoy.getMonth() + 1)
  const [anio, setAnio] = useState(hoy.getFullYear())
  const [dias, setDias] = useState('')
  const [registros, setRegistros] = useState([])
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const cargar = useCallback(() => {
    getMisRegistrosTeletrabajo(token).then(setRegistros).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await registrarTeletrabajo(token, {
        mes: Number(mes),
        anio: Number(anio),
        diasTeletrabajados: Number(dias),
      })
      setDias('')
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Ley 10/2021 · Trabajo a distancia</span>
        <h1>Gastos de teletrabajo</h1>
        <p>Registra tus días teletrabajados y calculamos la compensación automáticamente.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="card">
        <h2>Registrar mes</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field">
            <label>Mes</label>
            <select value={mes} onChange={(e) => setMes(e.target.value)}>
              {MESES.map((m, i) => (
                <option key={m} value={i + 1}>
                  {m}
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label>Año</label>
            <input type="number" value={anio} onChange={(e) => setAnio(e.target.value)} />
          </div>
          <div className="field">
            <label>Días teletrabajados</label>
            <input type="number" min="0" max="31" value={dias} onChange={(e) => setDias(e.target.value)} required />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Guardando...' : 'Registrar'}
          </button>
        </form>
      </div>

      {registros.length > 0 && (
        <div className="card">
          <h2>Histórico</h2>
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Mes</th>
                  <th>Días</th>
                  <th>Compensación</th>
                </tr>
              </thead>
              <tbody>
                {registros.map((r) => (
                  <tr key={r.id}>
                    <td>
                      {MESES[r.mes - 1]} {r.anio}
                    </td>
                    <td>{r.diasTeletrabajados}</td>
                    <td className="accent bold">{r.compensacion.toFixed(2)} €</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  )
}

export default Teletrabajo
