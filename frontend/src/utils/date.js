// El backend siempre manda fechas en ISO (yyyy-MM-dd, o yyyy-MM-ddTHH:mm:ss para timestamps).
// Estas funciones son solo para mostrarlas en pantalla en formato dd/mm/aaaa.

export function formatDate(iso) {
  if (!iso) return '—'
  const [fecha] = String(iso).split('T')
  const [anio, mes, dia] = fecha.split('-')
  if (!anio || !mes || !dia) return iso
  return `${dia}/${mes}/${anio}`
}

export function formatDateTime(iso) {
  if (!iso) return '—'
  const [fecha, hora] = String(iso).split('T')
  const fechaFormateada = formatDate(fecha)
  if (!hora) return fechaFormateada
  return `${fechaFormateada} ${hora.slice(0, 5)}`
}

export function hoyISO() {
  return new Date().toISOString().slice(0, 10)
}
