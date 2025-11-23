// src/components/Login/LoginPage.tsx
import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
// Importa l'azione asincrona (thunk)
import { loginUser } from '../../features/auth/authSlice'
import { RootState } from '../../app/store'

export default function LoginPage() {
  const [email, setEmail] = useState('') // Inizializza vuoto
  const [password, setPassword] = useState('') // Aggiunto stato per la password
  const dispatch = useDispatch()
  const navigate = useNavigate()
  // Ottieni lo stato di caricamento e l'errore dallo slice
  const { isLoading, error } = useSelector((s: RootState) => s.auth)

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!email || !password) {
      alert('Inserisci email e password.')
      return
    }

    try {
      // Dispatch del thunk loginUser
      const resultAction = await dispatch(
        loginUser({ email, password }) as any // Il thunk deve essere castato come 'any' per i tipi di Redux
      )

      // Verifica se il login è avvenuto con successo
      if (loginUser.fulfilled.match(resultAction)) {
        // Redirigi alla home solo se l'operazione ha avuto successo
        navigate('/')
      }
    } catch (err) {
      // Gli errori sono già gestiti nello slice (error è aggiornato)
      console.error('Errore durante il login:', err)
    }
  }

  return (
    <div className="card mx-auto" style={{ maxWidth: 520 }}>
      <div className="card-body">
        <h5 className="card-title">Login</h5>
        {/* Mostra errore se presente */}
        {error && <div className="alert alert-danger">{error}</div>}
        <form onSubmit={submit}>
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
            className="btn btn-primary"
            disabled={isLoading}
          >
            {isLoading ? 'Accesso in corso...' : 'Login'}
          </button>
        </form>
        <hr />
        <p className="text-center">
          Non hai un account? <Link to="/register">Registrati</Link>
        </p>
      </div>
    </div>
  )
}
