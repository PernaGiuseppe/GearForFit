import { useState, useEffect } from 'react'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

type UtenteDTO = {
  id: number
  email: string
  nome: string
  cognome: string
  tipoUtente: string
  tipoPiano: string
  attivo: boolean
}

const PIANI_DISPONIBILI = ['FREE', 'SILVER', 'GOLD', 'PREMIUM']

export default function GestioneUtenti() {
  const [utenti, setUtenti] = useState<UtenteDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchUtenti()
  }, [])

  const fetchUtenti = async () => {
    setLoading(true)
    setError(null)
    try {
      const res = await fetch(`${API_BASE_URL}/admin/utenti`, {
        headers: getAuthHeader(),
      })
      if (!res.ok) throw new Error('Errore nel caricamento degli utenti')
      const data = await res.json()
      setUtenti(data)
    } catch (err: any) {
      console.error(err)
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  // --- LOGICA CAMBIO PIANO ---
  const handleCambiaPiano = async (id: number, nuovoPiano: string) => {
    if (
      !window.confirm(
        `Vuoi cambiare il piano dell'utente ${id} in ${nuovoPiano}?`
      )
    )
      return

    try {
      const res = await fetch(
        `${API_BASE_URL}/admin/utenti/${id}/piano?nuovoPiano=${nuovoPiano}`,
        {
          method: 'PUT',
          headers: getAuthHeader(),
        }
      )
      if (!res.ok) throw new Error('Errore modifica piano')

      alert('Piano modificato con successo')
      fetchUtenti()
    } catch (err: any) {
      alert(err.message)
    }
  }

  // --- LOGICA RESET PASSWORD ---
  const handleResetPassword = async (id: number) => {
    const nuovaPassword = prompt("Inserisci la nuova password per l'utente:")

    if (!nuovaPassword) return
    if (nuovaPassword.length < 6) {
      alert('La password deve essere di almeno 6 caratteri')
      return
    }

    try {
      const res = await fetch(`${API_BASE_URL}/admin/utenti/reset-password`, {
        method: 'PUT',
        headers: {
          ...getAuthHeader(),
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          utenteId: id,
          nuovaPassword: nuovaPassword,
        }),
      })

      if (!res.ok) throw new Error('Errore reset password')

      alert('Password resettata con successo!')
    } catch (err: any) {
      alert(err.message)
    }
  }

  const handleDeleteUser = async (id: number) => {}
  const toggleUserStatus = async (id: number, currentStatus: boolean) => {
    try {
      const res = await fetch(
        `${API_BASE_URL}/admin/utenti/${id}/${
          currentStatus ? 'disattiva' : 'attiva'
        }`,
        { method: 'PUT', headers: getAuthHeader() }
      )
      if (!res.ok) throw new Error('Errore cambio stato utente')
      fetchUtenti()
    } catch (err: any) {
      alert(err.message)
    }
  }

  if (loading)
    return <div className="container mt-4">Caricamento utenti...</div>
  if (error)
    return <div className="container mt-4 alert alert-danger">{error}</div>

  return (
    <div className="container mt-4 page-content-general ">
      <h1>Gestione Utenti</h1>
      <p className="text-muted">Totale utenti: {utenti.length}</p>

      <div className="table-responsive">
        <table className="table table-striped table-hover align-middle">
          <thead>
            <tr>
              <th>ID</th>
              <th>Info</th>
              <th>Ruolo</th>
              <th>Piano</th>
              <th>Stato</th>
              <th>Azioni</th>
            </tr>
          </thead>
          <tbody>
            {utenti.map((utente) => (
              <tr key={utente.id}>
                <td>{utente.id}</td>
                <td>
                  <div>
                    <strong>
                      {utente.nome} {utente.cognome}
                    </strong>
                  </div>
                  <small className="text-muted">{utente.email}</small>
                </td>
                <td>
                  <span
                    className={`badge ${
                      utente.tipoUtente === 'ADMIN'
                        ? 'bg-danger'
                        : 'bg-secondary'
                    }`}
                  >
                    {utente.tipoUtente}
                  </span>
                </td>

                {/* COLONNA PIANO CON DROPDOWN */}
                <td>
                  {utente.tipoUtente === 'ADMIN' ? (
                    <span className="badge bg-dark">ADMIN</span>
                  ) : (
                    <select
                      className="form-select form-select-sm"
                      value={utente.tipoPiano}
                      onChange={(e) =>
                        handleCambiaPiano(utente.id, e.target.value)
                      }
                      style={{ width: '110px' }}
                    >
                      {PIANI_DISPONIBILI.map((piano) => (
                        <option key={piano} value={piano}>
                          {piano}
                        </option>
                      ))}
                    </select>
                  )}
                </td>

                <td>
                  <span
                    className={`badge ${
                      utente.attivo ? 'bg-success' : 'bg-warning'
                    }`}
                  >
                    {utente.attivo ? 'Attivo' : 'Disattivato'}
                  </span>
                </td>

                <td>
                  {/* PULSANTE RESET PASSWORD */}
                  <button
                    className="btn btn-sm btn-info text-white me-2"
                    onClick={() => handleResetPassword(utente.id)}
                    title="Resetta Password"
                  >
                    Reset pass
                  </button>

                  <button
                    className={`btn btn-sm ${
                      utente.attivo ? 'btn-warning' : 'btn-success ms-2 me-3'
                    } me-2`}
                    onClick={() => toggleUserStatus(utente.id, utente.attivo)}
                    disabled={utente.tipoUtente === 'ADMIN'}
                  >
                    {utente.attivo ? 'Disattiva' : 'Attiva'}
                  </button>

                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDeleteUser(utente.id)}
                    disabled={utente.tipoUtente === 'ADMIN'}
                  >
                    Elimina
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
