import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

type GiornoAllenamento = {
  id: number
  giornoSettimana: string // era "nomeGiorno"
  serie: EsercizioScheda[] // era "esercizi"
}

type EsercizioScheda = {
  id: number
  esercizio: {
    id: number
    nome: string
    descrizione?: string
  }
  serie: number
  ripetizioni: number
  recupero: number
  note?: string
}

type SchedaDettaglioDTO = {
  id: number
  nome: string
  descrizione?: string
  obiettivo: string
  isStandard: boolean
  isAttiva?: boolean
  giorni: GiornoAllenamento[]
  utenteId?: number
}
type EsercizioScheda = {
  id: number
  esercizioId: number
  nomeEsercizio: string
  numeroSerie: number
  numeroRipetizioni: number
  tempoRecuperoSecondi: number
}
export default function SchedaDettaglio() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const user = useSelector((s: RootState) => s.auth.user)

  const [scheda, setScheda] = useState<SchedaDettaglioDTO | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!id || !user) return

    setLoading(true)
    setError(null)

    fetch(`${API_BASE_URL}/schede-allenamento/${id}`, {
      headers: getAuthHeader(),
    })
      .then((res) => {
        if (!res.ok) {
          return res.json().then((err) => {
            throw new Error(
              err.message || 'Errore nel caricamento della scheda'
            )
          })
        }
        return res.json()
      })
      .then((data) => {
        setScheda(data)
      })
      .catch((err) => {
        console.error(err)
        setError(err.message)
      })
      .finally(() => {
        setLoading(false)
      })
  }, [id, user])

  if (loading)
    return <div className="container mt-4">Caricamento scheda...</div>
  if (error)
    return <div className="container mt-4 alert alert-danger">{error}</div>
  if (!scheda) return <div className="container mt-4">Scheda non trovata.</div>

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>{scheda.nome}</h1>
        <Link to="/schede" className="btn btn-secondary">
          ← Indietro
        </Link>
      </div>

      <div className="card mb-4">
        <div className="card-body">
          <div className="mb-3">
            {scheda.isStandard ? (
              <span className="badge bg-primary me-2">Standard</span>
            ) : (
              <span className="badge bg-success me-2">Personalizzata</span>
            )}
            <span className="badge bg-info me-2">{scheda.obiettivo}</span>
            {scheda.isAttiva && (
              <span className="badge bg-warning">Attiva</span>
            )}
          </div>

          {scheda.descrizione && (
            <p className="card-text">{scheda.descrizione}</p>
          )}
        </div>
      </div>

      <h3 className="mb-3">Programma di Allenamento</h3>

      {scheda.giorni && scheda.giorni.length > 0 ? (
        scheda.giorni.map((giorno) => (
          <div key={giorno.id} className="card mb-3">
            <div className="card-header bg-primary text-white">
              <h5 className="mb-0">{giorno.giornoSettimana}</h5>
            </div>
            <div className="card-body">
              {giorno.serie && giorno.serie.length > 0 ? (
                <div className="table-responsive">
                  <table className="table table-hover">
                    <thead>
                      <tr>
                        <th>Esercizio</th>
                        <th>Serie</th>
                        <th>Ripetizioni</th>
                        <th>Recupero (sec)</th>
                      </tr>
                    </thead>
                    <tbody>
                      {giorno.serie.map((es) => (
                        <tr key={es.id}>
                          <td>
                            <strong>{es.nomeEsercizio}</strong>
                          </td>
                          <td>{es.numeroSerie}</td>
                          <td>{es.numeroRipetizioni}</td>
                          <td>{es.tempoRecuperoSecondi}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ) : (
                <p className="text-muted">
                  Nessun esercizio per questo giorno.
                </p>
              )}
            </div>
          </div>
        ))
      ) : (
        <div className="alert alert-info">
          Questa scheda non ha ancora giorni programmati.
        </div>
      )}
    </div>
  )
}
