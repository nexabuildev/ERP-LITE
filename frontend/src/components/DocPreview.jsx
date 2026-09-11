import { useEffect, useState } from 'react'

function DocPreview({ tieneArchivo, archivoTipo, cargarUrl, onAbrir }) {
  const [url, setUrl] = useState(null)
  const [fallo, setFallo] = useState(false)

  useEffect(() => {
    if (!tieneArchivo) return undefined
    let vivo = true
    let objectUrl = null

    cargarUrl()
      .then((u) => {
        if (!vivo) {
          URL.revokeObjectURL(u)
          return
        }
        objectUrl = u
        setUrl(u)
      })
      .catch(() => {
        if (vivo) setFallo(true)
      })

    return () => {
      vivo = false
      if (objectUrl) URL.revokeObjectURL(objectUrl)
    }
  }, [tieneArchivo, cargarUrl])

  if (!tieneArchivo) {
    return (
      <div className="doc-preview doc-preview-static">
        <span className="doc-preview-empty">Sin archivo</span>
      </div>
    )
  }

  const esImagen = archivoTipo?.startsWith('image/')

  return (
    <div className="doc-preview" onClick={onAbrir} role="button" tabIndex={0} title="Ver documento completo">
      <div className="doc-preview-frame">
        {!url && !fallo && <span className="doc-preview-empty">Cargando…</span>}
        {fallo && <span className="doc-preview-empty">No disponible</span>}
        {url && esImagen && <img src={url} alt="Vista previa del documento" />}
        {url && !esImagen && <embed src={url} type="application/pdf" />}
      </div>
    </div>
  )
}

export default DocPreview
