import { useState } from 'react'

const W = 640
const H = 300
const PAD = { top: 24, right: 16, bottom: 36, left: 54 }
const PLOT_W = W - PAD.left - PAD.right
const PLOT_H = H - PAD.top - PAD.bottom

function formatoCorto(valor) {
  const abs = Math.abs(valor)
  if (abs >= 1000) return `${(valor / 1000).toFixed(abs >= 10000 ? 0 : 1)}k €`
  return `${Math.round(valor)} €`
}

function tickBonito(maxAbs) {
  if (maxAbs <= 0) return 100
  const potencia = Math.pow(10, Math.floor(Math.log10(maxAbs)))
  const normalizado = maxAbs / potencia
  let paso
  if (normalizado <= 1) paso = 0.25
  else if (normalizado <= 2) paso = 0.5
  else if (normalizado <= 5) paso = 1
  else paso = 2
  return paso * potencia
}

function HistorialChart({ puntos }) {
  const [activo, setActivo] = useState(null)

  if (!puntos || puntos.length === 0) return null

  const valores = puntos.flatMap((p) => [p.ingresos, p.gastos, p.saldoAcumulado])
  const maxValor = Math.max(0, ...valores)
  const minValor = Math.min(0, ...valores)
  const paso = tickBonito(Math.max(Math.abs(maxValor), Math.abs(minValor)) || 100)
  const dominioMax = Math.ceil(maxValor / paso) * paso || paso
  const dominioMin = Math.floor(minValor / paso) * paso
  const rango = dominioMax - dominioMin || 1

  const y = (valor) => PAD.top + ((dominioMax - valor) / rango) * PLOT_H
  const yBase = y(0)

  const gridTicks = []
  for (let v = dominioMin; v <= dominioMax + 1e-9; v += paso) gridTicks.push(Math.round(v * 100) / 100)

  const grupoAncho = PLOT_W / puntos.length
  const barraAncho = Math.min(20, grupoAncho * 0.22)
  const gap = 3

  const puntosLinea = puntos.map((p, i) => {
    const cx = PAD.left + grupoAncho * (i + 0.5)
    return { x: cx, y: y(p.saldoAcumulado) }
  })
  const lineaPath = puntosLinea.map((pt, i) => `${i === 0 ? 'M' : 'L'} ${pt.x} ${pt.y}`).join(' ')

  const activoData = activo != null ? puntos[activo] : null
  const tooltipLeft = activo != null ? ((PAD.left + grupoAncho * (activo + 0.5)) / W) * 100 : 0
  const tooltipTop = activo != null ? (Math.min(y(activoData.ingresos), y(activoData.gastos), y(activoData.saldoAcumulado)) / H) * 100 : 0

  return (
    <div className="chart-wrap">
      <div className="chart-legend">
        <span className="chart-legend-item">
          <span className="chart-swatch" style={{ background: 'var(--chart-ingreso)' }} />
          Ingresos
        </span>
        <span className="chart-legend-item">
          <span className="chart-swatch" style={{ background: 'var(--chart-gasto)' }} />
          Gastos
        </span>
        <span className="chart-legend-item">
          <span className="chart-line-key" />
          Saldo neto acumulado
        </span>
      </div>

      <div className="chart-svg-wrap" style={{ aspectRatio: `${W} / ${H}` }}>
        <svg viewBox={`0 0 ${W} ${H}`} width="100%" height="100%" role="img" aria-label="Historial mensual de ingresos, gastos y saldo neto acumulado">
          {gridTicks.map((v) => (
            <g key={v}>
              <line
                x1={PAD.left}
                x2={W - PAD.right}
                y1={y(v)}
                y2={y(v)}
                stroke={v === 0 ? 'var(--line-soft)' : 'var(--line-soft)'}
                strokeWidth={v === 0 ? 1.5 : 1}
              />
              <text x={PAD.left - 8} y={y(v)} textAnchor="end" dominantBaseline="middle" className="chart-axis-label">
                {formatoCorto(v)}
              </text>
            </g>
          ))}

          {puntos.map((p, i) => {
            const cx = PAD.left + grupoAncho * (i + 0.5)
            const xIngreso = cx - gap / 2 - barraAncho
            const xGasto = cx + gap / 2
            const activoAqui = activo === i
            return (
              <g
                key={p.periodo}
                onMouseEnter={() => setActivo(i)}
                onMouseLeave={() => setActivo(null)}
                onFocus={() => setActivo(i)}
                onBlur={() => setActivo(null)}
                tabIndex={0}
                style={{ cursor: 'pointer', outline: 'none' }}
              >
                <rect x={PAD.left + grupoAncho * i} y={PAD.top} width={grupoAncho} height={PLOT_H} fill="transparent" />

                <rect
                  x={xIngreso}
                  y={Math.min(y(p.ingresos), yBase)}
                  width={barraAncho}
                  height={Math.abs(y(p.ingresos) - yBase)}
                  rx={4}
                  fill="var(--chart-ingreso)"
                  opacity={activoAqui || activo === null ? 1 : 0.45}
                />
                <rect
                  x={xGasto}
                  y={Math.min(y(p.gastos), yBase)}
                  width={barraAncho}
                  height={Math.abs(y(p.gastos) - yBase)}
                  rx={4}
                  fill="var(--chart-gasto)"
                  opacity={activoAqui || activo === null ? 1 : 0.45}
                />

                <text x={cx} y={H - PAD.bottom + 18} textAnchor="middle" className="chart-axis-label">
                  {p.etiqueta}
                </text>
              </g>
            )
          })}

          <path d={lineaPath} fill="none" stroke="var(--chart-neto)" strokeWidth={2} strokeLinejoin="round" strokeLinecap="round" />
          {puntosLinea.map((pt, i) => (
            <circle
              key={i}
              cx={pt.x}
              cy={pt.y}
              r={4}
              fill="var(--chart-neto)"
              stroke="var(--paper-raised)"
              strokeWidth={2}
            />
          ))}

          {activo != null && (
            <line
              x1={PAD.left + grupoAncho * (activo + 0.5)}
              x2={PAD.left + grupoAncho * (activo + 0.5)}
              y1={PAD.top}
              y2={H - PAD.bottom}
              stroke="var(--line)"
              strokeWidth={1}
              strokeDasharray="3 3"
              opacity={0.4}
            />
          )}
        </svg>

        {activoData && (
          <div className="chart-tooltip" style={{ left: `${tooltipLeft}%`, top: `${tooltipTop}%` }}>
            <strong>{activoData.etiqueta}</strong>
            <span>
              <span className="chart-swatch" style={{ background: 'var(--chart-ingreso)' }} /> Ingresos: <b>{activoData.ingresos.toFixed(2)} €</b>
            </span>
            <span>
              <span className="chart-swatch" style={{ background: 'var(--chart-gasto)' }} /> Gastos: <b>{activoData.gastos.toFixed(2)} €</b>
            </span>
            <span>
              <span className="chart-swatch" style={{ background: 'var(--chart-neto)' }} /> Saldo acumulado: <b>{activoData.saldoAcumulado.toFixed(2)} €</b>
            </span>
          </div>
        )}
      </div>
    </div>
  )
}

export default HistorialChart
