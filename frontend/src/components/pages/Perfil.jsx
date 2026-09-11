import { useEffect, useState } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getPerfil, updatePerfil, cambiarCredenciales } from '../../api'

function Perfil() {
  const { token, refreshPerfil } = useOutletContext()
  const [perfil, setPerfil] = useState(null)
  const [form, setForm] = useState(null)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(false)
  const [loading, setLoading] = useState(false)

  const [credenciales, setCredenciales] = useState({ passwordActual: '', nuevoEmail: '', nuevaPassword: '' })
  const [credError, setCredError] = useState(null)
  const [credSuccess, setCredSuccess] = useState(false)
  const [credLoading, setCredLoading] = useState(false)

  useEffect(() => {
    getPerfil(token)
      .then((p) => {
        setPerfil(p)
        setForm({ ...p, tipoTrabajador: p.tipoTrabajador || 'ASALARIADO' })
      })
      .catch((err) => setError(err.message))
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
      const actualizado = await updatePerfil(token, {
        nombre: form.nombre,
        dni: form.dni,
        numeroSeguridadSocial: form.numeroSeguridadSocial,
        tipoTrabajador: form.tipoTrabajador,
        genero: form.genero || null,
        nif: form.nif,
        epigrafeIae: form.epigrafeIae,
        fechaAltaAutonomo: form.fechaAltaAutonomo,
        calle: form.calle,
        numero: form.numero,
        piso: form.piso,
        codigoPostal: form.codigoPostal,
        ciudad: form.ciudad,
        provincia: form.provincia,
        empresaNombre: form.empresaNombre,
        empresaCif: form.empresaCif,
        empresaDireccion: form.empresaDireccion,
        empresaTelefono: form.empresaTelefono,
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

      <form onSubmit={handleSubmit} className="stacked-form">
        <div className="card">
          <h2>Datos personales</h2>
          <div className="field" style={{ marginBottom: 16 }}>
            <label>Nombre completo</label>
            <input {...campo('nombre')} required />
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

          <div className="report-grid" style={{ marginTop: 16 }}>
            <div>
              <span className="muted">Categoría profesional</span>
              <strong>{perfil.categoriaProfesional ?? '—'}</strong>
            </div>
            <div>
              <span className="muted">Salario bruto anual</span>
              <strong>{perfil.salarioBrutoAnual != null ? `${perfil.salarioBrutoAnual.toLocaleString('es-ES')} €` : '—'}</strong>
            </div>
          </div>
          <p className="muted small">La categoría y el salario son de referencia y no se editan desde aquí.</p>
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
          <h2>Empresa</h2>
          <p className="muted small" style={{ marginTop: -8, marginBottom: 16 }}>
            Información de tu empleador, introducida a mano para tener todo en un mismo sitio.
          </p>
          <div className="field-row">
            <div className="field field-grow">
              <label>Nombre de la empresa</label>
              <input {...campo('empresaNombre')} />
            </div>
            <div className="field">
              <label>CIF</label>
              <input {...campo('empresaCif')} />
            </div>
          </div>
          <div className="field-row" style={{ marginTop: 16 }}>
            <div className="field field-grow">
              <label>Dirección</label>
              <input {...campo('empresaDireccion')} />
            </div>
            <div className="field">
              <label>Teléfono</label>
              <input {...campo('empresaTelefono')} />
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
    </div>
  )
}

export default Perfil
