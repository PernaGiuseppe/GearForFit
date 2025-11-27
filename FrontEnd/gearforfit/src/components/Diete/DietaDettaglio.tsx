import { useState, useEffect } from 'react'
import { useParams, useSearchParams, Link, useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import '../../css/Dieta.css'

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
  isAttiva?: boolean // Aggiunto campo
  pasti: Pasto[]
}

export default function DietaDettaglio() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const type = searchParams.get('type')
  const user = useSelector((s: RootState) => s.auth.user)

  const [dieta, setDieta] = useState<DietaDettaglioDTO | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!id || !user) return
    setLoading(true)
    setError(null)

    const endpoint =
      user.tipoUtente === 'ADMIN'
        ? `${API_BASE_URL}/admin/diete/${id}`
        : type === 'custom'
        ? `${API_BASE_URL}/diete/custom/${id}`
        : `${API_BASE_URL}/diete/standard/${id}`

    fetch(endpoint, { headers: getAuthHeader() })
      .then((res) => {
        if (!res.ok) throw new Error('Errore nel caricamento della dieta')
        return res.json()
      })
      .then((data) => setDieta(data))
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false))
  }, [id, type, user])

  // --- HANDLER ATTIVAZIONE ---
  const handleToggleAttiva = async () => {
    if (!dieta || dieta.isStandard) return

    try {
      const res = await fetch(
        `${API_BASE_URL}/diete/custom/${dieta.id}/attiva`,
        {
          method: 'PATCH',
          headers: {
            ...getAuthHeader(),
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ isAttiva: !dieta.isAttiva }),
        }
      )

      if (res.ok) {
        // Aggiorna solo lo stato corrente
        setDieta((prev) =>
          prev ? { ...prev, isAttiva: !prev.isAttiva } : null
        )
      } else {
        alert("Errore durante l'aggiornamento")
      }
    } catch (err) {
      console.error('Errore toggle:', err)
      alert('Errore di connessione')
    }
  }

  // --- HANDLER ELIMINAZIONE ---
  const handleDelete = async () => {
    if (!dieta) return

    if (
      !window.confirm(`Sei sicuro di voler eliminare la dieta "${dieta.nome}"?`)
    ) {
      return
    }

    try {
      const endpoint =
        user?.tipoUtente === 'ADMIN'
          ? `${API_BASE_URL}/admin/diete/${dieta.id}`
          : `${API_BASE_URL}/diete/custom/${dieta.id}`

      const res = await fetch(endpoint, {
        method: 'DELETE',
        headers: getAuthHeader(),
      })

      if (res.ok) {
        alert('Dieta eliminata con successo')
        navigate('/diete')
      } else {
        alert("Errore durante l'eliminazione della dieta")
      }
    } catch (err) {
      console.error('Errore delete:', err)
      alert('Errore di connessione')
    }
  }

  if (loading)
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border text-primary"></div>
      </div>
    )
  if (error)
    return <div className="container mt-5 alert alert-danger">{error}</div>
  if (!dieta)
    return (
      <div className="container mt-5 alert alert-warning">
        Dieta non trovata.
      </div>
    )

  // Raggruppamento dati
  const pastiPerGiorno = dieta.pasti.reduce((acc, pasto) => {
    const giorno = pasto.giornoSettimana
    if (!acc[giorno]) acc[giorno] = []
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
    <div className="container py-4 page-content-custom">
      {/* Intestazione */}
      <div className="row align-items-center mb-4">
        <div className="col-12 col-md-8 d-flex align-items-center">
          <h1 className="fw-bold mb-2 me-3">{dieta.nome}</h1>

          {/* STELLA (Solo per custom) */}
          {!dieta.isStandard && user?.tipoUtente !== 'ADMIN' && (
            <i
              className={`bi bi-star${
                dieta.isAttiva ? '-fill star-active' : ' star-inactive'
              } fs-2`}
              onClick={handleToggleAttiva}
              style={{ cursor: 'pointer' }}
              title={
                dieta.isAttiva
                  ? 'Dieta attiva - Clicca per disattivare'
                  : 'Clicca per attivare'
              }
            ></i>
          )}
        </div>

        <div className="col-12 col-md-4 text-md-end mt-3 mt-md-0">
          {/* DELETE BUTTON */}
          {(user?.tipoUtente === 'ADMIN' || !dieta?.isStandard) && (
            <button
              className="btn btn-outline-danger me-2"
              onClick={handleDelete}
            >
              <i className="bi bi-trash me-2"></i>Elimina
            </button>
          )}

          <Link to="/diete" className="btn btn-outline-secondary">
            <i className="bi bi-arrow-left me-2"></i>Indietro
          </Link>
        </div>
      </div>

      <div className="row">
        <div className="col-12">
          <p>Piano nutrizionale dettagliato</p>
        </div>
      </div>

      {/* Info Card */}
      <div className="row mb-5">
        <div className="col-12">
          <div className="card shadow-sm border-0 bg-white">
            <div className="card-body p-4">
              <div className="d-flex flex-wrap gap-2 mb-3">
                {!dieta.isStandard ? (
                  <span className="badge bg-success p-2">Assegnata</span>
                ) : (
                  <span className="badge bg-primary p-2">Standard</span>
                )}
                <span className="badge bg-info text-dark p-2">
                  {dieta.tipoDieta}
                </span>
                {dieta.durataSettimane && (
                  <span className="badge bg-secondary p-2">
                    {dieta.durataSettimane} settimane
                  </span>
                )}
                {/* Rimosso badge 'Attiva', ora c'è la stella */}
              </div>
              {dieta.descrizione && (
                <p className="card-text  border-top pt-3">
                  {dieta.descrizione}
                </p>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="row mb-3">
        <div className="col-12">
          <h3 className="fw-bold text-primary">Piano Alimentare Settimanale</h3>
        </div>
      </div>

      {/* Loop Giorni */}
      <div className="row">
        {giorniPresenti.length > 0 ? (
          giorniPresenti.map((giorno) => (
            <div key={giorno} className="col-12 mb-4">
              <div className="card shadow-sm border-0 h-100">
                <div className="card-header bg-primary text-white py-3">
                  <h5 className="mb-0 text-uppercase fw-bold">{giorno}</h5>
                </div>

                <div className="card-body p-0">
                  {pastiPerGiorno[giorno]
                    .sort((a, b) => a.ordine - b.ordine)
                    .map((pasto, idx) => (
                      <div key={`${giorno}-${pasto.nomePasto}-${idx}`}>
                        <div className="bg-light px-3 py-2 border-bottom border-top">
                          <h6 className="text-success fw-bold mb-0 text-uppercase">
                            {pasto.nomePasto}
                          </h6>
                        </div>

                        {pasto.alimenti && pasto.alimenti.length > 0 ? (
                          <div className="table-responsive">
                            <table className="table table-striped table-hover mb-0 align-middle">
                              <thead className="table-light">
                                <tr>
                                  <th
                                    scope="col"
                                    className="ps-3"
                                    style={{ width: '40%' }}
                                  >
                                    Alimento
                                  </th>
                                  <th
                                    scope="col"
                                    className="text-center"
                                    style={{ width: '12%' }}
                                  >
                                    Q.tà
                                  </th>
                                  <th
                                    scope="col"
                                    className="text-center"
                                    style={{ width: '12%' }}
                                  >
                                    Pro
                                  </th>
                                  <th
                                    scope="col"
                                    className="text-center"
                                    style={{ width: '12%' }}
                                  >
                                    Carbo
                                  </th>
                                  <th
                                    scope="col"
                                    className="text-center"
                                    style={{ width: '12%' }}
                                  >
                                    Grassi
                                  </th>
                                  <th
                                    scope="col"
                                    className="text-center"
                                    style={{ width: '12%' }}
                                  >
                                    Kcal
                                  </th>
                                </tr>
                              </thead>
                              <tbody>
                                {pasto.alimenti.map((alimento, aIdx) => (
                                  <tr key={aIdx}>
                                    <td className="ps-3 fw-semibold text-primary">
                                      {alimento.nome}
                                    </td>
                                    <td className="text-center">
                                      <span className="badge bg-light text-dark border">
                                        {alimento.grammi}g
                                      </span>
                                    </td>
                                    <td className="text-center text-muted small">
                                      {alimento.proteine.toFixed(1)}
                                    </td>
                                    <td className="text-center text-muted small">
                                      {alimento.carboidrati.toFixed(1)}
                                    </td>
                                    <td className="text-center text-muted small">
                                      {alimento.grassi.toFixed(1)}
                                    </td>
                                    <td className="text-center fw-bold text-dark">
                                      {alimento.calorie.toFixed(0)}
                                    </td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </div>
                        ) : (
                          <div className="p-3 text-muted small fst-italic">
                            Nessun alimento inserito.
                          </div>
                        )}
                      </div>
                    ))}
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="col-12">
            <div className="alert alert-info">
              Questa dieta non ha ancora pasti programmati.
            </div>
          </div>
        )}
      </div>
    </div>
  )
}
