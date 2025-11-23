import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

type GiornoAllenamento = {
  id: number
  numeroGiorno: number
  nomeGiorno?: string
  esercizi: EsercizioScheda[]
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
        scheda.giorni
          .sort((a, b) => a.numeroGiorno - b.numeroGiorno)
          .map((giorno) => (
            <div key={giorno.id} className="card mb-3">
              <div className="card-header bg-primary text-white">
                <h5 className="mb-0">
                  Giorno {giorno.numeroGiorno}
                  {giorno.nomeGiorno && ` - ${giorno.nomeGiorno}`}
                </h5>
              </div>
              <div className="card-body">
                {giorno.esercizi && giorno.esercizi.length > 0 ? (
                  <div className="table-responsive">
                    <table className="table table-hover">
                      <thead>
                        <tr>
                          <th>Esercizio</th>
                          <th>Serie</th>
                          <th>Ripetizioni</th>
                          <th>Recupero (sec)</th>
                          <th>Note</th>
                        </tr>
                      </thead>
                      <tbody>
                        {giorno.esercizi.map((es) => (
                          <tr key={es.id}>
                            <td>
                              <strong>{es.esercizio.nome}</strong>
                              {es.esercizio.descrizione && (
                                <div className="text-muted small">
                                  {es.esercizio.descrizione}
                                </div>
                              )}
                            </td>
                            <td>{es.serie}</td>
                            <td>{es.ripetizioni}</td>
                            <td>{es.recupero}</td>
                            <td>{es.note || '-'}</td>
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
