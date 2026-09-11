import { useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { simularNomina } from '../../api'

const PRESETS = [-10, -5, 5, 10, 20]

function Simulador() {
  const { token, perfil } = useOutletContext()
  const [nuevoSalario, setNuevoSalario] = useState(perfil?.salarioBrutoAnual ?? '')
  const [resultado, setResultado] = useState(null)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const aplicarPreset = (porcentaje) => {
    const base = perfil?.salarioBrutoAnual ?? Number(nuevoSalario) ?? 0
    setNuevoSalario(Math.round(base * (1 + porcentaje / 100)))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const data = await simularNomina(token, Number(nuevoSalario))
      setResultado(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">¿Y si...?</span>
        <h1>Simulador de sueldo</h1>
        <p>Prueba una subida o bajada de sueldo y mira cómo cambia tu neto mensual.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="card">
        <h2>Salario actual: {perfil?.salarioBrutoAnual?.toLocaleString('es-ES') ?? '—'} € brutos/año</h2>
        <form onSubmit={handleSubmit} className="inline-form">
          <div className="field field-grow">
            <label>Nuevo salario bruto anual (€)</label>
            <input
              type="number"
              min="1"
              step="100"
              value={nuevoSalario}
              onChange={(e) => setNuevoSalario(e.target.value)}
              required
            />
          </div>
          <button className="btn-primary" disabled={loading}>
            {loading ? 'Calculando...' : 'Simular'}
          </button>
        </form>

        <div className="preset-row">
          {PRESETS.map((p) => (
            <button key={p} type="button" className="btn-small" onClick={() => aplicarPreset(p)}>
              {p > 0 ? `+${p}%` : `${p}%`}
            </button>
          ))}
        </div>
      </div>

      {resultado && (
        <div className="stats-grid">
          <div className="stat-block">
            <span className="stat-block-label">Neto actual / mes</span>
            <span className="stat-block-value">{resultado.salarioActualNetoMensual.toFixed(2)} €</span>
          </div>
          <div className="stat-block">
            <span className="stat-block-label">Neto simulado / mes</span>
            <span className="stat-block-value">{resultado.nuevoSalarioNetoMensual.toFixed(2)} €</span>
          </div>
          <div className="stat-block">
            <span className="stat-block-label">Diferencia mensual</span>
            <span className={`stat-block-value ${resultado.diferenciaMensual >= 0 ? 'amount-positive' : 'amount-negative'}`}>
              {resultado.diferenciaMensual >= 0 ? '+' : ''}
              {resultado.diferenciaMensual.toFixed(2)} €
            </span>
            <span className="stat-block-hint">
              {resultado.diferenciaPorcentaje >= 0 ? '+' : ''}
              {resultado.diferenciaPorcentaje}%
            </span>
          </div>
        </div>
      )}

      <p className="muted small">
        Cálculo aproximado (retenciones e IRPF estimados). No sustituye una nómina real.
      </p>
    </div>
  )
}

export default Simulador
