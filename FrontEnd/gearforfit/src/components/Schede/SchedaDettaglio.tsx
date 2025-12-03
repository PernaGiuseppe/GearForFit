import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import { BsPencilFill } from 'react-icons/bs'
import { FaBookmark, FaRegBookmark } from 'react-icons/fa'
import { toast } from 'sonner'
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

  useEffect(() => {
    if (!id || !user) return

    setLoading(true)
    setError(null)

    const endpoint =
      user?.tipoUtente === 'ADMIN'
        ? `${API_BASE_URL}/admin/schede/${id}`
        : `${API_BASE_URL}/schede-allenamento/${id}`

    setLoading(true)
    fetch(endpoint, { headers: getAuthHeader() })
      .then((res) => {
        if (!res.ok) throw new Error('Errore nel caricamento della scheda')
        return res.json()
      })
      .then((data) => {
        setScheda(data)
        toast.success('Scheda caricata')
      })
      .catch((err) => {
        setError(err.message)
        toast.error(err.message || 'Errore nel caricamento della scheda')
      })
      .finally(() => {
        setLoading(false)
      })
  }, [id, user])

  const handleToggleAttiva = async (e: React.MouseEvent) => {
    e.preventDefault()
    e.stopPropagation()

    if (!scheda || scheda.isStandard) return

    toast.promise(
      fetch(
        `${API_BASE_URL}/schede-allenamento/me/schede/${scheda.id}/attiva`,
        {
          method: 'PUT',
          headers: getAuthHeader(),
        }
      ).then(async (res) => {
        if (!res.ok) throw new Error("Errore durante l'aggiornamento")
        setScheda((prev) => (prev ? { ...prev, attiva: !prev.attiva } : null))
      }),
      {
        loading: 'Aggiornamento in corso...',
        success: scheda.attiva
          ? 'Scheda disattivata con successo'
          : 'Scheda attivata con successo',
        error: "Errore durante l'aggiornamento dello stato",
      }
    )
  }

  const handleDelete = async () => {
    if (!scheda) return

    toast('Sei sicuro di voler eliminare questa scheda?', {
      action: {
        label: 'Conferma',
        onClick: async () => {
          const endpoint =
            user?.tipoUtente === 'ADMIN'
              ? `${API_BASE_URL}/admin/schede/${scheda.id}`
              : `${API_BASE_URL}/schede-allenamento/me/${scheda.id}`

          toast.promise(
            fetch(endpoint, {
              method: 'DELETE',
              headers: getAuthHeader(),
            }).then((res) => {
              if (!res.ok && res.status !== 204)
                throw new Error('Errore eliminazione')
              navigate('/schede')
            }),
            {
              loading: 'Eliminazione in corso...',
              success: 'Scheda eliminata con successo',
              error: 'Impossibile eliminare la scheda',
            }
          )
        },
      },
      cancel: {
        label: 'Annulla',
      },
      duration: 5000,
    })
  }

  const handleWeightUpdate = async (
    serie: EsercizioScheda,
    newWeight: string
  ) => {
    if (!scheda || scheda.isStandard) return

    toast.promise(
      fetch(
        `${API_BASE_URL}/schede-allenamento/me/schede/${scheda.id}/serie/${serie.id}/peso`,
        {
          method: 'PATCH',
          headers: {
            ...getAuthHeader(),
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ peso: newWeight }),
        }
      ).then(async (res) => {
        if (!res.ok) throw new Error("Errore durante l'aggiornamento del peso")

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
        return updatedSerie.peso
      }),
      {
        loading: 'Aggiornamento peso...',
        success: (peso) => `Peso aggiornato a ${peso} kg`,
        error: 'Errore durante aggiornamento peso',
      }
    )
  }

  const renderWeightCell = (giornoId: number, serie: EsercizioScheda) => {
    const isEditing =
      editing.giornoId === giornoId && editing.serieId === serie.id

    const displayedWeight =
      serie.peso && serie.peso.trim() !== '' ? serie.peso : '---'

    const canEdit = user?.tipoUtente !== 'ADMIN' && !scheda?.isStandard

    return (
      <div style={{ position: 'relative', minHeight: '30px' }}>
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
          </div>
        )}
      </div>
    )
  }

  if (loading)
    return (
      <div className="container mt-5 text-center page-content-custom-2">
        <div className="spinner-border text-primary" role="status"></div>
      </div>
    )
  if (error)
    return (
      <div className="container mt-5 page-content-custom-2">
        <div className="alert alert-danger">{error}</div>
      </div>
    )
  if (!scheda)
    return (
      <div className="container mt-5 page-content-custom-2">
        <div className="alert alert-warning">Scheda non trovata.</div>
      </div>
    )

  return (
    <div className="container py-4">
      <div className="row align-items-center mb-4">
        <div className="col-12 col-md-8 d-flex align-items-center">
          <h1 className="fw-bold mb-2 me-3">{scheda.nome}</h1>
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

      {/* Resto del componente rimane uguale... */}
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
                <span className="badge bg-info text-light p-2">
                  {scheda.obiettivo}
                </span>
                {scheda.durataSettimane && (
                  <span className="badge bg-secondary p-2">
                    {scheda.durataSettimane} settimane
                  </span>
                )}
                {scheda &&
                  !scheda.isStandard &&
                  user?.tipoUtente !== 'ADMIN' &&
                  scheda.attiva && (
                    <span className="badge badge--attiva p-2">Attiva</span>
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

      {/* Giorni allenamento... */}
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
                  {giorno.serie && giorno.serie.length > 0 && (
                    <div className="table-responsive">
                      <table className="table table-striped table-hover mb-0 align-middle">
                        <thead className="table-light">
                          <tr>
                            <th
                              scope="col"
                              className="ps-4 **text-start**"
                              style={{ width: '40%' }}
                            >
                              Esercizio
                            </th>
                            <th
                              scope="col"
                              className="text-center"
                              style={{ width: '15%' }}
                            >
                              Serie
                            </th>
                            <th
                              scope="col"
                              className="text-center"
                              style={{ width: '15%' }}
                            >
                              Reps
                            </th>
                            <th
                              scope="col"
                              className="text-center"
                              style={{ width: '15%' }}
                            >
                              Rec (s)
                            </th>
                            <th
                              scope="col"
                              className="text-center"
                              style={{ width: '15%' }}
                            >
                              Peso (kg)
                            </th>
                          </tr>
                        </thead>
                        <tbody>
                          {giorno.serie.map((es) => (
                            <tr key={es.id}>
                              <td className="ps-4 fw-semibold text-primary **text-start**">
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
