import { useState, useEffect } from 'react'
import { useParams, useSearchParams, Link } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import '../../css/DietaDettaglio.css'

type AlimentoPasto = {
  nome: string
  grammi: number
  proteine: number
  carboidrati: number
  grassi: number
  calorie: number
}

type Pasto = {
  nomePasto: string
  ordine: number
  giornoSettimana: string
  alimenti: AlimentoPasto[]
}

type DietaDettaglioDTO = {
  id: number
  nome: string
  descrizione?: string
  tipoDieta: string
  durataSettimane?: number
  isStandard: boolean
  pasti: Pasto[]
}

export default function DietaDettaglio() {
  const { id } = useParams<{ id: string }>()
  const [searchParams] = useSearchParams()
  // type può essere 'custom' o 'standard' passato dalla pagina precedente
  const type = searchParams.get('type')
  const user = useSelector((s: RootState) => s.auth.user)

  const [dieta, setDieta] = useState<DietaDettaglioDTO | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!id || !user) return

    setLoading(true)
    setError(null)

    // Logica aggiornata in base al nuovo DietaController:
    // GET /diete/custom/{id} -> per diete utente
    // GET /diete/standard/{id} -> per diete standard
    const endpoint =
      type === 'custom'
        ? `${API_BASE_URL}/diete/custom/${id}`
        : `${API_BASE_URL}/diete/standard/${id}`

    fetch(endpoint, {
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
        console.log('Dieta caricata:', data)
        setDieta(data)
      })
      .catch((err) => {
        console.error(err)
        setError(err.message)
      })
      .finally(() => {
        setLoading(false)
      })
  }, [id, type, user])

  if (loading) return <div className="container mt-4">Caricamento dieta...</div>
  if (error)
    return <div className="container mt-4 alert alert-danger">{error}</div>
  if (!dieta) return <div className="container mt-4">Dieta non trovata.</div>

  const pastiPerGiorno = dieta.pasti.reduce((acc, pasto) => {
    const giorno = pasto.giornoSettimana
    if (!acc[giorno]) {
      acc[giorno] = []
    }
    acc[giorno].push(pasto)
    return acc
  }, {} as Record<string, Pasto[]>)

  const ordineGiorni = [
    'LUNEDI',
    'MARTEDI',
    'MERCOLEDI',
    'GIOVEDI',
    'VENERDI',
    'SABATO',
    'DOMENICA',
  ]
  const giorniPresenti = ordineGiorni.filter((g) => pastiPerGiorno[g])

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h1>{dieta.nome}</h1>
        <Link to="/diete" className="btn btn-secondary">
          ← Indietro
        </Link>
      </div>

      <div className="card mb-4">
        <div className="card-body">
          <div className="mb-3">
            {/* Controllo isStandard invece del vecchio check sul type */}
            {!dieta.isStandard ? (
              <span className="badge bg-success me-2">Assegnata</span>
            ) : (
              <span className="badge bg-primary me-2">Standard</span>
            )}
            <span className="badge bg-info me-2">{dieta.tipoDieta}</span>
            {dieta.durataSettimane && (
              <span className="badge bg-secondary">
                {dieta.durataSettimane} settimane
              </span>
            )}
          </div>

          {dieta.descrizione && (
            <p className="card-text">{dieta.descrizione}</p>
          )}
        </div>
      </div>

      <h3 className="mb-3">Piano Alimentare Settimanale</h3>

      {giorniPresenti.length > 0 ? (
        giorniPresenti.map((giorno) => (
          <div key={giorno} className="card mb-3">
            <div className="card-header bg-primary text-white">
              <h5 className="mb-0">{giorno}</h5>
            </div>
            <div className="card-body">
              {pastiPerGiorno[giorno]
                .sort((a, b) => a.ordine - b.ordine)
                .map((pasto, idx) => (
                  <div
                    key={`${giorno}-${pasto.nomePasto}-${idx}`}
                    className="mb-4"
                  >
                    <h6 className="text-primary mb-2">
                      <i className="bi bi-clock me-2"></i>
                      {pasto.nomePasto}
                    </h6>
                    {pasto.alimenti && pasto.alimenti.length > 0 ? (
                      <div className="table-responsive">
                        <table className="table table-sm table-hover table-pasto">
                          <thead>
                            <tr>
                              <th>Alimento</th>
                              <th>Quantità</th>
                              <th>Proteine</th>
                              <th>Carboidrati</th>
                              <th>Grassi</th>
                              <th>Calorie</th>
                            </tr>
                          </thead>
                          <tbody>
                            {pasto.alimenti.map((alimento, aIdx) => (
                              <tr key={aIdx}>
                                <td>
                                  <strong>{alimento.nome}</strong>
                                </td>
                                <td>{alimento.grammi}g</td>
                                <td>{alimento.proteine.toFixed(1)}g</td>
                                <td>{alimento.carboidrati.toFixed(1)}g</td>
                                <td>{alimento.grassi.toFixed(1)}g</td>
                                <td>{alimento.calorie.toFixed(0)} kcal</td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    ) : (
                      <p className="text-muted">
                        Nessun alimento per questo pasto.
                      </p>
                    )}
                  </div>
                ))}
            </div>
          </div>
        ))
      ) : (
        <div className="alert alert-info">
          Questa dieta non ha ancora pasti programmati.
        </div>
      )}
    </div>
  )
}
