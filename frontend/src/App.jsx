import { useState } from 'react'

function App() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [token, setToken] = useState(null)
  const [error, setError] = useState(null)
  const [empleados, setEmpleados] = useState([])

  // --- LOGICA (NO CAMBIA) ---
  const handleLogin = async (e) => {
    e.preventDefault()
    setError(null)
    try {
      const response = await fetch('http://localhost:8080/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      })
      if (!response.ok) throw new Error('Credenciales incorrectas')
      const data = await response.json()
      setToken(data.token)
    } catch (err) {
      setError(err.message)
    }
  }

  const obtenerEmpleados = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/v1/empleados', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      })
      if (!response.ok) throw new Error('Error al obtener datos')
      const data = await response.json()
      setEmpleados(data)
    } catch (err) {
      alert("Error: " + err.message)
    }
  }

  // --- PANTALLA DASHBOARD (DENTRO) ---
  if (token) {
    return (
      <div style={{ minHeight: '100vh', width: '100vw', background: '#f5f6fa', padding: '0' }}>
        
        {/* BARRA SUPERIOR (NAVBAR) */}
        <nav style={{ background: '#2c3e50', padding: '15px 40px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: 'white', boxShadow: '0 2px 5px rgba(0,0,0,0.1)' }}>
          <h2 style={{ margin: 0 }}>🚀 ERP Lite Dashboard</h2>
          <button 
            onClick={() => { setToken(null); setEmpleados([]) }} 
            style={{ background: '#e74c3c', color: 'white', border: 'none', padding: '8px 15px', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}
          >
            Cerrar Sesión
          </button>
        </nav>

        {/* CONTENIDO PRINCIPAL */}
        <div style={{ padding: '40px', maxWidth: '1000px', margin: '0 auto' }}>
          
          <div style={{ background: 'white', padding: '30px', borderRadius: '10px', boxShadow: '0 2px 10px rgba(0,0,0,0.05)' }}>
            <h1 style={{ color: '#333', marginTop: 0 }}>Gestión de Empleados</h1>
            <p style={{ color: '#666', marginBottom: '20px' }}>Bienvenido al panel de administración. Aquí puedes consultar la base de datos.</p>
            
            <button 
              onClick={obtenerEmpleados}
              style={{ background: '#3498db', color: 'white', padding: '12px 25px', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '16px', fontWeight: '500', display: 'flex', alignItems: 'center', gap: '10px' }}
            >
              🔄 Cargar Lista de Empleados
            </button>

            {/* TABLA */}
            {empleados.length > 0 && (
              <div style={{ marginTop: '30px', overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse', minWidth: '600px' }}>
                  <thead>
                    <tr style={{ background: '#f8f9fa', color: '#555', textAlign: 'left' }}>
                      <th style={{ padding: '15px', borderBottom: '2px solid #eee' }}>ID</th>
                      <th style={{ padding: '15px', borderBottom: '2px solid #eee' }}>Nombre</th>
                      <th style={{ padding: '15px', borderBottom: '2px solid #eee' }}>Email</th>
                      <th style={{ padding: '15px', borderBottom: '2px solid #eee' }}>Rol</th>
                      <th style={{ padding: '15px', borderBottom: '2px solid #eee' }}>Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {empleados.map((emp) => (
                      <tr key={emp.id} style={{ borderBottom: '1px solid #eee' }}>
                        <td style={{ padding: '15px', color: '#888' }}>#{emp.id}</td>
                        <td style={{ padding: '15px', fontWeight: 'bold', color: '#333' }}>{emp.nombre}</td>
                        <td style={{ padding: '15px', color: '#555' }}>{emp.email}</td>
                        <td style={{ padding: '15px' }}>
                          <span style={{ 
                            background: emp.role === 'ADMIN' ? '#fceaea' : '#eafaf1', 
                            color: emp.role === 'ADMIN' ? '#c0392b' : '#27ae60',
                            padding: '5px 10px', borderRadius: '15px', fontSize: '12px', fontWeight: 'bold'
                          }}>
                            {emp.role}
                          </span>
                        </td>
                        <td style={{ padding: '15px' }}>
                           <span style={{ color: 'green' }}>● Activo</span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    )
  }

  // --- PANTALLA LOGIN (FUERA) ---
  return (
    <div style={{ width: '100vw', height: '100vh', display: 'flex', justifyContent: 'center', alignItems: 'center', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
      <div style={{ background: 'white', padding: '50px', borderRadius: '15px', boxShadow: '0 10px 25px rgba(0,0,0,0.2)', width: '100%', maxWidth: '400px' }}>
        <h1 style={{ textAlign: 'center', color: '#333', marginBottom: '30px', fontSize: '28px' }}>ERP Lite</h1>
        
        <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '8px', color: '#666', fontSize: '14px' }}>Email Corporativo</label>
            <input 
              type="email" 
              placeholder="ej: ruben@erplite.com" 
              value={email} 
              onChange={e => setEmail(e.target.value)} 
              style={{ width: '100%', padding: '12px', border: '1px solid #ddd', borderRadius: '8px', fontSize: '16px', boxSizing: 'border-box' }}
            />
          </div>
          
          <div>
             <label style={{ display: 'block', marginBottom: '8px', color: '#666', fontSize: '14px' }}>Contraseña</label>
             <input 
              type="password" 
              placeholder="••••••••" 
              value={password} 
              onChange={e => setPassword(e.target.value)} 
              style={{ width: '100%', padding: '12px', border: '1px solid #ddd', borderRadius: '8px', fontSize: '16px', boxSizing: 'border-box' }}
            />
          </div>

          <button type="submit" style={{ padding: '14px', background: '#764ba2', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold', fontSize: '16px', marginTop: '10px', transition: 'background 0.3s' }}>
            Iniciar Sesión
          </button>
        </form>
        
        {error && (
          <div style={{ marginTop: '20px', padding: '10px', background: '#fee', color: '#c0392b', borderRadius: '5px', textAlign: 'center', fontSize: '14px' }}>
            ⚠️ {error}
          </div>
        )}
      </div>
    </div>
  )
}

export default App