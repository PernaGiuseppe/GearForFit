import { useState, useEffect, useMemo } from 'react'
import { useSelector } from 'react-redux'
import { Link } from 'react-router-dom'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

type DietaDTO = {
  id: number
  nome?: string
  nomeDietaTemplate?: string
  descrizione?: string
  tipoDieta?: string
  tipoDietaObiettivo?: string
  isStandard?: boolean
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

        if (filter === 'ALL') {
          if (tipoDietaFilter !== 'ALL') {
            const resStandard = await fetch(
              `${API_BASE_URL}/diete/standard/tipo?tipoDieta=${tipoDietaFilter}`,
              { headers: getAuthHeader() }
            )
            if (!resStandard.ok)
              throw new Error('Errore caricamento diete standard')
            const standard = await resStandard.json()
            result.push(...standard)

            if (canViewPersonalized) {
              const resPersonal = await fetch(
                `${API_BASE_URL}/diete/me/dieta/tipo/${tipoDietaFilter}`,
                { headers: getAuthHeader() }
              )
              if (resPersonal.ok) {
                const personal = await resPersonal.json()
                result.push(...personal)
              }
            }
          } else {
            const res = await fetch(`${API_BASE_URL}/diete?filtro=ALL`, {
              headers: getAuthHeader(),
            })
            if (!res.ok) throw new Error('Errore caricamento diete')
            result = await res.json()
          }
        } else if (filter === 'STANDARD') {
          const url =
            tipoDietaFilter === 'ALL'
              ? `${API_BASE_URL}/diete/standard`
              : `${API_BASE_URL}/diete/standard/tipo?tipoDieta=${tipoDietaFilter}`
          const res = await fetch(url, { headers: getAuthHeader() })
          if (!res.ok) throw new Error('Errore caricamento diete standard')
          result = await res.json()
        } else if (filter === 'PERSONALIZZATE') {
          const url =
            tipoDietaFilter === 'ALL'
              ? `${API_BASE_URL}/diete/me/dieta`
              : `${API_BASE_URL}/diete/me/dieta/tipo/${tipoDietaFilter}`
          const res = await fetch(url, { headers: getAuthHeader() })
          if (!res.ok)
            throw new Error('Errore caricamento diete personalizzate')
          result = await res.json()
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

  if (!user) return <div>Devi essere loggato per vedere le diete.</div>

  return (
    <div className="container mt-4">
      <h1>Catalogo Diete</h1>
      <p>Piano attivo: {user.tipoPiano}</p>

      <div className="row mb-4">
        <div className="col-md-6">
          <label>Filtra per categoria:</label>
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
          <label>Filtra per obiettivo:</label>
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
            const nomeVisualizzato =
              dieta.nome || dieta.nomeDietaTemplate || 'Dieta senza nome'
            const descVisualizzata =
              dieta.descrizione ||
              (dieta.tipoDietaObiettivo
                ? `Obiettivo: ${dieta.tipoDietaObiettivo}`
                : '')
            const uniqueKey = dieta.nomeDietaTemplate
              ? `personalizzata-${dieta.id}`
              : `standard-${dieta.id}`

            const isStandard = !dieta.nomeDietaTemplate
            const dietaType = isStandard ? 'standard' : 'custom'

            return (
              <div key={uniqueKey} className="col-md-4 mb-3">
                <div className="card">
                  <div className="card-body">
                    <h5 className="card-title">{nomeVisualizzato}</h5>

                    {dieta.nomeDietaTemplate ? (
                      <span className="badge bg-success me-2">Assegnata</span>
                    ) : (
                      <span className="badge bg-primary me-2">Standard</span>
                    )}

                    {dieta.tipoDieta && (
                      <span className="badge bg-info me-2">
                        {dieta.tipoDieta}
                      </span>
                    )}

                    {dieta.tipoDietaObiettivo && (
                      <span className="badge bg-warning">
                        {dieta.tipoDietaObiettivo}
                      </span>
                    )}

                    <p className="card-text mt-2">{descVisualizzata}</p>
                    <Link
                      to={`/diete/dettaglio/${dieta.id}?type=${dietaType}`}
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
