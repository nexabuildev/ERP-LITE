import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { NAV_ENTRIES } from './layout/navEntries'
import { applyTheme, getEffectiveTheme } from '../theme'

function paginasNavegables() {
  const paginas = []
  for (const entry of NAV_ENTRIES) {
    if (entry.type === 'link') {
      paginas.push({ id: `nav-${entry.to}`, label: entry.label, to: entry.to })
    } else {
      for (const item of entry.items) {
        paginas.push({ id: `nav-${item.to}`, label: `${entry.label} · ${item.label}`, to: item.to })
      }
    }
  }
  return paginas
}

function CommandPalette({ onLogout }) {
  const [abierto, setAbierto] = useState(false)
  const [busqueda, setBusqueda] = useState('')
  const [seleccion, setSeleccion] = useState(0)
  const navigate = useNavigate()

  const paginas = useMemo(() => paginasNavegables(), [])

  const acciones = useMemo(
    () => [
      {
        id: 'accion-tema',
        label: `Cambiar a modo ${getEffectiveTheme() === 'dark' ? 'claro' : 'oscuro'}`,
        run: () => applyTheme(getEffectiveTheme() === 'dark' ? 'light' : 'dark'),
      },
      { id: 'accion-logout', label: 'Cerrar sesión', run: () => onLogout?.() },
    ],
    [onLogout]
  )

  const items = useMemo(() => {
    const todos = [
      ...paginas.map((p) => ({ ...p, tipo: 'Ir a' })),
      ...acciones.map((a) => ({ ...a, tipo: 'Acción' })),
    ]
    const q = busqueda.trim().toLowerCase()
    if (!q) return todos
    return todos.filter((item) => item.label.toLowerCase().includes(q))
  }, [busqueda, paginas, acciones])

  useEffect(() => {
    const handleKeyDown = (e) => {
      const esAtajo = (e.metaKey || e.ctrlKey) && e.key.toLowerCase() === 'k'
      if (esAtajo) {
        e.preventDefault()
        setAbierto((prev) => !prev)
        setBusqueda('')
        setSeleccion(0)
        return
      }
      if (e.key === 'Escape') {
        setAbierto(false)
      }
    }
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [])

  const ejecutar = (item) => {
    if (!item) return
    if (item.to) navigate(item.to)
    else item.run?.()
    setAbierto(false)
  }

  const handleBusquedaChange = (valor) => {
    setBusqueda(valor)
    setSeleccion(0)
  }

  const handleKeyDownInput = (e) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault()
      setSeleccion((prev) => Math.min(prev + 1, items.length - 1))
    } else if (e.key === 'ArrowUp') {
      e.preventDefault()
      setSeleccion((prev) => Math.max(prev - 1, 0))
    } else if (e.key === 'Enter') {
      e.preventDefault()
      ejecutar(items[seleccion])
    }
  }

  if (!abierto) return null

  return (
    <div className="command-overlay" onClick={() => setAbierto(false)}>
      <div className="command-palette" onClick={(e) => e.stopPropagation()}>
        <input
          type="text"
          autoFocus
          className="command-input"
          placeholder="Ir a una sección o ejecutar una acción..."
          value={busqueda}
          onChange={(e) => handleBusquedaChange(e.target.value)}
          onKeyDown={handleKeyDownInput}
        />
        <div className="command-list">
          {items.length === 0 && <p className="command-empty">Sin resultados</p>}
          {items.map((item, i) => (
            <button
              type="button"
              key={item.id}
              className={`command-item${i === seleccion ? ' active' : ''}`}
              onMouseEnter={() => setSeleccion(i)}
              onClick={() => ejecutar(item)}
            >
              <span className="command-item-tipo">{item.tipo}</span>
              {item.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}

export default CommandPalette
