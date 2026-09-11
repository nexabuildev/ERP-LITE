import { useState } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import AuthPage from './components/AuthPage'
import Layout from './components/layout/Layout'
import Resumen from './components/pages/Resumen'
import Fichajes from './components/pages/Fichajes'
import Vacaciones from './components/pages/Vacaciones'
import Nominas from './components/pages/Nominas'
import RegistroRetributivo from './components/pages/RegistroRetributivo'
import Declaraciones from './components/pages/Declaraciones'
import Teletrabajo from './components/pages/Teletrabajo'
import Perfil from './components/pages/Perfil'
import AdminVacaciones from './components/pages/AdminVacaciones'
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

  if (!token) {
    return <AuthPage onAuthSuccess={handleAuthSuccess} />
  }

  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout token={token} onLogout={handleLogout} />}>
          <Route path="/" element={<Resumen />} />
          <Route path="/fichajes" element={<Fichajes />} />
          <Route path="/vacaciones" element={<Vacaciones />} />
          <Route path="/nominas" element={<Nominas />} />
          <Route path="/registro-retributivo" element={<RegistroRetributivo />} />
          <Route path="/declaraciones" element={<Declaraciones />} />
          <Route path="/teletrabajo" element={<Teletrabajo />} />
          <Route path="/perfil" element={<Perfil />} />
          <Route path="/admin/vacaciones" element={<AdminVacaciones />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
