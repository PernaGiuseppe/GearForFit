import React, { useState } from 'react'
import { useDispatch } from 'react-redux'
import { setUser } from '../../features/auth/authSlice'
import { useNavigate } from 'react-router-dom'

export default function LoginPage() {
  const [email, setEmail] = useState('user@example.com')
  const [tipoUtente, setTipoUtente] = useState<'USER' | 'ADMIN'>('USER')
  const [tipoPiano, setTipoPiano] = useState<
    'PREMIUM' | 'GOLD' | 'SILVER' | 'FREE'
  >('FREE')
  const dispatch = useDispatch()
  const navigate = useNavigate()

  const submit = (e: React.FormEvent) => {
    e.preventDefault()
    dispatch(
      setUser({
        id: Date.now(),
        email,
        nome: 'Test',
        cognome: 'User',
        tipoUtente,
        tipoPiano,
        token: 'fake-token',
      })
    )
    navigate('/')
  }

  return (
    <div className="card mx-auto" style={{ maxWidth: 520 }}>
      <div className="card-body">
        <h5 className="card-title">Login Mock</h5>
        <form onSubmit={submit}>
          <div className="mb-2">
            <label className="form-label">Email</label>
            <input
              className="form-control"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className="mb-2">
            <label className="form-label">Tipo Utente</label>
            <select
              className="form-select"
              value={tipoUtente}
              onChange={(e) => setTipoUtente(e.target.value as any)}
            >
              <option value="USER">USER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>

          <div className="mb-3">
            <label className="form-label">Piano</label>
            <select
              className="form-select"
              value={tipoPiano}
              onChange={(e) => setTipoPiano(e.target.value as any)}
            >
              <option value="PREMIUM">PREMIUM</option>
              <option value="GOLD">GOLD</option>
              <option value="SILVER">SILVER</option>
              <option value="FREE">FREE</option>
            </select>
          </div>

          <button className="btn btn-primary">Login (mock)</button>
        </form>
      </div>
    </div>
  )
}
