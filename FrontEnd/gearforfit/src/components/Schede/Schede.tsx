import { useState, useEffect, useMemo } from 'react'
import { useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

type SchedaDTO = {
  id: number
  nome: string
  obiettivo: string
  isStandard: boolean
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
      // Con "Tutte" consideriamo anche il filtro obiettivo
      if (obiettivoFilter === 'ALL') {
        // Nessun filtro obiettivo: mostra tutte le schede
        fetchUrl = `${API_BASE_URL}/schede-allenamento?filtro=ALL`
      } else {
        // Con filtro obiettivo: usiamo il filtro sui parametri
        // ATTENZIONE: dobbiamo creare questo endpoint nel backend
        fetchUrl = `${API_BASE_URL}/schede-allenamento/schede?filtro=ALL&obiettivo=${obiettivoFilter}`
      }
    } else if (filter === 'STANDARD') {
      // Schede standard con o senza filtro obiettivo
      if (obiettivoFilter === 'ALL') {
        fetchUrl = `${API_BASE_URL}/schede-allenamento/standard`
      } else {
        fetchUrl = `${API_BASE_URL}/schede-allenamento/standard/obiettivo/${obiettivoFilter}`
      }
    } else if (filter === 'PERSONALIZZATE') {
      // Schede personalizzate con o senza filtro obiettivo
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

  if (!user) return <div>Devi essere loggato per accedere alle schede.</div>

  return (
    <div className="container mt-4">
      <h1>Catalogo Schede Allenamento</h1>
      <p>Piano attivo: {user.tipoPiano}</p>

      <div className="row mb-3">
        {/* Primo dropdown: Tutte/Standard/Personalizzate */}
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

        {/* Secondo dropdown: Filtro per obiettivo - SEMPRE VISIBILE */}
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
        <div className="row">
          {schede.map((scheda) => (
            <div key={scheda.id} className="col-md-4 mb-3">
              <div className="card h-100">
                <div className="card-body">
                  <h5 className="card-title">{scheda.nome}</h5>
                  {!scheda.isStandard ? (
                    <span className="badge bg-success mb-2">Personale</span>
                  ) : (
                    <span className="badge bg-primary mb-2">Standard</span>
                  )}
                  <span className="badge bg-info mb-2 ms-2">
                    {scheda.obiettivo}
                  </span>
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
          ))}
        </div>
      )}
    </div>
  )
}
