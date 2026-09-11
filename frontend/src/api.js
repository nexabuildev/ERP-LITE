import { API_URL } from './config'

async function request(path, token, options = {}) {
  const isFormData = options.body instanceof FormData

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      ...(isFormData ? {} : { 'Content-Type': 'application/json' }),
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

export function reactivarCuentaLogin(email, password) {
  return request('/api/v1/auth/reactivar', null, {
    method: 'POST',
    body: JSON.stringify({ email, password }),
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

export function cambiarCredenciales(token, data) {
  return request('/api/v1/perfil/credenciales', token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function desactivarCuenta(token, data) {
  return request('/api/v1/perfil/desactivar', token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function reactivarCuentaPropia(token) {
  return request('/api/v1/perfil/reactivar', token, { method: 'PUT' })
}

export function eliminarCuentaDefinitivamente(token, data) {
  return request('/api/v1/perfil/eliminar', token, {
    method: 'DELETE',
    body: JSON.stringify(data),
  })
}

export function getExpedienteExportado(token) {
  return request('/api/v1/perfil/exportar', token)
}

// --- RESUMEN (wrapped y salud administrativa) ---

export function getWrapped(token) {
  return request('/api/v1/resumen/wrapped', token)
}

export function getSaludAdministrativa(token) {
  return request('/api/v1/resumen/salud', token)
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

export function editarSolicitudVacaciones(token, id, data) {
  return request(`/api/v1/vacaciones/${id}`, token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function getMisVacaciones(token) {
  return request('/api/v1/vacaciones/mias', token)
}

export function getSaldoVacaciones(token) {
  return request('/api/v1/vacaciones/saldo', token)
}

export function ajustarVacaciones(token, data) {
  return request('/api/v1/vacaciones/ajustes', token, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function getDesgloseVacaciones(token) {
  return request('/api/v1/vacaciones/desglose', token)
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

export function subirNomina(token, formData) {
  return request('/api/v1/nominas', token, {
    method: 'POST',
    body: formData,
  })
}

export function eliminarNomina(token, id) {
  return request(`/api/v1/nominas/${id}`, token, { method: 'DELETE' })
}

export async function abrirArchivoNomina(token, id) {
  const response = await fetch(`${API_URL}/api/v1/nominas/${id}/archivo`, {
    headers: { Authorization: `Bearer ${token}` },
  })
  if (!response.ok) throw new Error('No se pudo cargar el archivo')
  const blob = await response.blob()
  return URL.createObjectURL(blob)
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

export function registrarDeclaracionPresentada(token, formData) {
  return request('/api/v1/declaraciones/presentadas', token, {
    method: 'POST',
    body: formData,
  })
}

export async function abrirArchivoDeclaracion(token, id) {
  const response = await fetch(`${API_URL}/api/v1/declaraciones/presentadas/${id}/archivo`, {
    headers: { Authorization: `Bearer ${token}` },
  })
  if (!response.ok) throw new Error('No se pudo cargar el archivo')
  const blob = await response.blob()
  return URL.createObjectURL(blob)
}

export function getMisDeclaracionesPresentadas(token) {
  return request('/api/v1/declaraciones/presentadas', token)
}

export function eliminarDeclaracionPresentada(token, id) {
  return request(`/api/v1/declaraciones/presentadas/${id}`, token, { method: 'DELETE' })
}

// --- CUENTAS BANCARIAS ---

export function anadirCuentaBancaria(token, data) {
  return request('/api/v1/cuentas-bancarias', token, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function getMisCuentasBancarias(token) {
  return request('/api/v1/cuentas-bancarias/mias', token)
}

export function getResumenCuentas(token) {
  return request('/api/v1/cuentas-bancarias/resumen', token)
}

export function editarCuentaBancaria(token, id, data) {
  return request(`/api/v1/cuentas-bancarias/${id}`, token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function eliminarCuentaBancaria(token, id) {
  return request(`/api/v1/cuentas-bancarias/${id}`, token, { method: 'DELETE' })
}

// --- AHORROS ---

export function crearMetaAhorro(token, data) {
  return request('/api/v1/ahorros', token, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function getMisMetasAhorro(token) {
  return request('/api/v1/ahorros/mias', token)
}

export function editarMetaAhorro(token, id, data) {
  return request(`/api/v1/ahorros/${id}`, token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function aportarAhorro(token, id, monto) {
  return request(`/api/v1/ahorros/${id}/aportar`, token, {
    method: 'PUT',
    body: JSON.stringify({ monto }),
  })
}

export function retirarAhorro(token, id, monto) {
  return request(`/api/v1/ahorros/${id}/retirar`, token, {
    method: 'PUT',
    body: JSON.stringify({ monto }),
  })
}

export function eliminarMetaAhorro(token, id) {
  return request(`/api/v1/ahorros/${id}`, token, { method: 'DELETE' })
}

// --- MOVIMIENTOS (pagos) ---

export function registrarMovimiento(token, data) {
  return request('/api/v1/movimientos', token, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function editarMovimiento(token, id, data) {
  return request(`/api/v1/movimientos/${id}`, token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function eliminarMovimiento(token, id) {
  return request(`/api/v1/movimientos/${id}`, token, { method: 'DELETE' })
}

export function getResumenMovimientos(token, { mes, anio } = {}) {
  const params = new URLSearchParams()
  if (mes) params.set('mes', mes)
  if (anio) params.set('anio', anio)
  const query = params.toString()
  return request(`/api/v1/movimientos/mios/resumen${query ? `?${query}` : ''}`, token)
}

export function getHistorialMovimientos(token) {
  return request('/api/v1/movimientos/mios/historial', token)
}

export function getIncrementosGastos(token) {
  return request('/api/v1/movimientos/mios/incrementos', token)
}

// --- SIMULADOR DE NÓMINA ---

export function simularNomina(token, nuevoSalarioBrutoAnual) {
  return request('/api/v1/simulador/nomina', token, {
    method: 'POST',
    body: JSON.stringify({ nuevoSalarioBrutoAnual }),
  })
}

// --- ALERTAS (renovaciones y caducidades) ---

export function crearAlerta(token, data) {
  return request('/api/v1/alertas', token, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function editarAlerta(token, id, data) {
  return request(`/api/v1/alertas/${id}`, token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function eliminarAlerta(token, id) {
  return request(`/api/v1/alertas/${id}`, token, { method: 'DELETE' })
}

export function getResumenAlertas(token) {
  return request('/api/v1/alertas/mias/resumen', token)
}

// --- MÉTODOS DE PAGO (tarjetas / PayPal) ---

export function crearMetodoPago(token, data) {
  return request('/api/v1/metodos-pago', token, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function getMisMetodosPago(token) {
  return request('/api/v1/metodos-pago/mios', token)
}

export function editarMetodoPago(token, id, data) {
  return request(`/api/v1/metodos-pago/${id}`, token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function eliminarMetodoPago(token, id) {
  return request(`/api/v1/metodos-pago/${id}`, token, { method: 'DELETE' })
}

export function getMetodoPagoSensible(token, id) {
  return request(`/api/v1/metodos-pago/${id}/sensible`, token)
}

// --- EMPRESAS (historial laboral) ---

export function crearEmpresa(token, data) {
  return request('/api/v1/empresas', token, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function getMisEmpresas(token) {
  return request('/api/v1/empresas/mias', token)
}

export function editarEmpresa(token, id, data) {
  return request(`/api/v1/empresas/${id}`, token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function eliminarEmpresa(token, id) {
  return request(`/api/v1/empresas/${id}`, token, { method: 'DELETE' })
}

// --- PARO (desempleo) ---

export function crearParo(token, data) {
  return request('/api/v1/paro', token, {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

export function getMisPeriodosParo(token) {
  return request('/api/v1/paro/mias', token)
}

export function editarParo(token, id, data) {
  return request(`/api/v1/paro/${id}`, token, {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

export function eliminarParo(token, id) {
  return request(`/api/v1/paro/${id}`, token, { method: 'DELETE' })
}
