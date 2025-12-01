import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import { loginUser } from '../../features/auth/authSlice'
import { RootState } from '../../app/store'
import { toast } from 'sonner'

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const dispatch = useDispatch()
  const navigate = useNavigate()

  const { isLoading } = useSelector((s: RootState) => s.auth)

  const submit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!email || !password) {
      toast.error('Inserisci email e password')
      return
    }

    const loginPromise = dispatch(loginUser({ email, password }) as any)

    toast.promise(loginPromise, {
      loading: 'Accesso in corso...',
      success: (result) => {
        if (loginUser.fulfilled.match(result)) {
          navigate('/')
          return 'Login effettuato con successo!'
        }
        throw new Error('Login fallito')
      },
      error: (err) => {
        const errorMsg = err?.message || 'Errore durante il login'
        if (errorMsg.includes('account non è attivo')) {
          return "Il tuo account non è attivo, contatta l'admin"
        }
        return errorMsg
      },
    })
  }

  return (
    <div className="page-content-custom-2 mt-4">
      <div className="card mx-auto" style={{ maxWidth: 520 }}>
        <div className="card-body">
          <h5 className="card-title mb-3">Login</h5>

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
