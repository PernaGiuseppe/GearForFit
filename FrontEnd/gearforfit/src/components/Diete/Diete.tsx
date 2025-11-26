import { useState, useEffect, useMemo } from 'react'
import { useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
// Importiamo il CSS aggiornato che contiene le classi per la stella e il delete
import '../../css/DietaDettaglio.css'

type DietaDTO = {
  id: number
  nome: string
  descrizione?: string
  tipoDieta: string
  isStandard: boolean
  isAttiva?: boolean
}

type FilterType = 'STANDARD' | 'PERSONALIZZATE' | 'ALL'
type TipoDietaFilter = 'ALL' | 'IPOCALORICA' | 'IPERCALORICA' | 'NORMOCALORICA'

export default function Diete() {
  const user = useSelector((s: RootState) => s.auth.user)
  const [diete, setDiete] = useState<DietaDTO[]>([])
  const [filter, setFilter] = useState<FilterType>('ALL')
  const [tipoDietaFilter, setTipoDietaFilter] = useState<TipoDietaFilter>('ALL')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const canViewPersonalized = useMemo(() => {
    if (!user) return false
    const p = user.tipoPiano
    return (
      p === 'SILVER' ||
      p === 'GOLD' ||
      p === 'PREMIUM' ||
      user.tipoUtente === 'ADMIN'
    )
  }, [user])

  useEffect(() => {
    if (!user) return
    setLoading(true)
    setError(null)

    const fetchDiete = async () => {
      try {
        let result: DietaDTO[] = []
        const promises = []

        if (filter === 'ALL' || filter === 'STANDARD') {
          promises.push(
            fetch(`${API_BASE_URL}/diete/standard`, {
              headers: getAuthHeader(),
            }).then((res) => {
              if (!res.ok) throw new Error('Errore caricamento diete standard')
              return res.json()
            })
          )
        }

        if (
          canViewPersonalized &&
          (filter === 'ALL' || filter === 'PERSONALIZZATE')
        ) {
          promises.push(
            fetch(`${API_BASE_URL}/diete/custom`, {
              headers: getAuthHeader(),
            }).then((res) => {
              if (!res.ok)
                throw new Error('Errore caricamento diete personalizzate')
              return res.json()
            })
          )
        }

        const responses = await Promise.all(promises)
        responses.forEach((data) => {
          if (Array.isArray(data)) {
            result = [...result, ...data]
          }
        })

        if (tipoDietaFilter !== 'ALL') {
          result = result.filter((d) => d.tipoDieta === tipoDietaFilter)
        }

        setDiete(result)
      } catch (err: any) {
        console.error(err)
        setError(err.message)
        setDiete([])
      } finally {
        setLoading(false)
      }
    }

    fetchDiete()
  }, [user, filter, tipoDietaFilter, canViewPersonalized])

  useEffect(() => {
    setTipoDietaFilter('ALL')
  }, [filter])

  // --- LOGICA ATTIVAZIONE ---
  const handleToggleAttiva = async (
    e: React.MouseEvent,
    dietaId: number,
    currentState: boolean
  ) => {
    e.preventDefault() // Previeni navigazione Link
    e.stopPropagation()

    try {
      const res = await fetch(
        `${API_BASE_URL}/diete/custom/${dietaId}/attiva`,
        {
          method: 'PATCH',
          headers: {
            ...getAuthHeader(),
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ isAttiva: !currentState }),
        }
      )

      if (res.ok) {
        // Aggiorna lo stato locale
        setDiete((prevDiete) =>
          prevDiete.map((d) => {
            // Se stiamo attivando questa dieta (currentState era false),
            // allora questa diventa true e TUTTE le altre custom diventano false.
            if (!currentState) {
              if (d.id === dietaId) return { ...d, isAttiva: true }
              if (!d.isStandard) return { ...d, isAttiva: false }
              return d
            } else {
              // Se stiamo disattivando questa dieta
              if (d.id === dietaId) return { ...d, isAttiva: false }
              return d
            }
          })
        )
      } else {
        alert("Errore durante l'aggiornamento dello stato")
      }
    } catch (err) {
      console.error('Errore toggle attiva:', err)
      alert('Errore di connessione')
    }
  }

  // --- LOGICA ELIMINAZIONE ---
  const handleDelete = async (
    e: React.MouseEvent,
    dietaId: number,
    nome: string
  ) => {
    e.preventDefault()
    e.stopPropagation()

    if (!window.confirm(`Sei sicuro di voler eliminare la dieta "${nome}"?`)) {
      return
    }

    try {
      const res = await fetch(`${API_BASE_URL}/diete/custom/${dietaId}`, {
        method: 'DELETE',
        headers: getAuthHeader(),
      })

      if (res.ok) {
        // Rimuovi dalla lista locale
        setDiete((prev) => prev.filter((d) => d.id !== dietaId))
      } else {
        alert("Errore durante l'eliminazione della dieta")
      }
    } catch (err) {
      console.error('Errore delete:', err)
      alert('Errore di connessione')
    }
  }

  if (!user) return <div>Devi essere loggato per vedere le diete.</div>

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h1>Catalogo Diete</h1>
        {(user?.tipoPiano === 'SILVER' ||
          user?.tipoPiano === 'GOLD' ||
          user?.tipoPiano === 'PREMIUM') && (
          <Link to="/diete/crea-custom" className="btn btn-success">
            <i className="bi bi-plus-circle me-2"></i>
            Crea Dieta Custom
          </Link>
        )}
      </div>

      {/* Filtri */}
      <div className="row mb-3">
        <div className="col-md-6">
          <label className="form-label">Filtra per categoria:</label>
          <select
            className="form-select"
            value={filter}
            onChange={(e) => setFilter(e.target.value as FilterType)}
            disabled={loading}
          >
            {canViewPersonalized && <option value="ALL">Tutte</option>}
            <option value="STANDARD">Standard</option>
            {canViewPersonalized && (
              <option value="PERSONALIZZATE">Le mie Diete</option>
            )}
          </select>
        </div>

        <div className="col-md-6">
          <label className="form-label">Filtra per obiettivo:</label>
          <select
            className="form-select"
            value={tipoDietaFilter}
            onChange={(e) =>
              setTipoDietaFilter(e.target.value as TipoDietaFilter)
            }
            disabled={loading}
          >
            <option value="ALL">Tutte</option>
            <option value="IPOCALORICA">Ipocalorica (Dimagrimento)</option>
            <option value="IPERCALORICA">Ipercalorica (Massa)</option>
            <option value="NORMOCALORICA">Normocalorica (Mantenimento)</option>
          </select>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {loading && <div>Caricamento diete...</div>}
      {!loading && !error && diete.length === 0 && (
        <div className="alert alert-info">
          Nessuna dieta trovata per i filtri selezionati.
        </div>
      )}

      {/* Lista Cards */}
      {!loading && diete.length > 0 && (
        <div className="row pt-2">
          {diete.map((dieta) => {
            const nomeVisualizzato = dieta.nome || 'Dieta senza nome'
            const descVisualizzata = dieta.descrizione || ''
            const uniqueKey = `${dieta.isStandard ? 'std' : 'cust'}-${dieta.id}`
            const queryType = dieta.isStandard ? 'standard' : 'custom'

            return (
              <div key={uniqueKey} className="col-md-4 mb-4">
                {/* Aggiunta classe card-diet-wrapper per posizionamento relativo del bottone delete */}
                <div className="card h-100 card-diet-wrapper">
                  {/* BUTTON DELETE (Solo per custom) */}
                  {!dieta.isStandard && (
                    <button
                      className="btn-delete-card"
                      onClick={(e) =>
                        handleDelete(e, dieta.id, nomeVisualizzato)
                      }
                      title="Elimina dieta"
                    >
                      <i className="bi bi-trash-fill"></i>
                    </button>
                  )}

                  <div className="card-body">
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <h5 className="card-title fw-bold mb-0">
                        {nomeVisualizzato}
                      </h5>

                      {/* STELLA (Solo per custom) */}
                      {!dieta.isStandard && (
                        <i
                          className={`bi bi-star${
                            dieta.isAttiva
                              ? '-fill star-active'
                              : ' star-inactive'
                          } fs-4 ms-2`}
                          onClick={(e) =>
                            handleToggleAttiva(e, dieta.id, !!dieta.isAttiva)
                          }
                          title={
                            dieta.isAttiva
                              ? 'Dieta attiva - Clicca per disattivare'
                              : 'Clicca per attivare questa dieta'
                          }
                        ></i>
                      )}
                    </div>

                    <div className="mb-3">
                      {!dieta.isStandard ? (
                        <span className="badge bg-success me-2">Assegnata</span>
                      ) : (
                        <span className="badge bg-primary me-2">Standard</span>
                      )}

                      {dieta.tipoDieta && (
                        <span className="badge bg-info text-dark">
                          {dieta.tipoDieta}
                        </span>
                      )}
                    </div>

                    <p className="card-text text-muted small">
                      {descVisualizzata.length > 100
                        ? descVisualizzata.substring(0, 100) + '...'
                        : descVisualizzata}
                    </p>

                    <Link
                      to={`/diete/dettaglio/${dieta.id}?type=${queryType}`}
                      className="btn btn-outline-primary w-100 mt-auto"
                    >
                      Visualizza Dettagli
                    </Link>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
