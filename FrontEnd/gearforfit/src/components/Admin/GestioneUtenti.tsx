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

      if (!res.ok) {
        throw new Error('Errore nel caricamento degli utenti')
      }

      const data = await res.json()
      setUtenti(data)
    } catch (err: any) {
      console.error(err)
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleDeleteUser = async (id: number) => {
    if (!window.confirm('Sei sicuro di voler eliminare questo utente?')) {
      return
    }

    try {
      const res = await fetch(`${API_BASE_URL}/admin/utenti/${id}`, {
        method: 'DELETE',
        headers: getAuthHeader(),
      })

      if (!res.ok) {
        throw new Error('Errore eliminazione utente')
      }

      // Ricarica la lista
      fetchUtenti()
    } catch (err: any) {
      alert(err.message)
    }
  }

  const toggleUserStatus = async (id: number, currentStatus: boolean) => {
    try {
      const res = await fetch(
        `${API_BASE_URL}/admin/utenti/${id}/${
          currentStatus ? 'disattiva' : 'attiva'
        }`,
        {
          method: 'PUT',
          headers: getAuthHeader(),
        }
      )

      if (!res.ok) {
        throw new Error('Errore cambio stato utente')
      }

      // Ricarica la lista
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
    <div className="container mt-4">
      <h1>Gestione Utenti</h1>
      <p className="text-muted">Totale utenti: {utenti.length}</p>

      <div className="table-responsive">
        <table className="table table-striped table-hover">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nome</th>
              <th>Cognome</th>
              <th>Email</th>
              <th>Tipo Utente</th>
              <th>Piano</th>
              <th>Stato</th>
              <th>Azioni</th>
            </tr>
          </thead>
          <tbody>
            {utenti.map((utente) => (
              <tr key={utente.id}>
                <td>{utente.id}</td>
                <td>{utente.nome}</td>
                <td>{utente.cognome}</td>
                <td>{utente.email}</td>
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
                <td>
                  <span className="badge bg-info">{utente.tipoPiano}</span>
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
                  <button
                    className={`btn btn-sm ${
                      utente.attivo ? 'btn-warning' : 'btn-success'
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
