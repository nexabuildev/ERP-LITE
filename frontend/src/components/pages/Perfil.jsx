import { useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import {
  getPerfil,
  updatePerfil,
  cambiarCredenciales,
  desactivarCuenta,
  reactivarCuentaPropia,
  eliminarCuentaDefinitivamente,
  getResumenCuentas,
  getExpedienteExportado,
} from '../../api'

const PALABRA_CONFIRMACION = 'DELETE-CUENTA'

function Perfil() {
  const { token, refreshPerfil } = useOutletContext()
  const [perfil, setPerfil] = useState(null)
  const [form, setForm] = useState(null)
  const [resumenCuentas, setResumenCuentas] = useState(null)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)

  const [credenciales, setCredenciales] = useState({ passwordActual: '', nuevoEmail: '', nuevaPassword: '' })
  const [credError, setCredError] = useState(null)
  const [credSuccess, setCredSuccess] = useState(false)
  const [credLoading, setCredLoading] = useState(false)

  const [zonaPeligro, setZonaPeligro] = useState({ confirmacion: '', passwordActual: '' })
  const [zonaPeligroError, setZonaPeligroError] = useState(null)
  const [accionEnCurso, setAccionEnCurso] = useState(null) // 'desactivar' | 'eliminar' | 'reactivar' | null

  const [exportando, setExportando] = useState(false)
  const [exportError, setExportError] = useState(null)

  useEffect(() => {
    getPerfil(token)
      .then((p) => {
        setPerfil(p)
        setForm({ ...p, tipoTrabajador: p.tipoTrabajador || 'ASALARIADO' })
      })
      .catch((err) => setError(err.message))
    getResumenCuentas(token).then(setResumenCuentas).catch(() => {})
  }, [token])

  if (!form) {
    return error ? <div className="alert alert-error">⚠️ {error}</div> : <p className="empty-state">Cargando...</p>
  }

  const campo = (key) => ({
    value: form[key] || '',
    onChange: (e) => setForm({ ...form, [key]: e.target.value }),
  })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setSuccess(false)
    setLoading(true)
    try {
      const nombreCompleto = [form.nombre1, form.nombre2, form.apellidos].filter(Boolean).join(' ').trim() || form.nombre
      const actualizado = await updatePerfil(token, {
        nombre: nombreCompleto,
        nombre1: form.nombre1,
        nombre2: form.nombre2,
        apellidos: form.apellidos,
        dni: form.dni,
        numeroSeguridadSocial: form.numeroSeguridadSocial,
        tipoTrabajador: form.tipoTrabajador,
        genero: form.genero || null,
        categoriaProfesional: form.categoriaProfesional,
        salarioBrutoAnual: form.salarioBrutoAnual !== '' && form.salarioBrutoAnual != null ? Number(form.salarioBrutoAnual) : null,
        nif: form.nif,
        epigrafeIae: form.epigrafeIae,
        fechaAltaAutonomo: form.fechaAltaAutonomo,
        calle: form.calle,
        numero: form.numero,
        piso: form.piso,
        codigoPostal: form.codigoPostal,
        ciudad: form.ciudad,
        provincia: form.provincia,
        calle2: form.calle2,
        numero2: form.numero2,
        piso2: form.piso2,
        codigoPostal2: form.codigoPostal2,
        ciudad2: form.ciudad2,
        provincia2: form.provincia2,
        grupoSanguineo: form.grupoSanguineo,
        alergias: form.alergias,
        contactoEmergenciaNombre: form.contactoEmergenciaNombre,
        contactoEmergenciaTelefono: form.contactoEmergenciaTelefono,
        seguroMedico: form.seguroMedico,
      })
      setPerfil(actualizado)
      setForm(actualizado)
      setSuccess(true)
      refreshPerfil?.()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleCredenciales = async (e) => {
    e.preventDefault()
    setCredError(null)
    setCredSuccess(false)
    setCredLoading(true)
    try {
      const resultado = await cambiarCredenciales(token, {
        passwordActual: credenciales.passwordActual,
        nuevoEmail: credenciales.nuevoEmail || null,
        nuevaPassword: credenciales.nuevaPassword || null,
      })
      localStorage.setItem('token', resultado.token)
      setPerfil(resultado.perfil)
      setForm(resultado.perfil)
      setCredenciales({ passwordActual: '', nuevoEmail: '', nuevaPassword: '' })
      setCredSuccess(true)
      // El token cambió: recargamos para que toda la app use el nuevo JWT.
      setTimeout(() => window.location.reload(), 900)
    } catch (err) {
      setCredError(err.message)
    } finally {
      setCredLoading(false)
    }
  }

  const handleDesactivar = async (e) => {
    e.preventDefault()
    setZonaPeligroError(null)
    setAccionEnCurso('desactivar')
    try {
      await desactivarCuenta(token, zonaPeligro)
      setZonaPeligro({ confirmacion: '', passwordActual: '' })
      refreshPerfil?.()
      setPerfil((p) => ({ ...p, activa: false }))
    } catch (err) {
      setZonaPeligroError(err.message)
    } finally {
      setAccionEnCurso(null)
    }
  }

  const handleReactivar = async () => {
    setZonaPeligroError(null)
    setAccionEnCurso('reactivar')
    try {
      await reactivarCuentaPropia(token)
      refreshPerfil?.()
      setPerfil((p) => ({ ...p, activa: true }))
    } catch (err) {
      setZonaPeligroError(err.message)
    } finally {
      setAccionEnCurso(null)
    }
  }

  const handleEliminar = async (e) => {
    e.preventDefault()
    setZonaPeligroError(null)
    setAccionEnCurso('eliminar')
    try {
      await eliminarCuentaDefinitivamente(token, zonaPeligro)
      localStorage.removeItem('token')
      window.location.reload()
    } catch (err) {
      setZonaPeligroError(err.message)
      setAccionEnCurso(null)
    }
  }

  const handleExportar = async () => {
    setExportError(null)
    setExportando(true)
    try {
      const expediente = await getExpedienteExportado(token)
      const blob = new Blob([JSON.stringify(expediente, null, 2)], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const enlace = document.createElement('a')
      enlace.href = url
      enlace.download = `expediente-ziviko-${new Date().toISOString().slice(0, 10)}.json`
      document.body.appendChild(enlace)
      enlace.click()
      enlace.remove()
      URL.revokeObjectURL(url)
    } catch (err) {
      setExportError(err.message)
    } finally {
      setExportando(false)
    }
  }

  const esAutonomo = form.tipoTrabajador === 'AUTONOMO'

  return (
    <div>
      <header className="page-header">
        <span className="page-eyebrow">Expediente Nº {perfil.id}</span>
        <h1>Mi perfil</h1>
        <p>{perfil.email}</p>
      </header>

      {error && <div className="alert alert-error">⚠️ {error}</div>}
      {success && <div className="alert alert-success">Perfil actualizado correctamente</div>}

      {resumenCuentas && (
        <div className="card">
          <h2>Todo tu dinero</h2>
          <div className="stats-grid stats-grid-compact">
            <div className="stat-block">
              <span className="stat-block-label">Total</span>
              <span className="stat-block-value">{resumenCuentas.totalGeneral.toFixed(2)} €</span>
            </div>
            <div className="stat-block">
              <span className="stat-block-label">En bancos</span>
              <span className="stat-block-value">{resumenCuentas.totalBanco.toFixed(2)} €</span>
            </div>
            <div className="stat-block">
              <span className="stat-block-label">En efectivo</span>
              <span className="stat-block-value">{resumenCuentas.totalEfectivo.toFixed(2)} €</span>
            </div>
            <div className="stat-block">
              <span className="stat-block-label">En PayPal</span>
              <span className="stat-block-value">{resumenCuentas.totalPaypal.toFixed(2)} €</span>
            </div>
          </div>
        </div>
      )}

      {perfil.activa === false && (
        <div className="card card-highlight">
          <div className="card-header">
            <div>
              <h2>Tu cuenta está desactivada temporalmente</h2>
              <p>Nadie más puede iniciar sesión en ella, pero tus datos siguen aquí. Puedes reactivarla cuando quieras.</p>
            </div>
            <button className="btn-primary" disabled={accionEnCurso === 'reactivar'} onClick={handleReactivar}>
              {accionEnCurso === 'reactivar' ? 'Reactivando...' : 'Reactivar cuenta'}
            </button>
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit} className="stacked-form">
        <div className="card">
          <h2>Datos personales</h2>
          <div className="field-row" style={{ marginBottom: 16 }}>
            <div className="field field-grow">
              <label>Nombre</label>
              <input {...campo('nombre1')} required />
            </div>
            <div className="field field-grow">
              <label>Segundo nombre (opcional)</label>
              <input {...campo('nombre2')} />
            </div>
            <div className="field field-grow">
              <label>Apellidos</label>
              <input {...campo('apellidos')} required />
            </div>
          </div>
          <div className="field-row">
            <div className="field">
              <label>DNI</label>
              <input {...campo('dni')} placeholder="12345678A" />
            </div>
            <div className="field">
              <label>Nº Seguridad Social</label>
              <input {...campo('numeroSeguridadSocial')} placeholder="281234567890" />
            </div>
            <div className="field">
              <label>Tipo de trabajador</label>
              <select value={form.tipoTrabajador || 'ASALARIADO'} onChange={(e) => setForm({ ...form, tipoTrabajador: e.target.value })}>
                <option value="ASALARIADO">Asalariado</option>
                <option value="AUTONOMO">Autónomo / cuenta propia</option>
                <option value="DESEMPLEADO">Desempleado</option>
                <option value="ESTUDIANTE">Estudiante</option>
              </select>
            </div>
            <div className="field">
              <label>Género (registro retributivo)</label>
              <select value={form.genero || ''} onChange={(e) => setForm({ ...form, genero: e.target.value })}>
                <option value="">Prefiero no decirlo</option>
                <option value="HOMBRE">Hombre</option>
                <option value="MUJER">Mujer</option>
                <option value="OTRO">Otro</option>
              </select>
            </div>
          </div>

          {esAutonomo && (
            <div className="field-row" style={{ marginTop: 16 }}>
              <div className="field">
                <label>NIF</label>
                <input {...campo('nif')} />
              </div>
              <div className="field">
                <label>Epígrafe IAE</label>
                <input {...campo('epigrafeIae')} />
              </div>
              <div className="field">
                <label>Fecha de alta</label>
                <input type="date" {...campo('fechaAltaAutonomo')} />
              </div>
            </div>
          )}

          <div className="field-row" style={{ marginTop: 16 }}>
            <div className="field field-grow">
              <label>Categoría profesional</label>
              <input {...campo('categoriaProfesional')} placeholder="Ej: Analista, Técnico..." />
            </div>
            <div className="field">
              <label>Salario bruto anual (€)</label>
              <input type="number" step="0.01" {...campo('salarioBrutoAnual')} />
            </div>
          </div>
        </div>

        <div className="card">
          <h2>Dirección</h2>
          <div className="field-row">
            <div className="field field-grow">
              <label>Calle</label>
              <input {...campo('calle')} placeholder="Calle Mayor" />
            </div>
            <div className="field">
              <label>Número</label>
              <input {...campo('numero')} placeholder="12" />
            </div>
            <div className="field">
              <label>Piso / puerta</label>
              <input {...campo('piso')} placeholder="3ºB" />
            </div>
          </div>
          <div className="field-row" style={{ marginTop: 16 }}>
            <div className="field">
              <label>Código postal</label>
              <input {...campo('codigoPostal')} placeholder="28001" />
            </div>
            <div className="field field-grow">
              <label>Ciudad</label>
              <input {...campo('ciudad')} placeholder="Madrid" />
            </div>
            <div className="field field-grow">
              <label>Provincia</label>
              <input {...campo('provincia')} placeholder="Madrid" />
            </div>
          </div>
        </div>

        <div className="card">
          <h2>Segunda vivienda (opcional)</h2>
          <div className="field-row">
            <div className="field field-grow">
              <label>Calle</label>
              <input {...campo('calle2')} placeholder="Calle Mayor" />
            </div>
            <div className="field">
              <label>Número</label>
              <input {...campo('numero2')} placeholder="12" />
            </div>
            <div className="field">
              <label>Piso / puerta</label>
              <input {...campo('piso2')} placeholder="3ºB" />
            </div>
          </div>
          <div className="field-row" style={{ marginTop: 16 }}>
            <div className="field">
              <label>Código postal</label>
              <input {...campo('codigoPostal2')} placeholder="28001" />
            </div>
            <div className="field field-grow">
              <label>Ciudad</label>
              <input {...campo('ciudad2')} placeholder="Madrid" />
            </div>
            <div className="field field-grow">
              <label>Provincia</label>
              <input {...campo('provincia2')} placeholder="Madrid" />
            </div>
          </div>
        </div>

        <div className="card">
          <h2>Ficha médica de emergencia</h2>
          <div className="field-row">
            <div className="field">
              <label>Grupo sanguíneo</label>
              <input {...campo('grupoSanguineo')} placeholder="0+" />
            </div>
            <div className="field field-grow">
              <label>Alergias</label>
              <input {...campo('alergias')} placeholder="Ninguna conocida" />
            </div>
            <div className="field field-grow">
              <label>Seguro médico</label>
              <input {...campo('seguroMedico')} />
            </div>
          </div>
          <div className="field-row" style={{ marginTop: 16 }}>
            <div className="field field-grow">
              <label>Contacto de emergencia</label>
              <input {...campo('contactoEmergenciaNombre')} placeholder="Nombre" />
            </div>
            <div className="field field-grow">
              <label>Teléfono de emergencia</label>
              <input {...campo('contactoEmergenciaTelefono')} />
            </div>
          </div>
        </div>

        <button className="btn-primary" disabled={loading}>
          {loading ? 'Guardando...' : 'Guardar cambios'}
        </button>
      </form>

      <div className="card" style={{ marginTop: 24 }}>
        <h2>Seguridad: email y contraseña</h2>
        {credError && <div className="alert alert-error">⚠️ {credError}</div>}
        {credSuccess && <div className="alert alert-success">Credenciales actualizadas, recargando...</div>}
        <form onSubmit={handleCredenciales} className="stacked-form">
          <div className="field">
            <label>Contraseña actual (obligatoria para confirmar)</label>
            <input
              type="password"
              value={credenciales.passwordActual}
              onChange={(e) => setCredenciales({ ...credenciales, passwordActual: e.target.value })}
              required
            />
          </div>
          <div className="field-row">
            <div className="field field-grow">
              <label>Nuevo email (opcional)</label>
              <input
                type="email"
                value={credenciales.nuevoEmail}
                onChange={(e) => setCredenciales({ ...credenciales, nuevoEmail: e.target.value })}
                placeholder={perfil.email}
              />
            </div>
            <div className="field field-grow">
              <label>Nueva contraseña (opcional)</label>
              <input
                type="password"
                value={credenciales.nuevaPassword}
                onChange={(e) => setCredenciales({ ...credenciales, nuevaPassword: e.target.value })}
                placeholder="••••••••"
              />
            </div>
          </div>
          <button className="btn-primary" disabled={credLoading}>
            {credLoading ? 'Guardando...' : 'Actualizar credenciales'}
          </button>
        </form>
      </div>

      <div className="card" style={{ marginTop: 24 }}>
        <h2>Descarga tu expediente</h2>
        <p className="muted small" style={{ marginTop: -8 }}>
          Un archivo JSON con todos tus datos: perfil, cuentas, movimientos, nóminas, vacaciones, declaraciones y más.
        </p>
        {exportError && <div className="alert alert-error">⚠️ {exportError}</div>}
        <button type="button" className="btn-small" onClick={handleExportar} disabled={exportando}>
          {exportando ? 'Generando...' : 'Descargar mi expediente (JSON)'}
        </button>
      </div>

      <div className="card" style={{ marginTop: 24, borderColor: 'var(--accent)' }}>
        <h2>Zona peligrosa</h2>
        <p className="muted small">
          Escribe exactamente <strong className="mono">{PALABRA_CONFIRMACION}</strong> y tu contraseña para confirmar cualquiera de estas dos acciones.
        </p>

        {zonaPeligroError && <div className="alert alert-error">⚠️ {zonaPeligroError}</div>}

        <div className="field-row" style={{ marginTop: 12, marginBottom: 20 }}>
          <div className="field">
            <label>Escribe "{PALABRA_CONFIRMACION}"</label>
            <input
              type="text"
              value={zonaPeligro.confirmacion}
              onChange={(e) => setZonaPeligro({ ...zonaPeligro, confirmacion: e.target.value })}
              placeholder={PALABRA_CONFIRMACION}
            />
          </div>
          <div className="field">
            <label>Tu contraseña</label>
            <input
              type="password"
              value={zonaPeligro.passwordActual}
              onChange={(e) => setZonaPeligro({ ...zonaPeligro, passwordActual: e.target.value })}
            />
          </div>
        </div>

        <div className="inline-form">
          <button
            type="button"
            className="btn-small"
            disabled={accionEnCurso === 'desactivar' || perfil.activa === false}
            onClick={handleDesactivar}
          >
            {accionEnCurso === 'desactivar' ? 'Desactivando...' : 'Eliminar temporalmente (desactivar)'}
          </button>
          <button
            type="button"
            className="btn-small btn-danger"
            disabled={accionEnCurso === 'eliminar'}
            onClick={handleEliminar}
          >
            {accionEnCurso === 'eliminar' ? 'Eliminando...' : 'Eliminar definitivamente'}
          </button>
        </div>
        <p className="muted small" style={{ marginTop: 12 }}>
          <strong>Temporal</strong>: nadie puede iniciar sesión hasta que la reactives (puedes hacerlo volviendo a
          intentar el login). <strong>Definitiva</strong>: borra tu cuenta y todos tus datos sin posibilidad de
          recuperarlos.
        </p>
      </div>
    </div>
  )
}

export default Perfil
