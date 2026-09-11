import { useState } from 'react'
import { login, register } from '../api'

function AuthPage({ onAuthSuccess }) {
  const [mode, setMode] = useState('login') // 'login' | 'register'
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)

  const isLogin = mode === 'login'

  const switchMode = () => {
    setError(null)
    setMode(isLogin ? 'register' : 'login')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      const data = isLogin
        ? await login(email, password)
        : await register(nombre, email, password)
      onAuthSuccess(data.token)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-screen">
      <div className="auth-card">
        <div className="auth-brand">
          <span className="auth-logo">🚀</span>
          <h1>ERP Lite</h1>
          <p>{isLogin ? 'Inicia sesión para continuar' : 'Crea tu cuenta para empezar'}</p>
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
