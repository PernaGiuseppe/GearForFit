import { useState, useEffect, useMemo } from 'react'
import { useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import { BsTrash, BsStar, BsStarFill } from 'react-icons/bs'
import '../../css/Schede.css'

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

  const canViewPersonalized = useMemo(() => {
    if (!user) return false
    const p = user.tipoPiano
    return p === 'GOLD' || p === 'PREMIUM' || user.tipoUtente === 'ADMIN'
  }, [user])

  useEffect(() => {
    if (!user) return

    setLoading(true)
    setError(null)

    let fetchUrl = ''

    // Logica per determinare l'URL corretto
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
      })
      .catch((err) => {
        console.error(err)
        setError(err.message)
      })
      .finally(() => {
        setLoading(false)
      })
  }, [user, filter, obiettivoFilter])

  // Reset del filtro obiettivo quando cambia il filtro principale
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

    try {
      const res = await fetch(
        `${API_BASE_URL}/schede-allenamento/me/schede/${schedaId}/attiva`,
        {
          method: 'PUT',
          headers: getAuthHeader(),
        }
      )

      if (res.ok) {
        // Aggiorna lo stato locale
        setSchede((prevSchede) =>
          prevSchede.map((s) => {
            // Se stiamo attivando questa scheda (currentState era false),
            // allora questa diventa true e TUTTE le altre personalizzate diventano false.
            if (!currentState) {
              if (s.id === schedaId) return { ...s, attiva: true }
              if (!s.isStandard) return { ...s, attiva: false }
              return s
            } else {
              // Se stiamo disattivando questa scheda
              if (s.id === schedaId) return { ...s, attiva: false }
              return s
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
    schedaId: number,
    nome: string
  ) => {
    e.preventDefault()
    e.stopPropagation()

    if (!window.confirm(`Sei sicuro di voler eliminare la scheda "${nome}"?`)) {
      return
    }

    try {
      const res = await fetch(
        `${API_BASE_URL}/schede-allenamento/me/${schedaId}`,
        {
          method: 'DELETE',
          headers: getAuthHeader(),
        }
      )

      if (res.ok || res.status === 204) {
        // Rimuovi dalla lista locale
        setSchede((prev) => prev.filter((s) => s.id !== schedaId))
      } else {
        alert("Errore durante l'eliminazione della scheda")
      }
    } catch (err) {
      console.error('Errore delete:', err)
      alert('Errore di connessione')
    }
  }

  if (!user) return <div>Devi essere loggato per accedere alle schede.</div>

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h1>Catalogo Schede Allenamento</h1>
        {(user?.tipoPiano === 'GOLD' || user?.tipoPiano === 'PREMIUM') && (
          <Link to="/schede/crea-custom" className="btn btn-success">
            <i className="bi bi-plus-circle me-2"></i>
            Crea scheda personalizzata
          </Link>
        )}
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
            {canViewPersonalized && <option value="ALL">Tutte</option>}
            <option value="STANDARD">Standard</option>
            {canViewPersonalized && (
              <option value="PERSONALIZZATE">Le mie Schede</option>
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
            <option value="ALL">Tutti</option>
            <option value="MASSA">Massa</option>
            <option value="MANTENIMENTO">Mantenimento</option>
            <option value="DEFINIZIONE">Definizione</option>
          </select>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {loading && <p>Caricamento schede...</p>}
      {!loading && !error && schede.length === 0 && (
        <p>Nessuna scheda trovata per i filtri selezionati.</p>
      )}

      {!loading && schede.length > 0 && (
        <div className="row pt-2">
          {schede.map((scheda) => {
            const nomeVisualizzato = scheda.nome || 'Scheda senza nome'
            const uniqueKey = `${scheda.isStandard ? 'std' : 'cust'}-${
              scheda.id
            }`

            return (
              <div key={uniqueKey} className="col-md-4 mb-4">
                <div className="card h-100 position-relative">
                  {/* BUTTON DELETE (Solo per personalizzate) */}
                  {!scheda.isStandard && (
                    <button
                      className="btn-delete-card"
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
                      {!scheda.isStandard && (
                        <>
                          {scheda.attiva ? (
                            <BsStarFill
                              className="star-active fs-4 ms-2"
                              onClick={(e) =>
                                handleToggleAttiva(e, scheda.id, true)
                              }
                              title="Scheda attiva - Clicca per disattivare"
                              style={{ cursor: 'pointer' }}
                            />
                          ) : (
                            <BsStar
                              className="star-inactive fs-4 ms-2"
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
