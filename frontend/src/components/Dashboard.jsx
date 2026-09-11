import { useState } from 'react'
import { getEmpleados } from '../api'

function Dashboard({ token, onLogout }) {
  const [empleados, setEmpleados] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [loaded, setLoaded] = useState(false)

  const cargarEmpleados = async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await getEmpleados(token)
      setEmpleados(data)
      setLoaded(true)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const totalAdmins = empleados.filter((e) => e.role === 'ADMIN').length

  return (
    <div className="dashboard">
      <nav className="navbar">
        <h2>🚀 ERP Lite</h2>
        <button className="btn-ghost" onClick={onLogout}>Cerrar sesión</button>
      </nav>

      <main className="dashboard-content">
        <div className="stats-row">
          <div className="stat-card">
            <span className="stat-label">Empleados cargados</span>
            <span className="stat-value">{empleados.length}</span>
          </div>
          <div className="stat-card">
            <span className="stat-label">Administradores</span>
            <span className="stat-value">{totalAdmins}</span>
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <div>
              <h1>Gestión de empleados</h1>
              <p>Consulta el listado de empleados desde la base de datos.</p>
            </div>
            <button className="btn-primary" onClick={cargarEmpleados} disabled={loading}>
              {loading ? 'Cargando...' : '🔄 Cargar empleados'}
            </button>
          </div>

          {error && <div className="alert alert-error">⚠️ {error}</div>}

          {loaded && empleados.length === 0 && !error && (
            <p className="empty-state">No hay empleados registrados todavía.</p>
          )}

          {empleados.length > 0 && (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Email</th>
                    <th>Rol</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  {empleados.map((emp) => (
                    <tr key={emp.id}>
                      <td className="muted">#{emp.id}</td>
                      <td className="bold">{emp.nombre}</td>
                      <td className="muted">{emp.email}</td>
                      <td>
                        <span className={`badge ${emp.role === 'ADMIN' ? 'badge-admin' : 'badge-user'}`}>
                          {emp.role}
                        </span>
                      </td>
                      <td><span className="status-dot" />Activo</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>
    </div>
  )
}

export default Dashboard
