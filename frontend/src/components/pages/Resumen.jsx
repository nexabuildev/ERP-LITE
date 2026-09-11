import { useCallback, useEffect, useState } from 'react'
import { Link, useOutletContext } from 'react-router-dom'
import {
  getResumenFichajes,
  ficharEntrada,
  ficharSalida,
  getSaldoVacaciones,
  getMisDeclaraciones,
  getMisRegistrosTeletrabajo,
  getResumenAlertas,
  getResumenMovimientos,
  getMisMetasAhorro,
  getMisNominas,
} from '../../api'

const MESES = [
  'Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio',
  'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre',
]

function Resumen() {
  const { token, perfil } = useOutletContext()
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)
  const [fichando, setFichando] = useState(false)

  const cargar = useCallback(() => {
    Promise.all([
      getResumenFichajes(token),
      getSaldoVacaciones(token),
      getMisDeclaraciones(token),
      getMisRegistrosTeletrabajo(token),
      getResumenAlertas(token),
      getResumenMovimientos(token),
      getMisMetasAhorro(token),
      getMisNominas(token),
    ])
      .then(([fichajes, saldo, declaraciones, teletrabajo, alertas, movimientos, ahorros, nominas]) => {
        setData({
          fichajes,
          saldo,
          proximaDeclaracion: declaraciones[0],
          teletrabajo: teletrabajo[0],
          alertas,
          movimientos,
          ahorros,
          ultimaNomina: nominas[0],
        })
      })
      .catch((err) => setError(err.message))
  }, [token])

  useEffect(cargar, [cargar])

  const marcarFichaje = async () => {
    setFichando(true)
    setError(null)
    try {
      if (data.fichajes.fichado) await ficharSalida(token)
      else await ficharEntrada(token)
      cargar()
    } catch (err) {
      setError(err.message)
    } finally {
      setFichando(false)
    }
  }

  const alertasCount = data ? data.alertas.vencidas + data.alertas.proximas : 0

  return (
    <div>
      <header className="page-header page-header-actions">
        <div>
          <span className="page-eyebrow">Expediente Nº {perfil?.id ?? '—'}</span>
          <h1>Hola, {perfil?.nombre?.split(' ')[0] ?? ''}</h1>
          <p>Tu expediente completo: laboral, financiero y lo que no puedes dejar caducar.</p>
        </div>
        <div className="notif-cluster">
          <Link to="/alertas" className="notif-icon" title="Alertas">
            🔔
            {alertasCount > 0 && <span className="notif-dot">{alertasCount}</span>}
          </Link>
          <Link to="/perfil" className="notif-icon" title="Perfil">
            👤
          </Link>
        </div>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}

      {data && (
        <>
          <div className="card card-highlight">
            <div className="card-header">
              <div>
                <h2>{data.fichajes.fichado ? 'Estás fichado ahora mismo' : 'No estás fichado'}</h2>
                <p>{data.fichajes.horasMesActual}h trabajadas este mes</p>
              </div>
              <button className="btn-primary" disabled={fichando} onClick={marcarFichaje}>
                {fichando ? 'Un momento...' : data.fichajes.fichado ? 'Fichar salida' : 'Fichar entrada'}
              </button>
            </div>
          </div>

          <div className="stats-grid">
            <Link to="/alertas" className="stat-block">
              <span className="stat-block-label">Alertas activas</span>
              <span className={`stat-block-value ${data.alertas.vencidas > 0 ? 'amount-negative' : ''}`}>
                {alertasCount}
              </span>
              <span className="stat-block-hint">{data.alertas.vencidas > 0 ? `${data.alertas.vencidas} vencidas` : 'Al día'}</span>
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

          <h2 className="section-title">Tus finanzas</h2>
          <div className="stats-grid stats-grid-compact">
            <Link to="/movimientos" className="stat-block">
              <span className="stat-block-label">Saldo movimientos</span>
              <span className="stat-block-value">{data.movimientos.saldo.toFixed(2)} €</span>
              <span className="stat-block-hint">
                +{data.movimientos.totalIngresos.toFixed(2)} € / -{data.movimientos.totalGastos.toFixed(2)} €
              </span>
            </Link>

            <Link to="/nominas" className="stat-block">
              <span className="stat-block-label">Última nómina</span>
              <span className="stat-block-value">
                {data.ultimaNomina ? `${data.ultimaNomina.salarioNeto.toFixed(2)} €` : '—'}
              </span>
              <span className="stat-block-hint">
                {data.ultimaNomina ? `${MESES[data.ultimaNomina.mes - 1]} ${data.ultimaNomina.anio}` : 'Sin nóminas'}
              </span>
            </Link>
          </div>

          {data.ahorros.length > 0 && (
            <div className="card">
              <h2>Metas de ahorro</h2>
              <div className="stacked-form">
                {data.ahorros.map((m) => (
                  <Link to="/ahorros" key={m.id} className="mini-goal">
                    <div className="mini-goal-header">
                      <span className="bold">{m.nombre}</span>
                      <span className="muted small">
                        {m.montoActual.toFixed(2)} € / {m.montoObjetivo.toFixed(2)} €
                      </span>
                    </div>
                    <div className="progress-bar" style={{ height: 12 }}>
                      <div className="progress-fill" style={{ width: `${m.porcentajeCompletado}%` }} />
                    </div>
                  </Link>
                ))}
              </div>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default Resumen
