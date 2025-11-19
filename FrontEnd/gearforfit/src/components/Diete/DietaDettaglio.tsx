import { useParams, Link } from 'react-router-dom'

const dieteMock = [
  {
    id: 1,
    title: 'Dieta ipocalorica base',
    summary: 'Piano per perdita di peso.',
  },
  {
    id: 2,
    title: 'Dieta ipercalorica massa',
    summary: 'Piano per aumentare massa muscolare.',
  },
  {
    id: 3,
    title: 'Dieta bilanciata',
    summary: 'Piano equilibrato per mantenimento.',
  },
]

export default function DietaDettaglio() {
  const { id } = useParams()
  const dieta = dieteMock.find((d) => String(d.id) === String(id))

  if (!dieta) {
    return (
      <div>
        <h4>Dieta non trovata</h4>
        <Link to="/diete" className="btn btn-sm btn-secondary">
          Torna alle diete
        </Link>
      </div>
    )
  }

  return (
    <div className="card">
      <div className="card-body">
        <h3>{dieta.title}</h3>
        <p>{dieta.summary}</p>
        <hr />
        <p>
          Dettagli della dieta (giorni, pasti, macro). Contenuto dimostrativo
          statico.
        </p>
        <Link to="/diete" className="btn btn-sm btn-primary">
          Indietro alle diete
        </Link>
      </div>
    </div>
  )
}
