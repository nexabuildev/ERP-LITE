import { API_URL } from './config'

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })

  if (!response.ok) {
    let message = 'Ha ocurrido un error, inténtalo de nuevo'
    try {
      const data = await response.json()
      if (data.message) message = data.message
    } catch {
      // respuesta sin cuerpo JSON
    }
    throw new Error(message)
  }

  return response.json()
}

export function login(email, password) {
  return request('/api/v1/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

export function register(nombre, email, password) {
  return request('/api/v1/auth/register', {
    method: 'POST',
    body: JSON.stringify({ nombre, email, password }),
  })
}

export function getEmpleados(token) {
  return request('/api/v1/empleados', {
    headers: { Authorization: `Bearer ${token}` },
  })
}
