import { useState, useEffect } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

type EsercizioScheda = {
  id: number
  esercizioId: number
  nomeEsercizio: string
  numeroSerie: number
  numeroRipetizioni: number
  tempoRecuperoSecondi: number
}

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
  isAttiva?: boolean
  durataSettimane?: number
  giorni: GiornoAllenamento[]
  utenteId?: number
}

export default function SchedaDettaglio() {
  const { id } = useParams<{ id: string }>()
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
        if (!res.ok) throw new Error('Errore nel caricamento della scheda')
        return res.json()
      })
      .then((data) => setScheda(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id, user])

  if (loading)
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border text-primary" role="status"></div>
      </div>
    )
  if (error)
    return (
      <div className="container mt-5">
        <div className="alert alert-danger">{error}</div>
      </div>
    )
  if (!scheda)
    return (
      <div className="container mt-5">
        <div className="alert alert-warning">Scheda non trovata.</div>
      </div>
    )

  return (
    <div className="container py-4">
      {/* Intestazione: Titolo e Bottone su riga responsive */}
      <div className="row align-items-center mb-4">
        <div className="col-12 col-md-8">
          <h1 className="fw-bold mb-1">{scheda.nome}</h1>
          <p className="text-muted mb-0">
            Dettaglio del programma di allenamento
          </p>
        </div>
        <div className="col-12 col-md-4 text-md-end mt-3 mt-md-0">
          <Link to="/schede" className="btn btn-outline-secondary">
            <i className="bi bi-arrow-left me-2"></i>Torna alle schede
          </Link>
        </div>
      </div>

      {/* Info Card: Uso della Grid per i badge */}
      <div className="row mb-4">
        <div className="col-12">
          <div className="card shadow-sm border-0">
            <div className="card-body p-4">
              <div className="row g-3 align-items-center">
                <div className="col-12 col-md-auto">
                  {scheda.isStandard ? (
                    <span className="badge bg-primary p-2">Standard</span>
                  ) : (
                    <span className="badge bg-success p-2">Personalizzata</span>
                  )}
                </div>
                <div className="col-12 col-md-auto">
                  <span className="badge bg-info text-dark p-2">
                    {scheda.obiettivo}
                  </span>
                </div>
                {scheda.isAttiva && (
                  <div className="col-12 col-md-auto">
                    <span className="badge bg-warning text-dark p-2">
                      Attiva
                    </span>
                  </div>
                )}
                {scheda.durataSettimane && (
                  <div className="col-12 col-md-auto">
                    <span className="badge bg-secondary p-2">
                      {scheda.durataSettimane} settimane
                    </span>
                  </div>
                )}
              </div>

              {scheda.descrizione && (
                <div className="row mt-3">
                  <div className="col-12">
                    <p className="card-text text-secondary">
                      {scheda.descrizione}
                    </p>
                  </div>
                </div>
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
                {/* Header del giorno con stile Bootstrap nativo */}
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
                              style={{ width: '40%' }}
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
