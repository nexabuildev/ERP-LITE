import { useState } from 'react'
import { applyTheme, getEffectiveTheme } from '../theme'

function ThemeToggle({ className = '' }) {
  const [theme, setTheme] = useState(getEffectiveTheme)

  const toggle = () => {
    const next = theme === 'dark' ? 'light' : 'dark'
    setTheme(next)
    applyTheme(next)
  }

  return (
    <button type="button" className={`theme-toggle ${className}`} onClick={toggle}>
      {theme === 'dark' ? '☀ Modo claro' : '☾ Modo oscuro'}
    </button>
  )
}

export default ThemeToggle
