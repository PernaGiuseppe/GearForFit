import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import { BsStar, BsStarFill, BsPencilFill } from 'react-icons/bs'
import '../../css/Schede.css'
import { GiConfirmed } from 'react-icons/gi'
import { MdClose } from 'react-icons/md'

type SerieDTOBackend = {
  id: number
  esercizioId: number
  nomeEsercizio: string
  numeroSerie: number
  numeroRipetizioni: number
  tempoRecuperoSecondi: number
  peso?: string | null
}

type EsercizioScheda = SerieDTOBackend

type GiornoAllenamento = {
  id: number
  giornoSettimana: string
  serie: EsercizioScheda[]
}

type SchedaDettaglioDTO = {
  id: number
  nome: string
  descrizione?: string
  obiettivo: string
  isStandard: boolean
  attiva?: boolean
  durataSettimane?: number
  giorni: GiornoAllenamento[]
  utenteId?: number
}

type EditingState = {
  giornoId: number | null
  serieId: number | null
  newWeight: string
}

export default function SchedaDettaglio() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const user = useSelector((s: RootState) => s.auth.user)

  const [scheda, setScheda] = useState<SchedaDettaglioDTO | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [editing, setEditing] = useState<EditingState>({
    giornoId: null,
    serieId: null,
    newWeight: '',
  })

  const [updateMessage, setUpdateMessage] = useState<{
    type: 'success' | 'error'
    text: string
  } | null>(null)

  useEffect(() => {
    if (!id || !user) return

    setLoading(true)
    setError(null)

    const endpoint =
      user.tipoUtente === 'ADMIN'
        ? `${API_BASE_URL}/admin/schede/${id}`
        : `${API_BASE_URL}/schede-allenamento/${id}`

    fetch(endpoint, { headers: getAuthHeader() })
      .then((res) => {
        if (!res.ok) throw new Error('Errore nel caricamento della scheda')
        return res.json()
      })
      .then((data) => setScheda(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id, user])

  const handleToggleAttiva = async (e: React.MouseEvent) => {
    e.preventDefault()
    e.stopPropagation()

    if (!scheda || scheda.isStandard) return

    try {
      const res = await fetch(
        `${API_BASE_URL}/schede-allenamento/me/schede/${scheda.id}/attiva`,
        {
          method: 'PUT',
          headers: getAuthHeader(),
        }
      )

      if (res.ok) {
        setScheda((prev) => (prev ? { ...prev, attiva: !prev.attiva } : null))
      } else {
        alert("Errore durante l'aggiornamento")
      }
    } catch (err) {
      console.error('Errore toggle:', err)
      alert('Errore di connessione')
    }
  }

  const handleDelete = async () => {
    if (!scheda) return

    if (
      !window.confirm(
        `Sei sicuro di voler eliminare la scheda "${scheda.nome}"?`
      )
    ) {
      return
    }

    try {
      const endpoint =
        user?.tipoUtente === 'ADMIN'
          ? `${API_BASE_URL}/admin/schede/${scheda.id}`
          : `${API_BASE_URL}/schede-allenamento/me/${scheda.id}`

      const res = await fetch(endpoint, {
        method: 'DELETE',
        headers: getAuthHeader(),
      })

      if (res.ok || res.status === 204) {
        alert('Scheda eliminata con successo')
        navigate('/schede')
      } else {
        alert("Errore durante l'eliminazione della scheda")
      }
    } catch (err) {
      console.error('Errore delete:', err)
      alert('Errore di connessione')
    }
  }

  const handleWeightUpdate = async (
    serie: EsercizioScheda,
    newWeight: string
  ) => {
    if (!scheda || scheda.isStandard) return
    setUpdateMessage(null)

    try {
      const res = await fetch(
        `${API_BASE_URL}/schede-allenamento/me/schede/${scheda.id}/serie/${serie.id}/peso`,
        {
          method: 'PATCH',
          headers: {
            ...getAuthHeader(),
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ peso: newWeight }),
        }
      )

      if (res.ok) {
        const updatedSerie: SerieDTOBackend = await res.json()

        setScheda((prevScheda) => {
          if (!prevScheda) return null

          const updatedGiorni = prevScheda.giorni.map((giorno) => {
            const updatedSerieList = giorno.serie.map((s) =>
              s.id === updatedSerie.id ? { ...s, peso: updatedSerie.peso } : s
            )
            return { ...giorno, serie: updatedSerieList }
          })

          return { ...prevScheda, giorni: updatedGiorni }
        })
        setEditing({ giornoId: null, serieId: null, newWeight: '' })
        setUpdateMessage({
          type: 'success',
          text: `Peso aggiornato a ${updatedSerie.peso}`,
        })

        setTimeout(() => setUpdateMessage(null), 3000)
      } else {
        throw new Error("Errore durante l'aggiornamento del peso")
      }
    } catch (err) {
      console.error('Errore PATCH peso:', err)
      setUpdateMessage({
        type: 'error',
        text: 'Errore di connessione o autorizzazione',
      })
    }
  }

  const renderWeightCell = (giornoId: number, serie: EsercizioScheda) => {
    const isEditing =
      editing.giornoId === giornoId && editing.serieId === serie.id

    const displayedWeight =
      serie.peso && serie.peso.trim() !== '' ? serie.peso : '---'

    // Mostra solo la visualizzazione se è admin o scheda standard
    const canEdit = user?.tipoUtente !== 'ADMIN' && !scheda?.isStandard

    return (
      <div style={{ position: 'relative', minHeight: '30px' }}>
        {/* Contenuto sempre visibile (non cambia mai di dimensione) */}
        <div className="d-flex align-items-center justify-content-center">
          <span className="me-2">{displayedWeight}</span>
          {canEdit && (
            <BsPencilFill
              className="text-primary"
              title="Modifica peso"
              onClick={() =>
                setEditing({
                  giornoId,
                  serieId: serie.id,
                  newWeight: serie.peso || '',
                })
              }
              style={{ cursor: 'pointer', fontSize: '0.9em' }}
            />
          )}
        </div>

        {/* Popover di editing che si sovrappone */}
        {isEditing && (
          <div id="input-cambiopeso" onClick={(e) => e.stopPropagation()}>
            <div className="d-flex align-items-center gap-2">
              <input
                type="text"
                className="form-control form-control-sm text-center"
                style={{ width: '90px' }}
                value={editing.newWeight}
                onChange={(e) =>
                  setEditing({ ...editing, newWeight: e.target.value })
                }
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    handleWeightUpdate(serie, editing.newWeight)
                  }
                  if (e.key === 'Escape') {
                    setEditing({ giornoId: null, serieId: null, newWeight: '' })
                  }
                }}
                placeholder="kg"
                autoFocus
              />
              <GiConfirmed
                className="text-success"
                title="Conferma peso"
                onClick={() => handleWeightUpdate(serie, editing.newWeight)}
                style={{ cursor: 'pointer', fontSize: '1.5em' }}
              />
              <MdClose
                className="text-danger"
                title="Annulla"
                onClick={() =>
                  setEditing({ giornoId: null, serieId: null, newWeight: '' })
                }
                style={{ cursor: 'pointer', fontSize: '1.5em' }}
              />
            </div>
            <small className="text-muted d-block mt-1 text-center">
              Premi Enter per confermare
            </small>
          </div>
        )}
      </div>
    )
  }

  if (loading)
    return (
      <div className="container mt-5 text-center page-content-custom">
        <div className="spinner-border text-primary" role="status"></div>
      </div>
    )
  if (error)
    return (
      <div className="container mt-5 page-content-custom">
        <div className="alert alert-danger">{error}</div>
      </div>
    )
  if (!scheda)
    return (
      <div className="container mt-5 page-content-custom">
        <div className="alert alert-warning">Scheda non trovata.</div>
      </div>
    )

  return (
    <div className="container py-4">
      <div className="row align-items-center mb-4">
        <div className="col-12 col-md-8 d-flex align-items-center">
          <h1 className="fw-bold mb-2 me-3">{scheda.nome}</h1>
          {scheda && !scheda.isStandard && user?.tipoUtente !== 'ADMIN' && (
            <>
              {scheda.attiva ? (
                <BsStarFill
                  className="stella-dettaglio star-active"
                  onClick={handleToggleAttiva}
                  title="Scheda attiva - Clicca per disattivare"
                  style={{ cursor: 'pointer' }}
                />
              ) : (
                <BsStar
                  className="stella-dettaglio star-inactive"
                  onClick={handleToggleAttiva}
                  title="Clicca per attivare questa scheda"
                  style={{ cursor: 'pointer' }}
                />
              )}
            </>
          )}
        </div>
        <div className="col-12 col-lg-4 text-lg-end mt-3 mt-lg-0">
          {(user?.tipoUtente === 'ADMIN' || !scheda?.isStandard) && (
            <button
              className="btn btn-outline-danger me-2"
              onClick={handleDelete}
            >
              Elimina
            </button>
          )}

          <Link to="/schede" className="btn btn-outline-secondary">
            Torna alle schede
          </Link>
        </div>
      </div>

      {updateMessage && (
        <div
          className={`alert alert-${
            updateMessage.type === 'success' ? 'success' : 'danger'
          } alert-dismissible fade show`}
          role="alert"
        >
          {updateMessage.text}
          <button
            type="button"
            className="btn-close"
            onClick={() => setUpdateMessage(null)}
          ></button>
        </div>
      )}

      <div className="row">
        <div className="col-12">
          <p className="text-muted">Dettaglio del programma di allenamento</p>
        </div>
      </div>

      <div className="row mb-4">
        <div className="col-12">
          <div className="card shadow-sm border-0">
            <div className="card-body p-4">
              <div className="d-flex flex-wrap gap-2 mb-3">
                {scheda.isStandard ? (
                  <span className="badge bg-primary p-2">Standard</span>
                ) : (
                  <span className="badge bg-success p-2">Personalizzata</span>
                )}
                <span className="badge bg-info text-dark p-2">
                  {scheda.obiettivo}
                </span>
                {scheda.durataSettimane && (
                  <span className="badge bg-secondary p-2">
                    {scheda.durataSettimane} settimane
                  </span>
                )}
              </div>

              {scheda.descrizione && (
                <p className="card-text text-secondary border-top pt-3">
                  {scheda.descrizione}
                </p>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-12 mb-3">
          <h3 className="fw-bold text-primary">Programma Settimanale</h3>
        </div>

        {scheda.giorni && scheda.giorni.length > 0 ? (
          scheda.giorni.map((giorno) => (
            <div key={giorno.id} className="col-12 mb-4">
              <div className="card shadow-sm border-0 h-100">
                <div className="card-header bg-primary text-white py-3">
                  <h5 className="mb-0 text-uppercase fw-bold">
                    {giorno.giornoSettimana}
                  </h5>
                </div>

                <div className="card-body p-0">
                  {giorno.serie && giorno.serie.length > 0 ? (
                    <>
                      <div className="table-responsive d-none d-md-block">
                        <table className="table table-striped table-hover mb-0 align-middle">
                          <thead className="table-light">
                            <tr>
                              <th
                                scope="col"
                                className="ps-4"
                                style={{ width: '35%' }}
                              >
                                Esercizio
                              </th>
                              <th
                                scope="col"
                                className="text-center"
                                style={{ width: '13%' }}
                              >
                                Serie
                              </th>
                              <th
                                scope="col"
                                className="text-center"
                                style={{ width: '13%' }}
                              >
                                Reps
                              </th>
                              <th
                                scope="col"
                                className="text-center"
                                style={{ width: '13%' }}
                              >
                                Rec (s)
                              </th>
                              <th
                                scope="col"
                                className="text-center"
                                style={{ width: '26%' }}
                              >
                                Peso (kg)
                              </th>
                            </tr>
                          </thead>
                          <tbody>
                            {giorno.serie.map((es) => (
                              <tr key={es.id}>
                                <td className="ps-4 fw-semibold text-primary">
                                  {es.nomeEsercizio}
                                </td>
                                <td className="text-center">
                                  <span className="badge bg-light text-dark border">
                                    {es.numeroSerie}
                                  </span>
                                </td>
                                <td className="text-center">
                                  {es.numeroRipetizioni}
                                </td>
                                <td className="text-center text-muted">
                                  {es.tempoRecuperoSecondi}"
                                </td>
                                <td className="text-center">
                                  {renderWeightCell(giorno.id, es)}
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>

                      {/* CARD LAYOUT PER MOBILE (sm e inferiori) */}
                      <div className="d-md-none p-3">
                        {giorno.serie.map((es) => (
                          <div
                            key={es.id}
                            className="card mb-3 shadow-sm border"
                            style={{
                              borderLeft: '4px solid #0d6efd',
                              transition: 'transform 0.2s',
                            }}
                          >
                            <div className="card-body">
                              <h6 className="card-title text-primary fw-bold mb-3">
                                {es.nomeEsercizio}
                              </h6>

                              <div className="row g-2">
                                <div className="col-6">
                                  <div className="d-flex flex-column">
                                    <small className="text-muted mb-1">
                                      Serie
                                    </small>
                                    <span className="badge bg-light text-dark border w-auto align-self-start px-3 py-2">
                                      {es.numeroSerie}
                                    </span>
                                  </div>
                                </div>

                                <div className="col-6">
                                  <div className="d-flex flex-column">
                                    <small className="text-muted mb-1">
                                      Ripetizioni
                                    </small>
                                    <span className="fw-semibold fs-5">
                                      {es.numeroRipetizioni}
                                    </span>
                                  </div>
                                </div>

                                <div className="col-6">
                                  <div className="d-flex flex-column">
                                    <small className="text-muted mb-1">
                                      Recupero
                                    </small>
                                    <span className="fw-semibold fs-5">
                                      {es.tempoRecuperoSecondi}"
                                    </span>
                                  </div>
                                </div>

                                <div className="col-6">
                                  <div className="d-flex flex-column">
                                    <small className="text-muted mb-1">
                                      Peso (kg)
                                    </small>
                                    <div className="mt-1">
                                      {renderWeightCell(giorno.id, es)}
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </>
                  ) : (
                    <div className="p-4 text-center text-muted">
                      <em>Nessun esercizio programmato per questo giorno.</em>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="col-12">
            <div className="alert alert-info">
              Questa scheda non ha ancora giorni programmati.
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
