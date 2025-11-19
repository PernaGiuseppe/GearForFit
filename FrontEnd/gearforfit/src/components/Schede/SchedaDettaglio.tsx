import { useParams, Link } from 'react-router-dom'

const schedeMock = [
  {
    id: 1,
    title: 'Full Body Principianti',
    summary: 'Scheda base per iniziare in palestra.',
  },
  {
    id: 2,
    title: 'Forza 3 giorni',
    summary: 'Programma per sviluppare forza.',
  },
  {
    id: 3,
    title: 'Ipertrofia Split',
    summary: 'Split avanzato per ipertrofia.',
  },
]

export default function SchedaDettaglio() {
  const { id } = useParams()
  const scheda = schedeMock.find((s) => String(s.id) === String(id))

  if (!scheda) {
    return (
      <div>
        <h4>Scheda non trovata</h4>
        <Link to="/schede" className="btn btn-sm btn-secondary">
          Torna alle schede
        </Link>
      </div>
    )
  }

  return (
    <div className="card">
      <div className="card-body">
        <h3>{scheda.title}</h3>
        <p>{scheda.summary}</p>
        <hr />
        <p>
          Dettagli della scheda (esercizi, ripetizioni, note ...). Questa è una
          card statica di esempio.
        </p>
        <Link to="/schede" className="btn btn-sm btn-primary">
          Indietro alle schede
        </Link>
      </div>
    </div>
  )
}
