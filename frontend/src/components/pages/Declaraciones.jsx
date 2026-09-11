import { useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getMisDeclaraciones } from '../../api'

function Declaraciones() {
  const { token } = useOutletContext()
  const [declaraciones, setDeclaraciones] = useState([])
  const [error, setError] = useState(null)

  useEffect(() => {
    getMisDeclaraciones(token).then(setDeclaraciones).catch((err) => setError(err.message))
  }, [token])

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Obligaciones fiscales</span>
        <h1>Declaraciones</h1>
        <p>Próximos vencimientos según tu situación (asalariado o autónomo).</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="timeline">
        {declaraciones.map((d, i) => (
          <div key={i} className="timeline-item">
            <span className="timeline-days">{d.diasRestantes}d</span>
            <div>
              <h3>{d.nombre}</h3>
              <p className="muted">{d.descripcion}</p>
              <p className="muted">Fecha límite: {d.fechaLimite}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default Declaraciones
