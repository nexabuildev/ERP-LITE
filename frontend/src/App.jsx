import { useState } from 'react'
import AuthPage from './components/AuthPage'
import Dashboard from './components/Dashboard'
import './App.css'

function App() {
  const [token, setToken] = useState(() => localStorage.getItem('token'))

  const handleAuthSuccess = (newToken) => {
    localStorage.setItem('token', newToken)
    setToken(newToken)
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    setToken(null)
  }

  if (token) {
    return <Dashboard token={token} onLogout={handleLogout} />
  }

  return <AuthPage onAuthSuccess={handleAuthSuccess} />
}

export default App
