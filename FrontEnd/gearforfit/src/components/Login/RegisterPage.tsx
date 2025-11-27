// src/components/Login/RegisterPage.tsx
import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import { registerUser } from '../../features/auth/authSlice'
import { RootState } from '../../app/store'

export default function RegisterPage() {
  const [nome, setNome] = useState('')
  const [cognome, setCognome] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { isLoading, error } = useSelector((s: RootState) => s.auth)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setSuccessMessage(null) // Resetta messaggio di successo

    if (!nome || !cognome || !email || !password) {
      alert('Compila tutti i campi!')
      return
    }

    try {
      // Dispatch del thunk registerUser
      const resultAction = await dispatch(
        registerUser({ nome, cognome, email, password }) as any
      )

      if (registerUser.fulfilled.match(resultAction)) {
        // Registrazione OK, pulisci i campi e mostra successo
        setNome('')
        setCognome('')
        setEmail('')
        setPassword('')
        setSuccessMessage(
          'Registrazione completata! Ora puoi effettuare il login.'
        )
        // Puoi anche reindirizzare direttamente al login dopo un breve timeout
        setTimeout(() => navigate('/login'), 2000)
      }
    } catch (err) {
      // L'errore è già gestito nello slice (error è aggiornato)
      console.error('Errore durante la registrazione:', err)
    }
  }

  return (
    <div className="page-content-custom-2">
      <div className="card mx-auto" style={{ maxWidth: 520 }}>
        <div className="card-body">
          <h5 className="card-title">Registrazione</h5>
          {/* Mostra errore o successo */}
          {error && <div className="alert alert-danger">{error}</div>}
          {successMessage && (
            <div className="alert alert-success">{successMessage}</div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="mb-2">
              <label className="form-label">Nome</label>
              <input
                className="form-control"
                value={nome}
                onChange={(e) => setNome(e.target.value)}
                required
              />
            </div>

            <div className="mb-2">
              <label className="form-label">Cognome</label>
              <input
                className="form-control"
                value={cognome}
                onChange={(e) => setCognome(e.target.value)}
                required
              />
            </div>

            <div className="mb-2">
              <label className="form-label">Email</label>
              <input
                type="email"
                className="form-control"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="mb-3">
              <label className="form-label">Password</label>
              <input
                type="password"
                className="form-control"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button
              type="submit"
              className="btn btn-success"
              disabled={isLoading}
            >
              {isLoading ? 'Registrazione in corso...' : 'Registrati'}
            </button>
          </form>
          <hr />
          <p className="text-center">
            Sei già registrato? <Link to="/login">Accedi</Link>
          </p>
        </div>
      </div>
    </div>
  )
}
