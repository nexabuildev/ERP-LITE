import { API_URL } from './config'

async function request(path, token, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
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

  if (response.status === 204) return null
  return response.json()
}

// --- AUTENTICACIÓN ---

export function login(email, password) {
  return request('/api/v1/auth/login', null, {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })
}

export function register(nombre, email, password) {
  return request('/api/v1/auth/register', null, {
    method: 'POST',
    body: JSON.stringify({ nombre, email, password }),
  })
}

// --- PERFIL ---

export function getPerfil(token) {
  return request('/api/v1/perfil/mio', token)
}

export function updatePerfil(token, data) {
  return request('/api/v1/perfil/mio', token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

// --- FICHAJES ---

export function ficharEntrada(token) {
  return request('/api/v1/fichajes/entrada', token, { method: 'POST' })
}

export function ficharSalida(token) {
  return request('/api/v1/fichajes/salida', token, { method: 'POST' })
}

export function getResumenFichajes(token) {
  return request('/api/v1/fichajes/mios/resumen', token)
}

// --- VACACIONES ---

export function solicitarVacaciones(token, data) {
  return request('/api/v1/vacaciones', token, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function getMisVacaciones(token) {
  return request('/api/v1/vacaciones/mias', token)
}

export function getSaldoVacaciones(token) {
  return request('/api/v1/vacaciones/saldo', token)
}

export function getVacacionesPendientes(token) {
  return request('/api/v1/vacaciones/pendientes', token)
}

export function aprobarVacaciones(token, id) {
  return request(`/api/v1/vacaciones/${id}/aprobar`, token, { method: 'PUT' })
}

export function rechazarVacaciones(token, id) {
  return request(`/api/v1/vacaciones/${id}/rechazar`, token, { method: 'PUT' })
}

// --- NÓMINAS ---

export function getMisNominas(token) {
  return request('/api/v1/nominas/mias', token)
}

// --- TELETRABAJO ---

export function registrarTeletrabajo(token, data) {
  return request('/api/v1/teletrabajo', token, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function getMisRegistrosTeletrabajo(token) {
  return request('/api/v1/teletrabajo/mios', token)
}

// --- REGISTRO RETRIBUTIVO ---

export function getRegistroRetributivo(token) {
  return request('/api/v1/registro-retributivo/mio', token)
}

// --- DECLARACIONES ---

export function getMisDeclaraciones(token) {
  return request('/api/v1/declaraciones/mias', token)
}
