import { useState } from 'react'
import { login, register, reactivarCuentaLogin } from '../api'
import ThemeToggle from './ThemeToggle'

function AuthPage({ onAuthSuccess }) {
  const [mode, setMode] = useState('login') // 'login' | 'register'
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const [cuentaDesactivada, setCuentaDesactivada] = useState(false)
  const [reactivando, setReactivando] = useState(false)

  const isLogin = mode === 'login'

  const switchMode = () => {
    setError(null)
    setCuentaDesactivada(false)
    setMode(isLogin ? 'register' : 'login')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setCuentaDesactivada(false)
    setLoading(true)
    try {
      const data = isLogin
        ? await login(email, password)
        : await register(nombre, email, password)
      onAuthSuccess(data.token)
    } catch (err) {
      if (err.message.includes('CUENTA_DESACTIVADA')) {
        setCuentaDesactivada(true)
        setError('Tu cuenta está desactivada temporalmente.')
      } else {
        setError(err.message)
      }
    } finally {
      setLoading(false)
    }
  }

  const handleReactivar = async () => {
    setError(null)
    setReactivando(true)
    try {
      const data = await reactivarCuentaLogin(email, password)
      onAuthSuccess(data.token)
    } catch (err) {
      setError(err.message)
    } finally {
      setReactivando(false)
    }
  }

  return (
    <div className="auth-screen">
      <ThemeToggle className="auth-theme-toggle" />
      <div className="auth-card">
        <div className="auth-brand">
          <h1>ZIVIKO</h1>
          <p>{isLogin ? 'Portal del ciudadano — inicia sesión' : 'Crea tu cuenta y empieza tu expediente'}</p>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {!isLogin && (
            <div className="field">
              <label>Nombre completo</label>
              <input
                type="text"
                placeholder="Tu nombre"
                value={nombre}
                onChange={(e) => setNombre(e.target.value)}
                required
              />
            </div>
          )}

          <div className="field">
            <label>Email corporativo</label>
            <input
              type="email"
              placeholder="ej: ruben@erplite.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="field">
            <label>Contraseña</label>
            <input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn-primary btn-block" disabled={loading}>
            {loading ? 'Un momento...' : isLogin ? 'Iniciar sesión' : 'Crear cuenta'}
          </button>
        </form>

        {error && <div className="alert alert-error">⚠️ {error}</div>}

        {cuentaDesactivada && (
          <button type="button" className="btn-primary btn-block" style={{ marginTop: 12 }} onClick={handleReactivar} disabled={reactivando}>
            {reactivando ? 'Reactivando...' : 'Reactivar mi cuenta'}
          </button>
        )}

        <p className="auth-switch">
          {isLogin ? '¿No tienes cuenta?' : '¿Ya tienes cuenta?'}{' '}
          <button type="button" className="link-button" onClick={switchMode}>
            {isLogin ? 'Regístrate' : 'Inicia sesión'}
          </button>
        </p>
      </div>
    </div>
  )
}

export default AuthPage
