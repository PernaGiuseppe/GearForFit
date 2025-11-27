import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import { loginUser } from '../../features/auth/authSlice'
import { RootState } from '../../app/store'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const dispatch = useDispatch()
  const navigate = useNavigate()

  const { isLoading, error } = useSelector((s: RootState) => s.auth)

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!email || !password) {
      alert('Inserisci email e password.')
      return
    }

    try {
      const resultAction = await dispatch(loginUser({ email, password }) as any)
      if (loginUser.fulfilled.match(resultAction)) {
        navigate('/')
      }
      // Se fallisce, l'errore viene salvato nello state 'error' di Redux
      //  e verrà renderizzato nel componente sotto.
    } catch (err) {
      console.error('Errore durante il login:', err)
    }
  }

  // Funzione helper per determinare il messaggio da mostrare
  const getErrorMessage = (errorMsg: string | null) => {
    if (!errorMsg) return null

    // Controlla se il messaggio contiene la stringa specifica del backend
    if (errorMsg.includes('account non è attivo')) {
      return "Errore login, il tuo account non è attivo, contattare l'admin"
    }

    return errorMsg
  }

  const displayError = getErrorMessage(error)

  return (
    <div className="page-content-custom-2">
      <div className="card mx-auto " style={{ maxWidth: 520 }}>
        <div className="card-body">
          <h5 className="card-title">Login</h5>

          {/* Mostra Alert Errore */}
          {displayError && (
            <div
              className={`alert ${
                displayError.includes('non è attivo')
                  ? 'alert-warning'
                  : 'alert-danger'
              }`}
            >
              {displayError}
            </div>
          )}

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
    </div>
  )
}
