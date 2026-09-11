import { useEffect, useState } from 'react'
import { Link, useOutletContext } from 'react-router-dom'
import { getResumenFichajes, getSaldoVacaciones, getMisDeclaraciones, getMisRegistrosTeletrabajo } from '../../api'

function Resumen() {
  const { token, perfil } = useOutletContext()
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    Promise.all([
      getResumenFichajes(token),
      getSaldoVacaciones(token),
      getMisDeclaraciones(token),
      getMisRegistrosTeletrabajo(token),
    ])
      .then(([fichajes, saldo, declaraciones, teletrabajo]) => {
        setData({ fichajes, saldo, proximaDeclaracion: declaraciones[0], teletrabajo: teletrabajo[0] })
      })
      .catch((err) => setError(err.message))
  }, [token])

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Expediente Nº {perfil?.id ?? '—'}</span>
        <h1>Hola, {perfil?.nombre?.split(' ')[0] ?? ''}</h1>
        <p>Este es el estado de tu situación laboral hoy.</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      {data && (
        <div className="stats-grid">
          <Link to="/fichajes" className="stat-block">
            <span className="stat-block-label">Horas este mes</span>
            <span className="stat-block-value">{data.fichajes.horasMesActual}h</span>
            <span className="stat-block-hint">
              {data.fichajes.fichado ? 'Fichado ahora mismo' : 'No estás fichado'}
            </span>
          </Link>

          <Link to="/vacaciones" className="stat-block">
            <span className="stat-block-label">Días de vacaciones</span>
            <span className="stat-block-value">{data.saldo.diasRestantes}</span>
            <span className="stat-block-hint">de {data.saldo.diasAnuales} al año</span>
          </Link>

          <Link to="/declaraciones" className="stat-block">
            <span className="stat-block-label">Próxima declaración</span>
            <span className="stat-block-value">
              {data.proximaDeclaracion ? `${data.proximaDeclaracion.diasRestantes}d` : '—'}
            </span>
            <span className="stat-block-hint">{data.proximaDeclaracion?.nombre ?? 'Sin pendientes'}</span>
          </Link>

          <Link to="/teletrabajo" className="stat-block">
            <span className="stat-block-label">Teletrabajo (último mes)</span>
            <span className="stat-block-value">{data.teletrabajo?.diasTeletrabajados ?? 0}</span>
            <span className="stat-block-hint">
              {data.teletrabajo ? `${data.teletrabajo.compensacion.toFixed(2)} € compensados` : 'Sin registrar'}
            </span>
          </Link>
        </div>
      )}
    </div>
  )
}

export default Resumen
