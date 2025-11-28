import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import '../../css/SchedeAdmin.css'

type Utente = {
  id: number
  nome: string
  cognome: string
  email: string
}

type Esercizio = {
  id: number
  nome: string
  descrizione: string
  urlImmagine: string
  gruppoMuscolare: {
    id: number
    nome: string
  }
  attrezzo: {
    id: number
    nome: string
  }
}

type SerieConfig = {
  esercizioId: number
  numeroSerie: number
  numeroRipetizioni: number
  tempoRecuperoSecondi: number
}

type GiornoAllenamento = {
  giornoSettimana: string
  serie: SerieConfig[]
}

export default function SchedaCustomAdmin() {
  const navigate = useNavigate()
  const user = useSelector((s: RootState) => s.auth.user)

  // State per utenti
  const [utenti, setUtenti] = useState<Utente[]>([])
  const [selectedUtenteId, setSelectedUtenteId] = useState<number | null>(null)
  const [loadingUtenti, setLoadingUtenti] = useState(true)
  const [errorUtenti, setErrorUtenti] = useState<string | null>(null)

  // State per configurazione iniziale
  const [nomeScheda, setNomeScheda] = useState<string>('')
  const [descrizioneScheda, setDescrizioneScheda] = useState<string>('')
  const [obiettivo, setObiettivo] = useState<string>('')
  const [durataSettimane, setDurataSettimane] = useState<number>(6)
  const [numeroGiorni, setNumeroGiorni] = useState<number>(3)
  const [giorniSelezionati, setGiorniSelezionati] = useState<string[]>([])

  // State per wizard multi-step
  const [currentStep, setCurrentStep] = useState<number>(0) // 0 = config, 1+ = giorni
  const [currentDayIndex, setCurrentDayIndex] = useState<number>(0)

  // State per esercizi
  const [esercizi, setEsercizi] = useState<Esercizio[]>([])
  const [loading, setLoading] = useState(false)

  // State per selezione esercizi del giorno corrente
  const [maxEserciziGiorno, setMaxEserciziGiorno] = useState<number>(6)
  const [eserciziSelezionati, setEserciziSelezionati] = useState<number[]>([])
  const [configurazioniSerie, setConfigurazioniSerie] = useState<
    Map<number, SerieConfig>
  >(new Map())

  // State per salvare i giorni completati
  const [giorniCompletati, setGiorniCompletati] = useState<GiornoAllenamento[]>(
    []
  )

  // State per loading submit
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const giorniSettimana = [
    'LUNEDI',
    'MARTEDI',
    'MERCOLEDI',
    'GIOVEDI',
    'VENERDI',
    'SABATO',
  ]
  const obiettivi = ['MASSA', 'DEFINIZIONE', 'MANTENIMENTO']

  if (user?.tipoUtente !== 'ADMIN') {
    return (
      <div className="container">
        <p>Accesso negato. Solo admin possono creare schede.</p>
      </div>
    )
  }

  useEffect(() => {
    const fetchUtenti = async () => {
      setLoadingUtenti(true)
      setErrorUtenti(null)
      try {
        const res = await fetch(`${API_BASE_URL}/admin/utenti`, {
          headers: getAuthHeader(),
        })
        if (!res.ok) throw new Error('Errore caricamento utenti')
        const data = await res.json()
        setUtenti(Array.isArray(data) ? data : [])
      } catch (err: any) {
        setErrorUtenti(err.message)
      } finally {
        setLoadingUtenti(false)
      }
    }

    fetchUtenti()
  }, [])

  useEffect(() => {
    if (currentStep > 0) {
      fetchEsercizi()
    }
  }, [currentStep])

  useEffect(() => {
    if (giorniSelezionati.length > numeroGiorni) {
      setGiorniSelezionati([])
    }
  }, [numeroGiorni])

  useEffect(() => {
    if (eserciziSelezionati.length > maxEserciziGiorno) {
      setEserciziSelezionati([])
      setConfigurazioniSerie(new Map())
    }
  }, [maxEserciziGiorno])

  const fetchEsercizi = async () => {
    setLoading(true)
    try {
      const res = await fetch(`${API_BASE_URL}/schede-allenamento/esercizi`, {
        headers: getAuthHeader(),
      })
      if (res.ok) {
        const data = await res.json()
        setEsercizi(data)
      }
    } catch (err) {
      console.error('Errore caricamento esercizi:', err)
    } finally {
      setLoading(false)
    }
  }

  const handleGiornoToggle = (giorno: string) => {
    if (giorniSelezionati.includes(giorno)) {
      setGiorniSelezionati(giorniSelezionati.filter((g) => g !== giorno))
    } else if (giorniSelezionati.length < numeroGiorni) {
      setGiorniSelezionati([...giorniSelezionati, giorno])
    }
  }

  const handleStartCreation = () => {
    if (!selectedUtenteId) {
      alert('Seleziona un utente prima di proseguire')
      return
    }
    if (!nomeScheda.trim()) {
      alert('Inserisci il nome della scheda')
      return
    }
    if (!obiettivo || giorniSelezionati.length !== numeroGiorni) {
      alert('Completa tutti i campi prima di proseguire')
      return
    }
    const giorniOrdinati = giorniSettimana.filter((g) =>
      giorniSelezionati.includes(g)
    )
    setGiorniSelezionati(giorniOrdinati)
    setCurrentStep(1)
    setCurrentDayIndex(0)
  }

  const handleEsercizioToggle = (id: number) => {
    if (eserciziSelezionati.includes(id)) {
      setEserciziSelezionati(eserciziSelezionati.filter((e) => e !== id))
      const newConfig = new Map(configurazioniSerie)
      newConfig.delete(id)
      setConfigurazioniSerie(newConfig)
    } else if (eserciziSelezionati.length < maxEserciziGiorno) {
      setEserciziSelezionati([...eserciziSelezionati, id])
      const newConfig = new Map(configurazioniSerie)
      newConfig.set(id, {
        esercizioId: id,
        numeroSerie: 3,
        numeroRipetizioni: 10,
        tempoRecuperoSecondi: 90,
      })
      setConfigurazioniSerie(newConfig)
    }
  }

  const handleConfigChange = (
    esercizioId: number,
    field: string,
    value: number
  ) => {
    const newConfig = new Map(configurazioniSerie)
    const current = newConfig.get(esercizioId)
    if (current) {
      newConfig.set(esercizioId, { ...current, [field]: value })
      setConfigurazioniSerie(newConfig)
    }
  }

  const handleNextDay = async () => {
    if (eserciziSelezionati.length === 0) {
      alert('Seleziona almeno un esercizio per questo giorno')
      return
    }

    const serieGiorno: SerieConfig[] = eserciziSelezionati.map(
      (id) => configurazioniSerie.get(id)!
    )
    const giornoAllenamento: GiornoAllenamento = {
      giornoSettimana: giorniSelezionati[currentDayIndex],
      serie: serieGiorno,
    }

    const giorniAggiornati = [...giorniCompletati, giornoAllenamento]

    if (currentDayIndex < giorniSelezionati.length - 1) {
      setGiorniCompletati(giorniAggiornati)
      setCurrentDayIndex(currentDayIndex + 1)
      setEserciziSelezionati([])
      setConfigurazioniSerie(new Map())
      window.scrollTo({ top: 0, behavior: 'smooth' })
    } else {
      setSubmitting(true)
      const payloadFinale = {
        nome: nomeScheda,
        descrizione: descrizioneScheda || undefined,
        obiettivo: obiettivo,
        durataSettimane: durataSettimane,
        giorni: giorniAggiornati,
      }

      try {
        const res = await fetch(
          `${API_BASE_URL}/admin/schede/utente/${selectedUtenteId}`,
          {
            method: 'POST',
            headers: {
              ...getAuthHeader(),
              'Content-Type': 'application/json',
            },
            body: JSON.stringify(payloadFinale),
          }
        )

        if (res.ok) {
          alert('Scheda custom creata e assegnata con successo!')
          navigate('/schede')
        } else {
          const error = await res.text()
          alert(`Errore: ${error}`)
        }
      } catch (err) {
        console.error('Errore creazione scheda:', err)
        alert('Errore durante la creazione della scheda')
      } finally {
        setSubmitting(false)
      }
    }
  }

  const isEsercizioDisabled = (id: number) => {
    return (
      !eserciziSelezionati.includes(id) &&
      eserciziSelezionati.length >= maxEserciziGiorno
    )
  }

  if (currentStep === 0) {
    return (
      <div className="container mt-4 page-content-general ">
        <h1>Crea Scheda Custom per Utente</h1>
        <p className="text-muted">
          Configura una nuova scheda personalizzata e assegnala a un utente
        </p>

        {error && <div className="alert alert-danger mt-3">{error}</div>}

        {/* Sezione Selezione Utente */}
        <div className="mt-4">
          <label className="form-label">Seleziona Utente *</label>

          {loadingUtenti && <p className="text-muted">Caricamento utenti...</p>}
          {errorUtenti && <p className="text-danger">{errorUtenti}</p>}
          {!loadingUtenti && utenti.length === 0 && (
            <p className="text-info">Nessun utente trovato</p>
          )}

          {!loadingUtenti && utenti.length > 0 && (
            <select
              value={selectedUtenteId || ''}
              onChange={(e) => setSelectedUtenteId(Number(e.target.value))}
              required
              className="form-select"
            >
              <option value="">-- Seleziona un utente --</option>
              {utenti.map((u) => (
                <option key={u.id} value={u.id}>
                  Nome: {u.nome} {u.cognome}/ ID: ({u.id})
                </option>
              ))}
            </select>
          )}
        </div>

        {selectedUtenteId && (
          <>
            <div className="row mt-4">
              <div className="col-md-6">
                <label className="form-label">Nome Scheda *</label>
                <input
                  type="text"
                  className="form-control"
                  value={nomeScheda}
                  onChange={(e) => setNomeScheda(e.target.value)}
                  placeholder="Es. Scheda Forza Upper/Lower"
                />
              </div>

              <div className="col-md-6">
                <label className="form-label">Obiettivo *</label>
                <select
                  className="form-select"
                  value={obiettivo}
                  onChange={(e) => setObiettivo(e.target.value)}
                >
                  <option value="">Seleziona obiettivo</option>
                  {obiettivi.map((o) => (
                    <option key={o} value={o}>
                      {o}
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
                  value={descrizioneScheda}
                  onChange={(e) => setDescrizioneScheda(e.target.value)}
                  placeholder="Descrivi la scheda..."
                />
              </div>
            </div>

            <div className="row mt-3">
              <div className="col-md-6">
                <label className="form-label">Durata (settimane) *</label>
                <select
                  className="form-select"
                  value={durataSettimane}
                  onChange={(e) => setDurataSettimane(Number(e.target.value))}
                >
                  {Array.from({ length: 17 }, (_, i) => i + 4).map((n) => (
                    <option key={n} value={n}>
                      {n} settimane
                    </option>
                  ))}
                </select>
              </div>

              <div className="col-md-6">
                <label className="form-label">Numero di giorni *</label>
                <select
                  className="form-select"
                  value={numeroGiorni}
                  onChange={(e) => setNumeroGiorni(Number(e.target.value))}
                >
                  {Array.from({ length: 6 }, (_, i) => i + 1).map((n) => (
                    <option key={n} value={n}>
                      {n} giorni
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="mt-4">
              <label className="form-label">
                Seleziona {numeroGiorni} giorni della settimana *
              </label>
              <div className="d-flex gap-2 flex-wrap">
                {giorniSettimana.map((giorno) => (
                  <button
                    key={giorno}
                    type="button"
                    className={`btn ${
                      giorniSelezionati.includes(giorno)
                        ? 'btn-success'
                        : 'btn-outline-secondary'
                    }`}
                    onClick={() => handleGiornoToggle(giorno)}
                    disabled={
                      !giorniSelezionati.includes(giorno) &&
                      giorniSelezionati.length >= numeroGiorni
                    }
                  >
                    {giorno}
                  </button>
                ))}
              </div>
              <small className="text-muted">
                Selezionati: {giorniSelezionati.length}/{numeroGiorni}
              </small>
            </div>

            <button
              className="btn btn-primary mt-4 mb-3"
              onClick={handleStartCreation}
            >
              Inizia Creazione
            </button>
          </>
        )}
      </div>
    )
  }

  return (
    <div className="container mt-4">
      <h1>Giorno: {giorniSelezionati[currentDayIndex]}</h1>
      <p className="text-muted">
        Giorno {currentDayIndex + 1} di {giorniSelezionati.length}
      </p>

      <div className="mb-3">
        <label className="form-label">
          Numero massimo di esercizi per questo giorno
        </label>
        <select
          className="form-select w-auto"
          value={maxEserciziGiorno}
          onChange={(e) => setMaxEserciziGiorno(Number(e.target.value))}
        >
          {Array.from({ length: 8 }, (_, i) => i + 1).map((n) => (
            <option key={n} value={n}>
              {n} esercizi
            </option>
          ))}
        </select>
        <small className="text-muted">
          Selezionati: {eserciziSelezionati.length}/{maxEserciziGiorno}
        </small>
      </div>

      {loading && <p>Caricamento esercizi...</p>}

      <div className="row">
        {esercizi.map((esercizio) => {
          const isSelected = eserciziSelezionati.includes(esercizio.id)
          const isDisabled = isEsercizioDisabled(esercizio.id)
          const config = configurazioniSerie.get(esercizio.id)

          return (
            <div
              key={esercizio.id}
              className="col-lg-2 col-md-4 col-sm-6 col-6 mb-4"
            >
              <div
                className={`card h-100 esercizio-card ${
                  isSelected ? 'selected' : ''
                } ${isDisabled ? 'disabled' : ''}`}
                onClick={() =>
                  !isDisabled && handleEsercizioToggle(esercizio.id)
                }
              >
                <img
                  src={esercizio.urlImmagine}
                  className="card-img-top mt-3"
                  alt={esercizio.nome}
                  style={{ height: '180px', objectFit: 'cover' }}
                />
                <div className="card-body d-flex flex-column">
                  <h6 className="card-title">{esercizio.nome}</h6>
                  <p className="card-text small mb-3">
                    {esercizio.descrizione}
                  </p>
                  <div className="mt-auto">
                    <p className="card-text small mb-1">
                      <strong>Gruppo:</strong> {esercizio.gruppoMuscolare.nome}
                    </p>
                    <p className="card-text small mb-0">
                      <strong>Attrezzo:</strong> {esercizio.attrezzo.nome}
                    </p>
                  </div>

                  <div
                    className="mt-2"
                    onClick={(e) => e.stopPropagation()}
                    style={{
                      opacity: isSelected ? 1 : 0.5,
                      pointerEvents: isSelected ? 'auto' : 'none',
                      transition: 'opacity 0.2s ease',
                    }}
                  >
                    <label className="form-label small mb-1">Serie</label>
                    <select
                      className="form-select form-select-sm mb-2"
                      value={config?.numeroSerie || 3}
                      onChange={(e) =>
                        handleConfigChange(
                          esercizio.id,
                          'numeroSerie',
                          Number(e.target.value)
                        )
                      }
                      disabled={!isSelected}
                    >
                      {Array.from({ length: 6 }, (_, i) => i + 1).map((n) => (
                        <option key={n} value={n}>
                          {n}
                        </option>
                      ))}
                    </select>

                    <label className="form-label small mb-1">Ripetizioni</label>
                    <select
                      className="form-select form-select-sm mb-2"
                      value={config?.numeroRipetizioni || 10}
                      onChange={(e) =>
                        handleConfigChange(
                          esercizio.id,
                          'numeroRipetizioni',
                          Number(e.target.value)
                        )
                      }
                      disabled={!isSelected}
                    >
                      {Array.from({ length: 17 }, (_, i) => i + 4).map((n) => (
                        <option key={n} value={n}>
                          {n}
                        </option>
                      ))}
                    </select>

                    <label className="form-label small mb-1">
                      Recupero (sec)
                    </label>
                    <select
                      className="form-select form-select-sm"
                      value={config?.tempoRecuperoSecondi || 90}
                      onChange={(e) =>
                        handleConfigChange(
                          esercizio.id,
                          'tempoRecuperoSecondi',
                          Number(e.target.value)
                        )
                      }
                      disabled={!isSelected}
                    >
                      {Array.from({ length: 11 }, (_, i) => 30 + i * 15).map(
                        (n) => (
                          <option key={n} value={n}>
                            {n}s
                          </option>
                        )
                      )}
                    </select>
                  </div>
                </div>
              </div>
            </div>
          )
        })}
      </div>

      <button
        className="btn btn-success mt-4 mb-5"
        onClick={handleNextDay}
        disabled={submitting}
      >
        {submitting
          ? 'Salvataggio...'
          : currentDayIndex < giorniSelezionati.length - 1
          ? 'Avanti al prossimo giorno'
          : 'Completa Scheda'}
      </button>
    </div>
  )
}
