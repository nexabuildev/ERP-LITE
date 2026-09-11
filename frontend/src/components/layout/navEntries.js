export const NAV_ENTRIES = [
  { type: 'link', to: '/', label: 'Resumen', num: '00', end: true },
  { type: 'link', to: '/alertas', label: 'Alertas', num: '01' },
  {
    type: 'group',
    id: 'trabajo',
    label: 'Trabajo',
    items: [
      { to: '/fichajes', label: 'Fichajes', num: '02' },
      { to: '/vacaciones', label: 'Vacaciones', num: '03' },
      { to: '/nominas', label: 'Nóminas', num: '04' },
      { to: '/registro-retributivo', label: 'Registro retributivo', num: '05' },
      { to: '/declaraciones', label: 'Declaraciones', num: '06' },
      { to: '/teletrabajo', label: 'Teletrabajo', num: '07' },
      { to: '/empresas', label: 'Empresas', num: '08' },
      { to: '/paro', label: 'Paro', num: '09' },
    ],
  },
  {
    type: 'group',
    id: 'dinero',
    label: 'Dinero',
    items: [
      { to: '/movimientos', label: 'Movimientos', num: '10' },
      { to: '/ahorros', label: 'Ahorros', num: '11' },
      { to: '/cuentas-bancarias', label: 'Cuentas y efectivo', num: '12' },
      { to: '/metodos-pago', label: 'Métodos de pago', num: '13' },
      { to: '/simulador', label: 'Simulador de sueldo', num: '14' },
    ],
  },
  { type: 'link', to: '/perfil', label: 'Perfil', num: '15' },
]

export function grupoDeRuta(pathname) {
  for (const entry of NAV_ENTRIES) {
    if (entry.type === 'group' && entry.items.some((item) => pathname.startsWith(item.to))) {
      return entry.id
    }
  }
  return null
}
