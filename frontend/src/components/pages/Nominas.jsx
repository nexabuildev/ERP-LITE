import { useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getMisNominas } from '../../api'

const MESES = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
]

function Nominas() {
  const { token } = useOutletContext()
  const [nominas, setNominas] = useState([])
  const [error, setError] = useState(null)

  useEffect(() => {
    getMisNominas(token).then(setNominas).catch((err) => setError(err.message))
  }, [token])

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Cobros</span>
        <h1>Nóminas</h1>
        <p>Histórico de tus últimas nóminas.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      {nominas.length === 0 && !error && <p className="empty-state">Todavía no hay nóminas registradas.</p>}

      <div className="report-list">
        {nominas.map((n) => (
          <div key={n.id} className="card report-card">
            <div className="report-card-header">
              <h2>
                {MESES[n.mes - 1]} {n.anio}
              </h2>
              <span className="muted">Pagada el {n.fechaPago}</span>
            </div>
            <div className="report-grid">
              <div>
                <span className="muted">Bruto</span>
                <strong>{n.salarioBruto.toFixed(2)} €</strong>
              </div>
              <div>
                <span className="muted">Deducciones</span>
                <strong>-{n.deducciones.toFixed(2)} €</strong>
              </div>
              <div>
                <span className="muted">Neto</span>
                <strong className="accent">{n.salarioNeto.toFixed(2)} €</strong>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default Nominas
