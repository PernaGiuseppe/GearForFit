import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { useNavigate } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

type DietaStandard = {
  id: number
  nome: string
  descrizione?: string
  tipoDieta: string
}

export default function DietaCustom() {
  const user = useSelector((s: RootState) => s.auth.user)
  const navigate = useNavigate()

  const [dieteStandard, setDieteStandard] = useState<DietaStandard[]>([])
  const [loadingDiete, setLoadingDiete] = useState(false)

  const [dietaStandardId, setDietaStandardId] = useState<number | ''>('')
  const [nome, setNome] = useState<string>('')
  const [descrizione, setDescrizione] = useState<string>('')
  const [peso, setPeso] = useState<number | ''>('')
  const [altezza, setAltezza] = useState<number | ''>('')
  const [eta, setEta] = useState<number | ''>('')
  const [sesso, setSesso] = useState<string>('')
  const [livelloAttivita, setLivelloAttivita] = useState<string>('')
  const [tipoDieta, setTipoDieta] = useState<string>('')

  const [submitting, setSubmitting] = useState(false)

  const livelliAttivita = [
    'SEDENTARIO',
    'LEGGERO',
    'MODERATO',
    'INTENSO',
    'MOLTO_INTENSO',
  ]
  const tipiDieta = ['IPOCALORICA', 'NORMOCALORICA', 'IPERCALORICA']

  useEffect(() => {
    if (
      !user ||
      (user.tipoPiano !== 'SILVER' &&
        user.tipoPiano !== 'GOLD' &&
        user.tipoPiano !== 'PREMIUM')
    ) {
      navigate('/diete')
    } else {
      fetchDieteStandard()
    }
  }, [user, navigate])

  const fetchDieteStandard = async () => {
    setLoadingDiete(true)
    try {
      const res = await fetch(`${API_BASE_URL}/diete/standard`, {
        headers: getAuthHeader(),
      })
      if (res.ok) {
        const data = await res.json()
        setDieteStandard(data)
      }
    } catch (err) {
      console.error('Errore caricamento diete standard:', err)
    } finally {
      setLoadingDiete(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (
      !dietaStandardId ||
      !nome.trim() ||
      !peso ||
      !altezza ||
      !eta ||
      !sesso ||
      !livelloAttivita ||
      !tipoDieta
    ) {
      alert('Compila tutti i campi obbligatori')
      return
    }

    setSubmitting(true)

    const payload = {
      nome,
      descrizione: descrizione || undefined,
      peso: Number(peso),
      altezza: Number(altezza),
      eta: Number(eta),
      sesso,
      livelloAttivita,
      tipoDieta,
    }

    try {
      const res = await fetch(
        `${API_BASE_URL}/diete/standard/${dietaStandardId}/custom`,
        {
          method: 'POST',
          headers: {
            ...getAuthHeader(),
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(payload),
        }
      )

      if (res.ok) {
        alert('Dieta personalizzata creata con successo!')
        navigate('/diete')
      } else {
        const error = await res.text()
        alert(`Errore: ${error}`)
      }
    } catch (err) {
      console.error('Errore creazione dieta custom:', err)
      alert('Errore durante la creazione della dieta')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="container mt-4">
      <h1>Crea la tua Dieta Personalizzata</h1>
      <p className="text-muted">
        Scegli una dieta standard come base e personalizzala secondo le tue
        esigenze
      </p>

      <form onSubmit={handleSubmit}>
        {/* Selezione dieta standard */}
        <div className="row mt-4">
          <div className="col-md-12">
            <label className="form-label">
              Dieta Standard di riferimento *
            </label>
            {loadingDiete ? (
              <p>Caricamento diete...</p>
            ) : (
              <select
                className="form-select"
                value={dietaStandardId}
                onChange={(e) => setDietaStandardId(Number(e.target.value))}
                required
              >
                <option value="">Seleziona una dieta</option>
                {dieteStandard.map((dieta) => (
                  <option key={dieta.id} value={dieta.id}>
                    {dieta.nome} - {dieta.tipoDieta}
                  </option>
                ))}
              </select>
            )}
            <small className="text-muted">
              La dieta standard sarà utilizzata come modello per creare la tua
              dieta personalizzata
            </small>
          </div>
        </div>

        {/* Nome e descrizione */}
        <div className="row mt-3">
          <div className="col-md-6">
            <label className="form-label">Nome Dieta *</label>
            <input
              type="text"
              className="form-control"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              placeholder="Es. La mia dieta per definizione"
              required
            />
          </div>
          <div className="col-md-6">
            <label className="form-label">Tipo Dieta *</label>
            <select
              className="form-select"
              value={tipoDieta}
              onChange={(e) => setTipoDieta(e.target.value)}
              required
            >
              <option value="">Seleziona tipo</option>
              {tipiDieta.map((tipo) => (
                <option key={tipo} value={tipo}>
                  {tipo}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="row mt-3">
          <div className="col-md-12">
            <label className="form-label">Descrizione (opzionale)</label>
            <textarea
              className="form-control"
              rows={3}
              value={descrizione}
              onChange={(e) => setDescrizione(e.target.value)}
              placeholder="Descrivi la tua dieta personalizzata"
            />
          </div>
        </div>

        <div className="row mt-3">
          <div className="col-md-3">
            <label className="form-label">Peso (kg) *</label>
            <input
              type="number"
              step="0.1"
              className="form-control"
              value={peso}
              onChange={(e) =>
                setPeso(e.target.value ? Number(e.target.value) : '')
              }
              placeholder="75.0"
              required
            />
          </div>
          <div className="col-md-3">
            <label className="form-label">Altezza (cm) *</label>
            <input
              type="number"
              className="form-control"
              value={altezza}
              onChange={(e) =>
                setAltezza(e.target.value ? Number(e.target.value) : '')
              }
              placeholder="180"
              required
            />
          </div>
          <div className="col-md-3">
            <label className="form-label">Età *</label>
            <input
              type="number"
              className="form-control"
              value={eta}
              onChange={(e) =>
                setEta(e.target.value ? Number(e.target.value) : '')
              }
              placeholder="25"
              required
            />
          </div>
          <div className="col-md-3">
            <label className="form-label">Sesso *</label>
            <select
              className="form-select"
              value={sesso}
              onChange={(e) => setSesso(e.target.value)}
              required
            >
              <option value="">Seleziona</option>
              <option value="M">Maschio</option>
              <option value="F">Femmina</option>
            </select>
          </div>
        </div>

        {/* Livello attività */}
        <div className="row mt-3">
          <div className="col-md-12">
            <label className="form-label">Livello di Attività Fisica *</label>
            <select
              className="form-select"
              value={livelloAttivita}
              onChange={(e) => setLivelloAttivita(e.target.value)}
              required
            >
              <option value="">Seleziona livello</option>
              {livelliAttivita.map((livello) => (
                <option key={livello} value={livello}>
                  {livello.replace('_', ' ')}
                </option>
              ))}
            </select>
            <small className="text-muted">
              Il livello di attività sarà utilizzato per calcolare il tuo
              fabbisogno calorico
            </small>
          </div>
        </div>

        {/* Bottoni */}
        <div className="mt-4 mb-5">
          <button
            type="submit"
            className="btn btn-success me-2"
            disabled={submitting}
          >
            {submitting ? 'Creazione in corso...' : 'Crea Dieta Personalizzata'}
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => navigate('/diete')}
          >
            Annulla
          </button>
        </div>
      </form>
    </div>
  )
}
