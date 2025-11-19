import { useParams, Link } from 'react-router-dom'
import { articles } from '../../data/articles'
export default function ArticoloDettaglio() {
  const { id } = useParams()
  const articolo = articles.find((a) => String(a.id) === String(id))

  if (!articolo) {
    return (
      <div>
        <h4>Articolo non trovato</h4>
        <Link to="/" className="btn btn-sm btn-secondary">
          Torna alla home
        </Link>
      </div>
    )
  }

  return (
    <div className="card">
      <div className="card-body">
        <h3 className="card-title">{articolo.title}</h3>
        <p className="card-text">{articolo.excerpt}</p>
        <hr />
        <p>
          Contenuto dimostrativo dell'articolo. Qui puoi inserire il testo
          completo tratto dal backend oppure HTML ricco. Questo è un esempio
          statico per mostrare la struttura della pagina di dettaglio.
        </p>
        <Link to="/" className="btn btn-sm btn-primary">
          Torna alla home
        </Link>
      </div>
    </div>
  )
}
