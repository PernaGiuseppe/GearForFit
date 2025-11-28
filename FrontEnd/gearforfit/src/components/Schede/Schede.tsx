import { useState, useEffect, useMemo } from 'react'
import { useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import { BsTrash, BsStar, BsStarFill } from 'react-icons/bs'
import '../../css/Schede.css'
import { toast } from 'sonner'

type SchedaDTO = {
  id: number
  nome: string
  obiettivo: string
  isStandard: boolean
  attiva?: boolean
  utenteId?: number | null
}

type FilterType = 'STANDARD' | 'PERSONALIZZATE' | 'ALL'
type ObiettivoFilter = 'ALL' | 'MASSA' | 'MANTENIMENTO' | 'DEFINIZIONE'

export default function Schede() {
  const user = useSelector((s: RootState) => s.auth.user)
  const [schede, setSchede] = useState<SchedaDTO[]>([])
  const [filter, setFilter] = useState<FilterType>('ALL')
  const [obiettivoFilter, setObiettivoFilter] = useState<ObiettivoFilter>('ALL')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const isAdmin = user?.tipoUtente === 'ADMIN'

  const canViewPersonalized = useMemo(() => {
    if (!user) return false
    const p = user.tipoPiano
    return p === 'GOLD' || p === 'PREMIUM'
  }, [user])

  useEffect(() => {
    if (!user) return
    setLoading(true)
    setError(null)

    let fetchUrl = ''

    if (isAdmin) {
      // ADMIN: Applica filtri con endpoint dedicati
      if (filter === 'STANDARD') {
        if (obiettivoFilter === 'ALL') {
          fetchUrl = `${API_BASE_URL}/admin/schede/standard`
        } else {
          fetchUrl = `${API_BASE_URL}/admin/schede/standard/obiettivo/${obiettivoFilter}`
        }
      } else if (filter === 'PERSONALIZZATE') {
        if (obiettivoFilter === 'ALL') {
          fetchUrl = `${API_BASE_URL}/admin/schede/custom`
        } else {
          fetchUrl = `${API_BASE_URL}/admin/schede/custom/obiettivo/${obiettivoFilter}`
        }
      } else {
        // filter === 'ALL'
        if (obiettivoFilter === 'ALL') {
          fetchUrl = `${API_BASE_URL}/admin/schede`
        } else {
          fetchUrl = `${API_BASE_URL}/admin/schede/obiettivo/${obiettivoFilter}`
        }
      }
    } else {
      // UTENTI NORMALI
      if (filter === 'ALL') {
        if (obiettivoFilter === 'ALL') {
          fetchUrl = `${API_BASE_URL}/schede-allenamento?filtro=ALL`
        } else {
          fetchUrl = `${API_BASE_URL}/schede-allenamento/schede?filtro=ALL&obiettivo=${obiettivoFilter}`
        }
      } else if (filter === 'STANDARD') {
        if (obiettivoFilter === 'ALL') {
          fetchUrl = `${API_BASE_URL}/schede-allenamento/standard`
        } else {
          fetchUrl = `${API_BASE_URL}/schede-allenamento/standard/obiettivo/${obiettivoFilter}`
        }
      } else if (filter === 'PERSONALIZZATE') {
        if (obiettivoFilter === 'ALL') {
          fetchUrl = `${API_BASE_URL}/schede-allenamento/me`
        } else {
          fetchUrl = `${API_BASE_URL}/schede-allenamento/me/obiettivo/${obiettivoFilter}`
        }
      }
    }

    fetch(fetchUrl, {
      headers: getAuthHeader(),
    })
      .then((res) => {
        if (!res.ok) {
          return res.json().then((err) => {
            throw new Error(err.message || 'Errore fetch schede')
          })
        }
        return res.json()
      })
      .then((data) => {
        setSchede(data)
        toast.success('Schede caricate')
      })
      .catch((err) => {
        console.error(err)
        setError(err.message)
        toast.error(err.message || 'Errore nel caricamento delle schede')
      })
      .finally(() => {
        setLoading(false)
      })
  }, [user, filter, obiettivoFilter, isAdmin])

  useEffect(() => {
    setObiettivoFilter('ALL')
  }, [filter])

  // --- LOGICA ATTIVAZIONE ---
  const handleToggleAttiva = async (
    e: React.MouseEvent,
    schedaId: number,
    currentState: boolean
  ) => {
    e.preventDefault()
    e.stopPropagation()

    toast.promise(
      fetch(`${API_BASE_URL}/schede-allenamento/me/schede/${schedaId}/attiva`, {
        method: 'PUT',
        headers: getAuthHeader(),
      }).then((res) => {
        if (!res.ok) throw new Error('Errore aggiornamento stato')

        // Aggiorna lo stato locale
        setSchede((prevSchede) =>
          prevSchede.map((s) => {
            if (!currentState) {
              if (s.id === schedaId) return { ...s, attiva: true }
              if (!s.isStandard) return { ...s, attiva: false }
              return s
            } else {
              if (s.id === schedaId) return { ...s, attiva: false }
              return s
            }
          })
        )
      }),
      {
        loading: 'Aggiornamento stato...',
        success: !currentState ? 'Scheda attivata!' : 'Scheda disattivata!',
        error: 'Impossibile cambiare lo stato della scheda',
      }
    )
  }

  // --- LOGICA ELIMINAZIONE ---

  const handleDelete = (
    e: React.MouseEvent,
    schedaId: number,
    nome: string
  ) => {
    e.preventDefault()
    e.stopPropagation()

    toast('Sei sicuro di voler eliminare questa scheda?', {
      action: {
        label: 'Conferma',
        onClick: async () => {
          const endpoint =
            user?.tipoUtente === 'ADMIN'
              ? `${API_BASE_URL}/admin/schede/${schedaId}`
              : `${API_BASE_URL}/schede-allenamento/me/${schedaId}`

          toast.promise(
            fetch(endpoint, {
              method: 'DELETE',
              headers: getAuthHeader(),
            }).then((res) => {
              if (!res.ok && res.status !== 204)
                throw new Error('Errore eliminazione')
              setSchede((prev) => prev.filter((s) => s.id !== schedaId))
            }),
            {
              loading: 'Eliminazione in corso...',
              success: `Scheda "${nome}" eliminata correttamente`,
              error: "Errore durante l'eliminazione della scheda",
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

  if (!user) return <div>Devi essere loggato per accedere alle schede.</div>

  return (
    <div className="container mt-4 page-content-general">
      <div className="d-flex flex-column flex-lg-row justify-content-between align-items-start align-items-lg-center mb-3 gap-2 gap-lg-3">
        <h1 className="mb-3 mb-lg-0">Catalogo schede allenamento</h1>
        <div className="d-flex flex-column flex-sm-row gap-2">
          {(user?.tipoPiano === 'GOLD' || user?.tipoPiano === 'PREMIUM') && (
            <Link to="/schede/crea-custom" className="btn btn-success">
              Crea scheda personalizzata
            </Link>
          )}
          {isAdmin && (
            <>
              <div className="admin-buttons">
                <Link
                  to="/schede/standard-admin"
                  className="btn btn-primary mb-3 ms-2"
                >
                  Crea scheda standard
                </Link>
                <Link
                  to="/schede/custom-admin"
                  className="btn btn-info mb-3 ms-2"
                >
                  Crea scheda custom per Utente
                </Link>
              </div>
            </>
          )}
        </div>
      </div>

      <div className="row mb-3">
        <div className="col-md-6">
          <label htmlFor="filter-select" className="form-label">
            Filtra per categoria:
          </label>
          <select
            id="filter-select"
            className="form-select"
            value={filter}
            onChange={(e) => setFilter(e.target.value as FilterType)}
            disabled={loading}
          >
            {(canViewPersonalized || isAdmin) && (
              <option value="ALL">Tutte</option>
            )}
            <option value="STANDARD">Standard</option>
            {/* Nasconde "Le mie Schede" per ADMIN perché non ne ha */}
            {canViewPersonalized && !isAdmin && (
              <option value="PERSONALIZZATE">Le mie Schede</option>
            )}
            {/* ADMIN vede schede Custom */}
            {isAdmin && (
              <option value="PERSONALIZZATE">Schede custom utenti</option>
            )}
          </select>
        </div>

        <div className="col-md-6">
          <label htmlFor="obiettivo-filter" className="form-label">
            Filtra per obiettivo:
          </label>
          <select
            id="obiettivo-filter"
            className="form-select"
            value={obiettivoFilter}
            onChange={(e) =>
              setObiettivoFilter(e.target.value as ObiettivoFilter)
            }
            disabled={loading}
          >
            <option value="ALL">Tutte</option>
            <option value="MASSA">Massa</option>
            <option value="MANTENIMENTO">Mantenimento</option>
            <option value="DEFINIZIONE">Definizione</option>
          </select>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {loading && <p>Caricamento schede...</p>}
      {!loading && !error && schede.length === 0 && (
        <div className="alert alert-info">
          Nessuna scheda trovata per i filtri selezionati.
        </div>
      )}

      {!loading && schede.length > 0 && (
        <div className="row pt-2">
          {schede.map((scheda) => {
            const nomeVisualizzato = scheda.nome || 'Scheda senza nome'
            const uniqueKey = `${scheda.isStandard ? 'std' : 'cust'}-${
              scheda.id
            }`

            return (
              <div
                key={uniqueKey}
                className="col-12 col-sm-6 col-lg-4 col-xxl-3 mb-4"
              >
                <div className="card h-100 position-relative">
                  {/* BUTTON DELETE */}
                  {(!scheda.isStandard || user?.tipoUtente === 'ADMIN') && (
                    <button
                      className="btn-delete-button"
                      onClick={(e) =>
                        handleDelete(e, scheda.id, nomeVisualizzato)
                      }
                      title="Elimina scheda"
                    >
                      <BsTrash />
                    </button>
                  )}

                  <div className="card-body">
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <h5 className="card-title fw-bold mb-0">
                        {nomeVisualizzato}
                      </h5>

                      {/* STELLA (Solo per personalizzate) */}
                      {!scheda.isStandard && user?.tipoUtente !== 'ADMIN' && (
                        <>
                          {scheda.attiva ? (
                            <BsStarFill
                              className="star-active fs-4 ms-2 me-1 mt-1 "
                              onClick={(e) =>
                                handleToggleAttiva(e, scheda.id, true)
                              }
                              title="Scheda attiva - Clicca per disattivare"
                              style={{ cursor: 'pointer' }}
                            />
                          ) : (
                            <BsStar
                              className="star-inactive fs-4 ms-2 me-1 mt-1"
                              onClick={(e) =>
                                handleToggleAttiva(e, scheda.id, false)
                              }
                              title="Clicca per attivare questa scheda"
                              style={{ cursor: 'pointer' }}
                            />
                          )}
                        </>
                      )}
                    </div>

                    <div className="mb-3">
                      {!scheda.isStandard ? (
                        <span className="badge bg-success me-2">
                          Personalizzata
                        </span>
                      ) : (
                        <span className="badge bg-primary me-2">Standard</span>
                      )}
                      <span className="badge bg-info text-dark">
                        {scheda.obiettivo}
                      </span>
                    </div>

                    <p className="card-text">Obiettivo: {scheda.obiettivo}</p>
                    <Link
                      to={`/schede/${scheda.id}`}
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
