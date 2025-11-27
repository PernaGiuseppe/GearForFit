import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import { BsStar, BsStarFill, BsPencilFill } from 'react-icons/bs' // Aggiunta BsPencilFill
import '../../css/Schede.css'
import { GiConfirmed } from 'react-icons/gi'

// NUOVO TIPO: SerieDTO con campo peso (rispecchia il DTO di SchedaAllenamentoService.java)
type SerieDTOBackend = {
  id: number
  esercizioId: number
  nomeEsercizio: string
  numeroSerie: number
  numeroRipetizioni: number
  tempoRecuperoSecondi: number
  peso?: string | null // Aggiunto il campo peso
}

// Aggiornamento del tipo per l'elemento della scheda
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

// Nuovo tipo di stato per la gestione della modifica del peso
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

  // STATO PER LA MODIFICA DEL PESO
  const [editing, setEditing] = useState<EditingState>({
    giornoId: null,
    serieId: null,
    newWeight: '',
  })

  // TIPO PER IL MESSAGGIO DI SUCCESSO/ERRORE (opzionale)
  const [updateMessage, setUpdateMessage] = useState<{
    type: 'success' | 'error'
    text: string
  } | null>(null)
  useEffect(() => {
    if (!id || !user) return

    setLoading(true)
    setError(null)

    // Se l' admin è loggato, usa endpoint admin. Altrimenti usa logica utenti
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

  // --- HANDLER ATTIVAZIONE
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

  // --- LOGICA ELIMINAZIONE ---
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

  // --- HANDLER PER AGGIORNARE IL PESO ---
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

        // Aggiorna lo stato della scheda con il nuovo peso
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
        setEditing({ giornoId: null, serieId: null, newWeight: '' }) // Chiudi l'editing
        setUpdateMessage({
          type: 'success',
          text: `Peso aggiornato a ${updatedSerie.peso}`,
        })
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

  // Funzione per il rendering condizionale della cella del peso
  const renderWeightCell = (giornoId: number, serie: EsercizioScheda) => {
    const isEditing =
      editing.giornoId === giornoId && editing.serieId === serie.id

    if (isEditing) {
      return (
        <div className="d-flex align-items-center justify-content-center">
          <input
            type="text"
            className="form-control form-control-sm me-2 text-center"
            style={{ width: '80px' }}
            value={editing.newWeight}
            onChange={(e) =>
              setEditing({ ...editing, newWeight: e.target.value })
            }
            onKeyDown={(e) => {
              if (e.key === 'Enter') {
                handleWeightUpdate(serie, editing.newWeight)
              }
            }}
            autoFocus
          />
          <GiConfirmed
            className="green-confirm"
            title="Conferma peso"
            onClick={() => handleWeightUpdate(serie, editing.newWeight)}
            style={{ cursor: 'pointer', fontSize: '1.2em' }}
          />
        </div>
      )
    }

    const displayedWeight =
      serie.peso && serie.peso.trim() !== '' ? serie.peso : '---'

    // Mostra il peso e l'icona di modifica se non è una scheda standard
    return (
      <div className="d-flex align-items-center justify-content-center">
        <span className="me-2">{displayedWeight}</span>
        {!scheda?.isStandard && (
          <BsPencilFill
            className="text-primary"
            title="Modifica peso"
            onClick={() =>
              setEditing({
                giornoId,
                serieId: serie.id,
                newWeight: serie.peso || '', // Carica il peso esistente
              })
            }
            style={{ cursor: 'pointer', fontSize: '0.8em' }}
          />
        )}
      </div>
    )
  }

  // Rendering degli stati di caricamento/errore/non trovato (omesso per brevità, è nel tuo codice originale)
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
      {/* Intestazione */}
      <div className="row align-items-center mb-4">
        <div className="col-12 col-md-8 d-flex align-items-center">
          <h1 className="fw-bold mb-2 me-3">{scheda.nome}</h1>
          {/* Logica Stella attiva */}
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
        <div className="col-12 col-md-4 text-md-end mt-3 mt-md-0">
          {(user?.tipoUtente === 'ADMIN' || !scheda?.isStandard) && (
            <button
              className="btn btn-outline-danger me-2"
              onClick={handleDelete}
            >
              <i className="bi bi-trash me-2"></i>Elimina
            </button>
          )}

          <Link to="/schede" className="btn btn-outline-secondary">
            <i className="bi bi-arrow-left me-2"></i>Torna alle schede
          </Link>
        </div>
      </div>

      {/* Messaggio di aggiornamento */}
      {updateMessage && (
        <div
          className={`alert alert-${
            updateMessage.type === 'success' ? 'success' : 'danger'
          }`}
          role="alert"
        >
          {updateMessage.text}
        </div>
      )}

      <div className="row">
        <div className="col-12">
          <p className="text-muted">Dettaglio del programma di allenamento</p>
        </div>
      </div>

      {/* Info Card (omesso per brevità, è nel tuo codice originale) */}
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

      {/* Programma di Allenamento */}
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
                    <div className="table-responsive">
                      <table className="table table-striped table-hover mb-0 align-middle">
                        <thead className="table-light">
                          <tr>
                            <th
                              scope="col"
                              className="ps-4"
                              style={{ width: '30%' }}
                            >
                              Esercizio
                            </th>
                            <th scope="col" className="text-center">
                              Serie
                            </th>
                            <th scope="col" className="text-center">
                              Reps
                            </th>
                            <th scope="col" className="text-center">
                              Rec (s)
                            </th>
                            {/* NUOVA COLONNA */}
                            <th scope="col" className="text-center">
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
                              {/* Cella per la MODIFICA del peso */}
                              <td className="text-center">
                                {renderWeightCell(giorno.id, es)}
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
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
