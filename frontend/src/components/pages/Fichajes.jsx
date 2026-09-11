import { useCallback, useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { ficharEntrada, ficharSalida, getResumenFichajes } from '../../api'

function Fichajes() {
  const { token } = useOutletContext()
  const [resumen, setResumen] = useState(null)
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const cargar = useCallback(() => {
    getResumenFichajes(token).then(setResumen).catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const marcar = async (accion) => {
    setLoading(true)
    setError(null)
    try {
      if (accion === 'entrada') await ficharEntrada(token)
      else await ficharSalida(token)
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
        <span className="page-eyebrow">Control horario</span>
        <h1>Fichajes</h1>
        <p>Registra tu entrada y salida. El cómputo de horas es automático.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      <div className="card">
        <div className="card-header">
          <div>
            <h2>{resumen?.fichado ? 'Estás fichado' : 'No estás fichado'}</h2>
            <p>{resumen ? `${resumen.horasMesActual}h trabajadas este mes` : 'Cargando...'}</p>
          </div>
          <button
            className="btn-primary"
            disabled={loading || !resumen}
            onClick={() => marcar(resumen?.fichado ? 'salida' : 'entrada')}
          >
            {resumen?.fichado ? 'Fichar salida' : 'Fichar entrada'}
          </button>
        </div>

        {resumen?.fichajes?.length > 0 && (
          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Entrada</th>
                  <th>Salida</th>
                  <th>Horas</th>
                </tr>
              </thead>
              <tbody>
                {resumen.fichajes.map((f) => (
                  <tr key={f.id}>
                    <td>{new Date(f.fechaHoraEntrada).toLocaleString('es-ES')}</td>
                    <td>{f.fechaHoraSalida ? new Date(f.fechaHoraSalida).toLocaleString('es-ES') : '—'}</td>
                    <td className="bold">{f.horasTrabajadas ?? '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {resumen && resumen.fichajes.length === 0 && (
          <p className="empty-state">Todavía no tienes fichajes registrados.</p>
        )}
      </div>
    </div>
  )
}

export default Fichajes
