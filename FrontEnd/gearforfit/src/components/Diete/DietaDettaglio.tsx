import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

type Pasto = {
  id: number
  nomePasto: string
  orario?: string
  alimenti: AlimentoPasto[]
}

type AlimentoPasto = {
  id: number
  alimento: {
    id: number
    nome: string
    descrizione?: string
  }
  quantita: number
  unitaMisura: string
}

type GiornoDieta = {
  id: number
  numeroGiorno: number
  nomeGiorno?: string
  pasti: Pasto[]
}

type DietaDettaglioDTO = {
  id: number
  nome?: string
  nomeDietaTemplate?: string
  descrizione?: string
  tipoDieta?: string
  tipoDietaObiettivo?: string
  calorieTotali?: number
  proteine?: number
  carboidrati?: number
  grassi?: number
  isStandard?: boolean
  giorni?: GiornoDieta[]
}

export default function DietaDettaglio() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const user = useSelector((s: RootState) => s.auth.user)

  const [dieta, setDieta] = useState<DietaDettaglioDTO | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!id || !user) return

    setLoading(true)
    setError(null)

    fetch(`${API_BASE_URL}/diete/${id}`, {
      headers: getAuthHeader(),
    })
      .then((res) => {
        if (!res.ok) {
          return res.json().then((err) => {
            throw new Error(err.message || 'Errore nel caricamento della dieta')
          })
        }
        return res.json()
      })
      .then((data) => {
        setDieta(data)
      })
      .catch((err) => {
        console.error(err)
        setError(err.message)
      })
      .finally(() => {
        setLoading(false)
      })
  }, [id, user])

  if (loading) return <div className="container mt-4">Caricamento dieta...</div>
  if (error)
    return <div className="container mt-4 alert alert-danger">{error}</div>
  if (!dieta) return <div className="container mt-4">Dieta non trovata.</div>

  const nomeDieta = dieta.nome || dieta.nomeDietaTemplate || 'Dieta'
  const tipoDietaDisplay = dieta.tipoDieta || dieta.tipoDietaObiettivo

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>{nomeDieta}</h1>
        <Link to="/diete" className="btn btn-secondary">
          ← Indietro
        </Link>
      </div>

      <div className="card mb-4">
        <div className="card-body">
          <div className="mb-3">
            {dieta.nomeDietaTemplate ? (
              <span className="badge bg-success me-2">Assegnata</span>
            ) : (
              <span className="badge bg-primary me-2">Standard</span>
            )}
            {tipoDietaDisplay && (
              <span className="badge bg-info me-2">{tipoDietaDisplay}</span>
            )}
          </div>

          {dieta.descrizione && (
            <p className="card-text">{dieta.descrizione}</p>
          )}

          {/* Macronutrienti */}
          {(dieta.calorieTotali ||
            dieta.proteine ||
            dieta.carboidrati ||
            dieta.grassi) && (
            <div className="row mt-3">
              <div className="col-12">
                <h5>Valori Nutrizionali Giornalieri</h5>
              </div>
              {dieta.calorieTotali && (
                <div className="col-md-3">
                  <div className="text-center p-3 bg-light rounded">
                    <div className="h4 mb-0">{dieta.calorieTotali}</div>
                    <small className="text-muted">Calorie</small>
                  </div>
                </div>
              )}
              {dieta.proteine && (
                <div className="col-md-3">
                  <div className="text-center p-3 bg-light rounded">
                    <div className="h4 mb-0">{dieta.proteine}g</div>
                    <small className="text-muted">Proteine</small>
                  </div>
                </div>
              )}
              {dieta.carboidrati && (
                <div className="col-md-3">
                  <div className="text-center p-3 bg-light rounded">
                    <div className="h4 mb-0">{dieta.carboidrati}g</div>
                    <small className="text-muted">Carboidrati</small>
                  </div>
                </div>
              )}
              {dieta.grassi && (
                <div className="col-md-3">
                  <div className="text-center p-3 bg-light rounded">
                    <div className="h4 mb-0">{dieta.grassi}g</div>
                    <small className="text-muted">Grassi</small>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      <h3 className="mb-3">Piano Alimentare</h3>

      {dieta.giorni && dieta.giorni.length > 0 ? (
        dieta.giorni
          .sort((a, b) => a.numeroGiorno - b.numeroGiorno)
          .map((giorno) => (
            <div key={giorno.id} className="card mb-3">
              <div className="card-header bg-success text-white">
                <h5 className="mb-0">
                  Giorno {giorno.numeroGiorno}
                  {giorno.nomeGiorno && ` - ${giorno.nomeGiorno}`}
                </h5>
              </div>
              <div className="card-body">
                {giorno.pasti && giorno.pasti.length > 0 ? (
                  giorno.pasti.map((pasto) => (
                    <div key={pasto.id} className="mb-4">
                      <h6 className="text-primary">
                        {pasto.nomePasto}
                        {pasto.orario && (
                          <span className="text-muted ms-2">
                            ({pasto.orario})
                          </span>
                        )}
                      </h6>
                      {pasto.alimenti && pasto.alimenti.length > 0 ? (
                        <ul className="list-group">
                          {pasto.alimenti.map((ap) => (
                            <li key={ap.id} className="list-group-item">
                              <strong>{ap.alimento.nome}</strong> -{' '}
                              {ap.quantita} {ap.unitaMisura}
                              {ap.alimento.descrizione && (
                                <div className="text-muted small">
                                  {ap.alimento.descrizione}
                                </div>
                              )}
                            </li>
                          ))}
                        </ul>
                      ) : (
                        <p className="text-muted small">
                          Nessun alimento specificato.
                        </p>
                      )}
                    </div>
                  ))
                ) : (
                  <p className="text-muted">
                    Nessun pasto programmato per questo giorno.
                  </p>
                )}
              </div>
            </div>
          ))
      ) : (
        <div className="alert alert-info">
          Questa dieta non ha ancora giorni programmati.
        </div>
      )}
    </div>
  )
}
