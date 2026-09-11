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
  getHistorialMovimientos,
  getMisMetasAhorro,
  getMisNominas,
  getWrapped,
  getSaludAdministrativa,
} from '../../api'
import HistorialChart from '../HistorialChart'

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
      getHistorialMovimientos(token),
      getMisMetasAhorro(token),
      getMisNominas(token),
      getWrapped(token),
      getSaludAdministrativa(token),
    ])
      .then(([fichajes, saldo, declaraciones, teletrabajo, alertas, movimientos, historial, ahorros, nominas, wrapped, salud]) => {
        setData({
          fichajes,
          saldo,
          proximaDeclaracion: declaraciones[0],
          teletrabajo: teletrabajo[0],
          alertas,
          movimientos,
          historial,
          ahorros,
          ultimaNomina: nominas[0],
          wrapped,
          salud,
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

          {data.historial?.puntos?.length > 0 && (
            <div className="card">
              <div className="card-header">
                <div>
                  <h2>Historial financiero</h2>
                  <p>Ingresos, gastos y saldo neto acumulado de los últimos 6 meses.</p>
                </div>
                {data.historial.variacionPorcentaje != null && (
                  <span className={`stat-block-value ${data.historial.variacionPorcentaje >= 0 ? 'amount-positive' : 'amount-negative'}`} style={{ fontSize: 22 }}>
                    {data.historial.variacionPorcentaje >= 0 ? '+' : ''}
                    {data.historial.variacionPorcentaje}%
                    <span className="stat-block-hint" style={{ display: 'block', fontSize: 11 }}>
                      vs mes anterior
                    </span>
                  </span>
                )}
              </div>
              <HistorialChart puntos={data.historial.puntos} />
            </div>
          )}

          {data.wrapped && (
            <div className="card">
              <h2>Resumen de {data.wrapped.mesEtiqueta}</h2>
              <div className="report-grid">
                <div>
                  <span className="muted">Ingresos</span>
                  <strong className="amount-positive">+{data.wrapped.ingresos.toFixed(2)} €</strong>
                </div>
                <div>
                  <span className="muted">Gastos</span>
                  <strong className="amount-negative">-{data.wrapped.gastos.toFixed(2)} €</strong>
                </div>
                <div>
                  <span className="muted">Balance del mes</span>
                  <strong className={data.wrapped.balance >= 0 ? 'amount-positive' : 'amount-negative'}>
                    {data.wrapped.balance.toFixed(2)} €
                  </strong>
                </div>
                {data.wrapped.variacionBalancePorcentaje != null && (
                  <div>
                    <span className="muted">Vs. mes anterior</span>
                    <strong className={data.wrapped.variacionBalancePorcentaje >= 0 ? 'amount-positive' : 'amount-negative'}>
                      {data.wrapped.variacionBalancePorcentaje >= 0 ? '+' : ''}
                      {data.wrapped.variacionBalancePorcentaje}%
                    </strong>
                  </div>
                )}
                {data.wrapped.categoriaTopGasto && (
                  <div>
                    <span className="muted">Mayor gasto</span>
                    <strong>{data.wrapped.categoriaTopGasto}</strong>
                  </div>
                )}
                <div>
                  <span className="muted">Vacaciones disfrutadas</span>
                  <strong>{data.wrapped.diasVacacionesDisfrutados} días</strong>
                </div>
                <div>
                  <span className="muted">Horas trabajadas</span>
                  <strong>{data.wrapped.horasTrabajadas} h</strong>
                </div>
                <div>
                  <span className="muted">Días de teletrabajo</span>
                  <strong>{data.wrapped.diasTeletrabajados}</strong>
                </div>
              </div>
            </div>
          )}

          {data.salud && (
            <div className="card">
              <div className="card-header">
                <div>
                  <h2>Salud administrativa</h2>
                  <p>Cómo de al día están tu perfil, tus alertas, tus declaraciones y tus cuentas.</p>
                </div>
                <span
                  className="stat-block-value"
                  style={{ fontSize: 40, color: data.salud.puntuacion >= 70 ? 'var(--accent)' : 'inherit' }}
                >
                  {data.salud.puntuacion}
                </span>
              </div>
              <div className="ledger">
                {data.salud.factores.map((f, i) => (
                  <div key={i} className="ledger-row">
                    <div className="ledger-info">
                      <span className="ledger-concepto">{f.etiqueta}</span>
                      <span className="muted small">{f.mensaje}</span>
                    </div>
                    <span className="bold">
                      {f.puntos}/{f.puntosMaximos}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

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
