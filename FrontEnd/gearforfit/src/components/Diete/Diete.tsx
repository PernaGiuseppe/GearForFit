import { useState, useEffect, useMemo } from 'react'
import { useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import { BsTrash, BsStar, BsStarFill } from 'react-icons/bs'
import { toast } from 'sonner'
import '../../css/Dieta.css'

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

  const isAdmin = user?.tipoUtente === 'ADMIN'

  const canViewPersonalized = useMemo(() => {
    if (!user) return false
    const p = user.tipoPiano
    return p === 'SILVER' || p === 'GOLD' || p === 'PREMIUM'
  }, [user])

  useEffect(() => {
    if (!user) return
    setLoading(true)
    setError(null)

    const fetchDiete = async () => {
      try {
        let result: DietaDTO[] = []
        const promises: Promise<DietaDTO[]>[] = []

        if (isAdmin) {
          if (filter === 'ALL' || filter === 'STANDARD') {
            promises.push(
              fetch(`${API_BASE_URL}/admin/diete/standard`, {
                headers: getAuthHeader(),
              }).then((res) => {
                if (!res.ok)
                  throw new Error('Errore caricamento diete standard')
                return res.json()
              })
            )
          }

          if (filter === 'ALL' || filter === 'PERSONALIZZATE') {
            promises.push(
              fetch(`${API_BASE_URL}/admin/diete/custom`, {
                headers: getAuthHeader(),
              }).then((res) => {
                if (!res.ok) throw new Error('Errore caricamento diete custom')
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
        } else {
          if (filter === 'ALL' || filter === 'STANDARD') {
            promises.push(
              fetch(`${API_BASE_URL}/diete/standard`, {
                headers: getAuthHeader(),
              }).then((res) => {
                if (!res.ok)
                  throw new Error('Errore caricamento diete standard')
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
        }
        if (tipoDietaFilter !== 'ALL') {
          result = result.filter((d) => d.tipoDieta === tipoDietaFilter)
        }

        setDiete(result)
        toast.success('Diete caricate con successo')
      } catch (err: any) {
        console.error(err)
        setError(err.message)
        toast.error(err.message || 'Errore nel caricamento delle diete')
        setDiete([])
      } finally {
        setLoading(false)
      }
    }

    fetchDiete()
  }, [user, filter, tipoDietaFilter, canViewPersonalized, isAdmin])

  useEffect(() => {
    setTipoDietaFilter('ALL')
  }, [filter])

  const handleToggleAttiva = async (
    e: React.MouseEvent,
    dietaId: number,
    currentState: boolean
  ) => {
    e.preventDefault()
    e.stopPropagation()

    toast.promise(
      fetch(`${API_BASE_URL}/diete/custom/${dietaId}/attiva`, {
        method: 'PATCH',
        headers: {
          ...getAuthHeader(),
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ isAttiva: !currentState }),
      }).then(async (res) => {
        if (!res.ok) throw new Error("Errore durante l'aggiornamento")

        setDiete((prevDiete) =>
          prevDiete.map((d) => {
            if (!currentState) {
              if (d.id === dietaId) return { ...d, isAttiva: true }
              if (!d.isStandard) return { ...d, isAttiva: false }
              return d
            } else {
              if (d.id === dietaId) return { ...d, isAttiva: false }
              return d
            }
          })
        )
      }),
      {
        loading: 'Aggiornamento in corso...',
        success: currentState
          ? 'Dieta disattivata con successo'
          : 'Dieta attivata con successo',
        error: "Errore durante l'aggiornamento dello stato",
      }
    )
  }

  const handleDelete = (e: React.MouseEvent, dietaId: number, nome: string) => {
    e.preventDefault()
    e.stopPropagation()

    toast('Sei sicuro di voler eliminare questa dieta?', {
      action: {
        label: 'Conferma',
        onClick: async () => {
          const endpoint =
            user?.tipoUtente === 'ADMIN'
              ? `${API_BASE_URL}/admin/diete/${dietaId}`
              : `${API_BASE_URL}/diete/custom/${dietaId}`

          toast.promise(
            fetch(endpoint, {
              method: 'DELETE',
              headers: getAuthHeader(),
            }).then((res) => {
              if (!res.ok) throw new Error("Errore durante l'eliminazione")
              setDiete((prev) => prev.filter((d) => d.id !== dietaId))
            }),
            {
              loading: 'Eliminazione in corso...',
              success: 'Dieta eliminata con successo',
              error: 'Impossibile eliminare la dieta',
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
  if (!user) return <div>Devi essere loggato per vedere le diete.</div>

  return (
    <div className="container mt-4 page-content-general">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h1>Catalogo diete</h1>
        {(user?.tipoPiano === 'SILVER' ||
          user?.tipoPiano === 'GOLD' ||
          user?.tipoPiano === 'PREMIUM') && (
          <Link to="/diete/crea-custom" className="btn btn-success mb-3 mt-3">
            Crea Dieta Custom
          </Link>
        )}
      </div>

      <div className="row mb-3">
        <div className="col-md-6">
          <label className="form-label">Filtra per categoria:</label>
          <select
            className="form-select"
            value={filter}
            onChange={(e) => setFilter(e.target.value as FilterType)}
            disabled={loading}
          >
            {(canViewPersonalized || isAdmin) && (
              <option value="ALL">Tutte</option>
            )}
            <option value="STANDARD">Standard</option>
            {canViewPersonalized && !isAdmin && (
              <option value="PERSONALIZZATE">Le mie Diete</option>
            )}
            {isAdmin && (
              <option value="PERSONALIZZATE">Diete custom utenti</option>
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
      {loading && <p>Caricamento diete...</p>}
      {!loading && !error && diete.length === 0 && (
        <div className="alert alert-info">
          Nessuna dieta trovata per i filtri selezionati.
        </div>
      )}

      {!loading && diete.length > 0 && (
        <div className="row pt-2">
          {diete.map((dieta) => {
            const nomeVisualizzato = dieta.nome || 'Dieta senza nome'
            const descVisualizzata = dieta.descrizione || ''
            const uniqueKey = `${dieta.isStandard ? 'std' : 'cust'}-${dieta.id}`
            const queryType = dieta.isStandard ? 'standard' : 'custom'

            return (
              <div
                key={uniqueKey}
                className="col-12 col-sm-6 col-lg-4 col-xxl-3 mb-4"
              >
                <div className="card h-100 card-diet-wrapper">
                  {(user?.tipoUtente === 'ADMIN' || !dieta?.isStandard) && (
                    <button
                      className="btn-delete-card"
                      onClick={(e) =>
                        handleDelete(e, dieta.id, nomeVisualizzato)
                      }
                      title="Elimina dieta"
                    >
                      <BsTrash />
                    </button>
                  )}

                  <div className="card-body">
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <h5 className="card-title fw-bold mb-0">
                        {nomeVisualizzato}
                      </h5>

                      {!dieta.isStandard && user?.tipoUtente !== 'ADMIN' && (
                        <>
                          {dieta.isAttiva ? (
                            <BsStarFill
                              className="star-active fs-4 ms-2 me-1 mt-1"
                              onClick={(e) =>
                                handleToggleAttiva(e, dieta.id, true)
                              }
                              title="Dieta attiva - Clicca per disattivare"
                              style={{ cursor: 'pointer' }}
                            />
                          ) : (
                            <BsStar
                              className="star-inactive fs-4 ms-2 me-1 mt-1"
                              onClick={(e) =>
                                handleToggleAttiva(e, dieta.id, false)
                              }
                              title="Clicca per attivare questa dieta"
                              style={{ cursor: 'pointer' }}
                            />
                          )}
                        </>
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

                    <p className="card-text">
                      {descVisualizzata.length > 100
                        ? descVisualizzata.substring(0, 100) + '...'
                        : descVisualizzata}
                    </p>
                    <Link
                      to={`/diete/dettaglio/${dieta.id}?type=${queryType}`}
                      className="btn btn-outline-primary"
                    >
                      Dettagli
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
