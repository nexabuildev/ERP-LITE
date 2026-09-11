const STORAGE_KEY = 'ziviko-theme'

export function getStoredTheme() {
  try {
    return localStorage.getItem(STORAGE_KEY)
  } catch {
    return null
  }
}

export function applyTheme(theme) {
  if (theme === 'light' || theme === 'dark') {
    document.documentElement.setAttribute('data-theme', theme)
  } else {
    document.documentElement.removeAttribute('data-theme')
  }

  try {
    if (theme) {
      localStorage.setItem(STORAGE_KEY, theme)
    } else {
      localStorage.removeItem(STORAGE_KEY)
    }
  } catch {
    // localStorage no disponible (modo privado, etc.)
  }
}

export function initTheme() {
  const stored = getStoredTheme()
  if (stored) {
    applyTheme(stored)
  }
}

export function getEffectiveTheme() {
  const stored = getStoredTheme()
  if (stored) return stored
  return window.matchMedia?.('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}
