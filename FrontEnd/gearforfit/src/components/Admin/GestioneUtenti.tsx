import { useState, useEffect } from 'react'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'
import { toast } from 'sonner'

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
      toast.success('Utenti caricati con successo')
    } catch (err: any) {
      console.error(err)
      setError(err.message)
      toast.error(err.message || 'Errore nel caricamento degli utenti')
    } finally {
      setLoading(false)
    }
  }

  // --- CAMBIO PIANO ---
  const handleCambiaPiano = async (id: number, nuovoPiano: string) => {
    toast.promise(
      fetch(
        `${API_BASE_URL}/admin/utenti/${id}/piano?nuovoPiano=${nuovoPiano}`,
        {
          method: 'PUT',
          headers: getAuthHeader(),
        }
      ).then(async (res) => {
        if (!res.ok) throw new Error('Errore modifica piano')
        await fetchUtenti()
        return nuovoPiano
      }),
      {
        loading: 'Modifica del piano in corso...',
        success: (piano) => `Piano modificato in ${piano} con successo!`,
        error: 'Errore durante la modifica del piano',
      }
    )
  }

  // --- RESET PASSWORD ---
  const handleResetPassword = async (id: number) => {
    const nuovaPassword = prompt("Inserisci la nuova password per l'utente:")

    if (!nuovaPassword) return
    if (nuovaPassword.length < 6) {
      toast.error('La password deve essere di almeno 6 caratteri')
      return
    }

    toast.promise(
      fetch(`${API_BASE_URL}/admin/utenti/reset-password`, {
        method: 'PUT',
        headers: {
          ...getAuthHeader(),
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          utenteId: id,
          nuovaPassword: nuovaPassword,
        }),
      }).then(async (res) => {
        if (!res.ok) throw new Error('Errore reset password')
      }),
      {
        loading: 'Reset password in corso...',
        success: 'Password resettata con successo!',
        error: 'Errore durante il reset della password',
      }
    )
  }

  const handleDeleteUser = async (id: number) => {
    toast('Sei sicuro di voler eliminare questo utente?', {
      action: {
        label: 'Conferma',
        onClick: async () => {
          toast.promise(
            fetch(`${API_BASE_URL}/admin/utenti/${id}`, {
              method: 'DELETE',
              headers: getAuthHeader(),
            }).then(async (res) => {
              if (!res.ok) throw new Error('Errore eliminazione utente')
              await fetchUtenti()
            }),
            {
              loading: 'Eliminazione in corso...',
              success: 'Utente eliminato con successo',
              error: "Impossibile eliminare l'utente",
            }
          )
        },
      },
      cancel: {
        label: 'Annulla',
      },
      duration: 5000,
    })
  }

  const toggleUserStatus = async (id: number, currentStatus: boolean) => {
    toast.promise(
      fetch(
        `${API_BASE_URL}/admin/utenti/${id}/${
          currentStatus ? 'disattiva' : 'attiva'
        }`,
        { method: 'PUT', headers: getAuthHeader() }
      ).then(async (res) => {
        if (!res.ok) throw new Error('Errore cambio stato utente')
        await fetchUtenti()
      }),
      {
        loading: 'Modifica stato in corso...',
        success: `Utente ${
          currentStatus ? 'disattivato' : 'attivato'
        } con successo!`,
        error: 'Errore durante il cambio di stato',
      }
    )
  }

  if (loading)
    return (
      <div className="container mt-4 page-content-general ">
        Caricamento utenti...
      </div>
    )
  if (error)
    return (
      <div className="container mt-4 page-content-general ">
        <div className="alert alert-danger">{error}</div>
      </div>
    )

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
                  <div className="my-1">
                    <strong>
                      {utente.nome} {utente.cognome}
                    </strong>
                  </div>
                  {/* small da eliminare prima del demoday*/}
                  {/* <small className="text-muted">{utente.email}</small> */}
                </td>
                <td>
                  <span
                    className={`badge ${
                      utente.tipoUtente === 'ADMIN'
                        ? 'badge--violet'
                        : 'badge--secondary'
                    }`}
                  >
                    {utente.tipoUtente}
                  </span>
                </td>

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
                      utente.attivo ? 'badge--status' : 'badge--warning'
                    }`}
                  >
                    {utente.attivo ? 'Attivo' : 'Inattivo'}
                  </span>
                </td>

                <td>
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
                    } me-2 ${utente.tipoUtente === 'ADMIN' ? 'd-none' : ''}`}
                    onClick={() => toggleUserStatus(utente.id, utente.attivo)}
                    disabled={utente.tipoUtente === 'ADMIN'}
                  >
                    {utente.attivo ? 'Disattiva' : 'Attiva'}
                  </button>

                  <button
                    className={`btn btn-sm btn-danger ${
                      utente.tipoUtente === 'ADMIN' ? 'd-none' : ''
                    }`}
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
