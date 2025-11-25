import { useState, useEffect, useMemo } from 'react'
import { useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

// Aggiornato il DTO per rispecchiare il backend unificato
type DietaDTO = {
  id: number
  nome: string
  descrizione?: string
  tipoDieta: string // IPOCALORICA, NORMOCALORICA, ecc.
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

        // Array di promesse per gestire le chiamate parallele se serve
        const promises = []

        // 1. Logica per caricare DIETE STANDARD
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

        // 2. Logica per caricare DIETE PERSONALIZZATE (CUSTOM)
        // Solo se l'utente ha i permessi e il filtro lo richiede
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

        // Eseguiamo le chiamate
        const responses = await Promise.all(promises)

        // Uniamo i risultati (standard + custom se presenti)
        responses.forEach((data) => {
          if (Array.isArray(data)) {
            result = [...result, ...data]
          }
        })

        // 3. Filtraggio Lato Client (Client-side filtering)
        // Il backend non espone endpoint di filtro per tipo, lo facciamo qui.
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

  // Reset del filtro tipo quando cambio categoria principale
  useEffect(() => {
    setTipoDietaFilter('ALL')
  }, [filter])

  if (!user) return <div>Devi essere loggato per vedere le diete.</div>

  return (
    <div className="container mt-4">
      <h1>Catalogo Diete</h1>
      <p>Piano attivo: {user.tipoPiano}</p>

      <div className="row mb-3">
        <div className="col-md-6">
          <label htmlFor="filter-select" className="form-label">
            Filtra per categoria:
          </label>
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
          <label htmlFor="filter-select" className="form-label">
            Filtra per obiettivo:
          </label>
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

      {!loading && diete.length > 0 && (
        <div className="row">
          {diete.map((dieta) => {
            // Nel nuovo sistema il nome è sempre in 'nome'.
            // isStandard determina se è standard o custom.
            const nomeVisualizzato = dieta.nome || 'Dieta senza nome'
            const descVisualizzata = dieta.descrizione || ''

            // Chiave unica combinando ID e tipo per evitare conflitti React
            const uniqueKey = `${dieta.isStandard ? 'std' : 'cust'}-${dieta.id}`

            // Definiamo il type param per la navigazione
            const queryType = dieta.isStandard ? 'standard' : 'custom'

            return (
              <div key={uniqueKey} className="col-md-4 mb-3">
                <div className="card">
                  <div className="card-body">
                    <h5 className="card-title">{nomeVisualizzato}</h5>

                    {!dieta.isStandard ? (
                      <span className="badge bg-success me-2">Assegnata</span>
                    ) : (
                      <span className="badge bg-primary me-2">Standard</span>
                    )}

                    {dieta.tipoDieta && (
                      <span className="badge bg-info me-2">
                        {dieta.tipoDieta}
                      </span>
                    )}

                    {dieta.isAttiva && (
                      <span className="badge bg-warning text-dark ms-1">
                        Attiva
                      </span>
                    )}

                    <p className="card-text mt-2">{descVisualizzata}</p>
                    <Link
                      to={`/diete/dettaglio/${dieta.id}?type=${queryType}`}
                      className="btn btn-primary"
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
