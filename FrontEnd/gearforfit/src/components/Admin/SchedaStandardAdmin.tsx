import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import '../../css/SchedaCustom.css'

type Esercizio = {
  nomeEsercizio: string
  numeroSerie: number
  numeroRipetizioni: number
  tempoRecuperoSecondi: number
}

type Giorno = {
  nomeGiorno: string
  esercizi: Esercizio[]
}

type SchedaPersonalizzataRequestDTO = {
  nome: string
  descrizione: string
  obiettivo: 'MASSA' | 'MANTENIMENTO' | 'DEFINIZIONE'
  giorni: Giorno[]
}

export default function SchedaStandardAdmin() {
  const navigate = useNavigate()
  const user = useSelector((s: RootState) => s.auth.user)

  const [nome, setNome] = useState('')
  const [descrizione, setDescrizione] = useState('')
  const [obiettivo, setObiettivo] = useState<
    'MASSA' | 'MANTENIMENTO' | 'DEFINIZIONE'
  >('MASSA')
  const [giorni, setGiorni] = useState<Giorno[]>([
    {
      nomeGiorno: 'Lunedì',
      esercizi: [
        {
          nomeEsercizio: '',
          numeroSerie: 3,
          numeroRipetizioni: 8,
          tempoRecuperoSecondi: 60,
        },
      ],
    },
  ])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  if (user?.tipoUtente !== 'ADMIN') {
    return (
      <div className="container">
        <p>Accesso negato. Solo admin possono creare schede.</p>
      </div>
    )
  }

  const aggiungiGiorno = () => {
    setGiorni([
      ...giorni,
      {
        nomeGiorno: `Giorno ${giorni.length + 1}`,
        esercizi: [
          {
            nomeEsercizio: '',
            numeroSerie: 3,
            numeroRipetizioni: 8,
            tempoRecuperoSecondi: 60,
          },
        ],
      },
    ])
  }

  const rimuoviGiorno = (index: number) => {
    if (giorni.length > 1) setGiorni(giorni.filter((_, i) => i !== index))
  }

  const aggiungiEsercizio = (giornoIndex: number) => {
    const newGiorni = [...giorni]
    newGiorni[giornoIndex].esercizi.push({
      nomeEsercizio: '',
      numeroSerie: 3,
      numeroRipetizioni: 8,
      tempoRecuperoSecondi: 60,
    })
    setGiorni(newGiorni)
  }

  const rimuoviEsercizio = (giornoIndex: number, esercizioIndex: number) => {
    const newGiorni = [...giorni]
    if (newGiorni[giornoIndex].esercizi.length > 1) {
      newGiorni[giornoIndex].esercizi = newGiorni[giornoIndex].esercizi.filter(
        (_, i) => i !== esercizioIndex
      )
      setGiorni(newGiorni)
    }
  }

  const updateEsercizio = (
    giornoIndex: number,
    esercizioIndex: number,
    field: string,
    value: any
  ) => {
    const newGiorni = [...giorni]
    newGiorni[giornoIndex].esercizi[esercizioIndex] = {
      ...newGiorni[giornoIndex].esercizi[esercizioIndex],
      [field]: value,
    }
    setGiorni(newGiorni)
  }

  const updateGiorno = (index: number, field: string, value: any) => {
    const newGiorni = [...giorni]
    newGiorni[index] = { ...newGiorni[index], [field]: value }
    setGiorni(newGiorni)
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError(null)

    try {
      const body: SchedaPersonalizzataRequestDTO = {
        nome,
        descrizione,
        obiettivo,
        giorni,
      }

      const res = await fetch(`${API_BASE_URL}/admin/schede/standard`, {
        method: 'POST',
        headers: {
          ...getAuthHeader(),
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
      })

      if (!res.ok) {
        const errorData = await res.json()
        throw new Error(
          errorData.message || 'Errore nella creazione della scheda'
        )
      }

      alert('Scheda standard creata con successo!')
      navigate('/schede')
    } catch (err: any) {
      console.error('Errore submit:', err)
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container scheda-custom-container">
      <div className="scheda-custom-header">
        <h1>Crea Scheda Standard</h1>
        <p>Configura una nuova scheda di allenamento standard</p>
      </div>

      <form onSubmit={handleSubmit} className="scheda-custom-form">
        {error && <div className="alert alert-danger">{error}</div>}

        {/* Sezione Informazioni Principali */}
        <div className="form-section">
          <h2>Informazioni Scheda</h2>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="nome">Nome Scheda *</label>
              <input
                id="nome"
                type="text"
                value={nome}
                onChange={(e) => setNome(e.target.value)}
                required
                placeholder="Es: Full Body Monday"
                className="form-control"
              />
            </div>

            <div className="form-group">
              <label htmlFor="obiettivo">Obiettivo *</label>
              <select
                id="obiettivo"
                value={obiettivo}
                onChange={(e) => setObiettivo(e.target.value as any)}
                className="form-control"
              >
                <option value="MASSA">Massa</option>
                <option value="MANTENIMENTO">Mantenimento</option>
                <option value="DEFINIZIONE">Definizione</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="descrizione">Descrizione</label>
            <textarea
              id="descrizione"
              value={descrizione}
              onChange={(e) => setDescrizione(e.target.value)}
              rows={3}
              placeholder="Descrivi questa scheda..."
              className="form-control"
            />
          </div>
        </div>

        {/* Sezione Giorni di Allenamento */}
        <div className="form-section">
          <div className="section-header">
            <h2>Giorni di Allenamento</h2>
            <button
              type="button"
              onClick={aggiungiGiorno}
              className="btn btn-secondary btn-sm"
            >
              + Aggiungi Giorno
            </button>
          </div>

          <div className="giorni-list">
            {giorni.map((giorno, giornoIndex) => (
              <div key={giornoIndex} className="giorno-card">
                <div className="giorno-header-row">
                  <input
                    type="text"
                    value={giorno.nomeGiorno}
                    onChange={(e) =>
                      updateGiorno(giornoIndex, 'nomeGiorno', e.target.value)
                    }
                    placeholder="Es: Lunedì"
                    className="form-control giorno-input"
                  />
                  {giorni.length > 1 && (
                    <button
                      type="button"
                      onClick={() => rimuoviGiorno(giornoIndex)}
                      className="btn btn-danger btn-sm"
                    >
                      Rimuovi Giorno
                    </button>
                  )}
                </div>

                {/* Esercizi */}
                <div className="esercizi-container">
                  <div className="esercizi-header">
                    <span>Esercizi</span>
                    <button
                      type="button"
                      onClick={() => aggiungiEsercizio(giornoIndex)}
                      className="btn btn-secondary btn-sm"
                    >
                      + Esercizio
                    </button>
                  </div>

                  {giorno.esercizi.map((esercizio, esercizioIndex) => (
                    <div key={esercizioIndex} className="esercizio-input-row">
                      <input
                        type="text"
                        value={esercizio.nomeEsercizio}
                        onChange={(e) =>
                          updateEsercizio(
                            giornoIndex,
                            esercizioIndex,
                            'nomeEsercizio',
                            e.target.value
                          )
                        }
                        placeholder="Nome esercizio"
                        required
                        className="form-control esercizio-nome"
                      />
                      <input
                        type="number"
                        value={esercizio.numeroSerie}
                        onChange={(e) =>
                          updateEsercizio(
                            giornoIndex,
                            esercizioIndex,
                            'numeroSerie',
                            Number(e.target.value)
                          )
                        }
                        placeholder="Serie"
                        min="1"
                        className="form-control esercizio-numero"
                      />
                      <input
                        type="number"
                        value={esercizio.numeroRipetizioni}
                        onChange={(e) =>
                          updateEsercizio(
                            giornoIndex,
                            esercizioIndex,
                            'numeroRipetizioni',
                            Number(e.target.value)
                          )
                        }
                        placeholder="Reps"
                        min="1"
                        className="form-control esercizio-numero"
                      />
                      <input
                        type="number"
                        value={esercizio.tempoRecuperoSecondi}
                        onChange={(e) =>
                          updateEsercizio(
                            giornoIndex,
                            esercizioIndex,
                            'tempoRecuperoSecondi',
                            Number(e.target.value)
                          )
                        }
                        placeholder="Recupero (s)"
                        min="0"
                        className="form-control esercizio-numero"
                      />
                      {giorno.esercizi.length > 1 && (
                        <button
                          type="button"
                          onClick={() =>
                            rimuoviEsercizio(giornoIndex, esercizioIndex)
                          }
                          className="btn btn-danger btn-sm"
                        >
                          Rimuovi
                        </button>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Pulsanti Azione */}
        <div className="form-actions">
          <button
            type="submit"
            disabled={loading}
            className="btn btn-primary btn-lg"
          >
            {loading ? 'Creazione in corso...' : 'Crea Scheda Standard'}
          </button>
          <button
            type="button"
            onClick={() => navigate('/schede')}
            className="btn btn-outline btn-lg"
          >
            Annulla
          </button>
        </div>
      </form>
    </div>
  )
}
