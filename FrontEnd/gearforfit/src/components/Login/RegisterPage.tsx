import React, { useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import { registerUser } from '../../features/auth/authSlice'
import { RootState } from '../../app/store'
import { toast } from 'sonner'

export default function RegisterPage() {
  const [nome, setNome] = useState('')
  const [cognome, setCognome] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')

  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { isLoading } = useSelector((s: RootState) => s.auth)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!nome || !cognome || !email || !password) {
      toast.error('Compila tutti i campi!')
      return
    }

    const registerPromise = dispatch(
      registerUser({ nome, cognome, email, password }) as any
    )

    toast.promise(registerPromise, {
      loading: 'Registrazione in corso...',
      success: (result) => {
        if (registerUser.fulfilled.match(result)) {
          setNome('')
          setCognome('')
          setEmail('')
          setPassword('')
          setTimeout(() => navigate('/login'), 2000)
          return 'Registrazione completata! Ora puoi effettuare il login.'
        }
        throw new Error('Registrazione fallita')
      },
      error: (err) => err?.message || 'Errore durante la registrazione',
    })
  }

  return (
    <div className="page-content-custom-2">
      <div className="card mx-auto" style={{ maxWidth: 520 }}>
        <div className="card-body">
          <h5 className="card-title">Registrazione</h5>

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
