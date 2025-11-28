import { useState, useEffect } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { useNavigate } from 'react-router-dom'
import { RootState } from '../../app/store'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import { logout } from '../../features/auth/authSlice'

type ProfiloDTO = {
  id: number
  nome: string
  cognome: string
  email: string
  tipoUtente: string
  tipoPiano: string
}

export default function ProfiloUtente() {
  const user = useSelector((s: RootState) => s.auth.user)
  const dispatch = useDispatch()
  const navigate = useNavigate()

  const [profilo, setProfilo] = useState<ProfiloDTO | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  // Form modifica dati
  const [editMode, setEditMode] = useState(false)
  const [nome, setNome] = useState('')
  const [cognome, setCognome] = useState('')
  const [email, setEmail] = useState('')

  // Form cambio password
  const [passwordMode, setPasswordMode] = useState(false)
  const [passwordVecchia, setPasswordVecchia] = useState('')
  const [passwordNuova, setPasswordNuova] = useState('')

  // Messaggi di successo/errore
  const [successMsg, setSuccessMsg] = useState<string | null>(null)

  // Carica i dati del profilo
  useEffect(() => {
    if (!user) return

    fetch(`${API_BASE_URL}/utenti/me`, {
      headers: getAuthHeader(),
    })
      .then((res) => {
        if (!res.ok) throw new Error('Errore caricamento profilo')
        return res.json()
      })
      .then((data) => {
        setProfilo(data)
        setNome(data.nome)
        setCognome(data.cognome)
        setEmail(data.email)
      })
      .catch((err) => {
        console.error(err)
        setError(err.message)
      })
      .finally(() => {
        setLoading(false)
      })
  }, [user])

  // Aggiorna profilo
  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setSuccessMsg(null)

    try {
      const res = await fetch(`${API_BASE_URL}/utenti/me`, {
        method: 'PUT',
        headers: {
          ...getAuthHeader(),
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ nome, cognome, email }),
      })

      if (!res.ok) {
        const errData = await res.json()
        throw new Error(errData.message || 'Errore aggiornamento profilo')
      }

      const updated = await res.json()
      setProfilo(updated)
      setEditMode(false)
      setSuccessMsg('Profilo aggiornato con successo!')
    } catch (err: any) {
      setError(err.message)
    }
  }

  // Cambia password
  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setSuccessMsg(null)

    try {
      const res = await fetch(`${API_BASE_URL}/utenti/me/password`, {
        method: 'PUT',
        headers: {
          ...getAuthHeader(),
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ passwordVecchia, passwordNuova }),
      })

      if (!res.ok) {
        const errData = await res.json()
        throw new Error(errData.message || 'Errore cambio password')
      }

      setPasswordMode(false)
      setPasswordVecchia('')
      setPasswordNuova('')
      setSuccessMsg('Password cambiata con successo!')
    } catch (err: any) {
      setError(err.message)
    }
  }

  // Elimina account
  const handleDeleteAccount = async () => {
    if (
      !window.confirm(
        'Sei sicuro di voler eliminare il tuo account? Questa azione è irreversibile.'
      )
    ) {
      return
    }

    try {
      const res = await fetch(`${API_BASE_URL}/utenti/me`, {
        method: 'DELETE',
        headers: getAuthHeader(),
      })

      if (!res.ok) {
        throw new Error('Errore eliminazione account')
      }

      // Logout e redirect
      dispatch(logout())
      navigate('/login')
    } catch (err: any) {
      setError(err.message)
    }
  }

  if (!user)
    return (
      <div className="container mt-4">
        Devi essere loggato per vedere questa pagina.
      </div>
    )
  if (loading)
    return <div className="container mt-4">Caricamento profilo...</div>
  if (error && !profilo)
    return <div className="container mt-4 alert alert-danger">{error}</div>

  return (
    <div className="container page-content-general">
      <h1>Il Mio Profilo</h1>

      {successMsg && <div className="alert alert-success">{successMsg}</div>}
      {error && <div className="alert alert-danger">{error}</div>}

      {/* Informazioni Base */}
      <div className="card mb-4">
        <div className="card-header bg-primary text-white">
          <h5 className="mb-0">Informazioni Personali</h5>
        </div>
        <div className="card-body">
          {!editMode ? (
            <>
              <p>
                <strong>Nome:</strong> {profilo?.nome}
              </p>
              <p>
                <strong>Cognome:</strong> {profilo?.cognome}
              </p>
              <p>
                <strong>Email:</strong> {profilo?.email}
              </p>
              <p>
                <strong>Piano Attivo:</strong>{' '}
                <span className="badge bg-info">{profilo?.tipoPiano}</span>
              </p>
              <p>
                <strong>Ruolo:</strong>{' '}
                <span className="badge bg-secondary">
                  {profilo?.tipoUtente}
                </span>
              </p>
              <button
                className="btn btn-primary"
                onClick={() => setEditMode(true)}
              >
                Modifica Dati
              </button>
            </>
          ) : (
            <form onSubmit={handleUpdateProfile}>
              <div className="mb-3">
                <label className="form-label">Nome</label>
                <input
                  type="text"
                  className="form-control"
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  required
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Cognome</label>
                <input
                  type="text"
                  className="form-control"
                  value={cognome}
                  onChange={(e) => setCognome(e.target.value)}
                  required
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Email</label>
                <input
                  type="email"
                  className="form-control"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
              <button type="submit" className="btn btn-success me-2">
                Salva Modifiche
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => {
                  setEditMode(false)
                  setNome(profilo?.nome || '')
                  setCognome(profilo?.cognome || '')
                  setEmail(profilo?.email || '')
                }}
              >
                Annulla
              </button>
            </form>
          )}
        </div>
      </div>

      {/* Cambio Password */}
      <div className="card mb-4">
        <div className="card-header bg-warning">
          <h5 className="mb-0">Sicurezza</h5>
        </div>
        <div className="card-body">
          {!passwordMode ? (
            <button
              className="btn btn-warning"
              onClick={() => setPasswordMode(true)}
            >
              Cambia Password
            </button>
          ) : (
            <form onSubmit={handleChangePassword}>
              <div className="mb-3">
                <label className="form-label">Password Attuale</label>
                <input
                  type="password"
                  className="form-control"
                  value={passwordVecchia}
                  onChange={(e) => setPasswordVecchia(e.target.value)}
                  required
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Nuova Password</label>
                <input
                  type="password"
                  className="form-control"
                  value={passwordNuova}
                  onChange={(e) => setPasswordNuova(e.target.value)}
                  required
                  minLength={6}
                />
              </div>
              <button type="submit" className="btn btn-success me-2">
                Cambia Password
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => {
                  setPasswordMode(false)
                  setPasswordVecchia('')
                  setPasswordNuova('')
                }}
              >
                Annulla
              </button>
            </form>
          )}
        </div>
      </div>

      {/* Elimina Account */}
      <div className="card border-danger">
        <div className="card-header bg-danger text-white">
          <h5 className="mb-0">Zona Pericolosa</h5>
        </div>
        <div className="card-body">
          <p className="text-muted">
            L'eliminazione dell'account è permanente e non può essere annullata.
          </p>
          <button className="btn btn-danger" onClick={handleDeleteAccount}>
            Elimina Account
          </button>
        </div>
      </div>
    </div>
  )
}
