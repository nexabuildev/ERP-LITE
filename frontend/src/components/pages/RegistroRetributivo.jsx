import { useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getRegistroRetributivo } from '../../api'

function RegistroRetributivo() {
  const { token } = useOutletContext()
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    getRegistroRetributivo(token).then(setData).catch((err) => setError(err.message))
  }, [token])

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Transparencia salarial · RD 902/2020</span>
        <h1>Registro retributivo</h1>
        <p>Compara tu salario con la media de tu categoría y la brecha de género de la empresa.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      {data && (
        <>
          <div className="stats-grid">
            <div className="stat-block">
              <span className="stat-block-label">Tu salario bruto anual</span>
              <span className="stat-block-value">{formatear(data.miSalario)}</span>
              <span className="stat-block-hint">{data.miCategoria ?? 'Sin categoría asignada'}</span>
            </div>
            <div className="stat-block">
              <span className="stat-block-label">Media de tu categoría</span>
              <span className="stat-block-value">{formatear(data.mediaCategoria)}</span>
            </div>
            <div className="stat-block">
              <span className="stat-block-label">Brecha salarial de género</span>
              <span className="stat-block-value">{data.brechaPorcentaje ?? '—'}%</span>
              <span className="stat-block-hint">Hombres vs. mujeres, media empresa</span>
            </div>
          </div>

          <div className="card">
            <h2>Media por género (empresa)</h2>
            <div className="report-grid">
              <div>
                <span className="muted">Hombres</span>
                <strong>{formatear(data.mediaHombres)}</strong>
              </div>
              <div>
                <span className="muted">Mujeres</span>
                <strong>{formatear(data.mediaMujeres)}</strong>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  )
}

function formatear(valor) {
  return valor != null ? `${valor.toLocaleString('es-ES')} €` : '—'
}

export default RegistroRetributivo
