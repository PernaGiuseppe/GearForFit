import { useParams, Link, useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from '../../app/store'
import { articles } from '../../data/articles'

export default function ArticoliDettaglio() {
  const { id } = useParams()
  const navigate = useNavigate()
  const user = useSelector((state: RootState) => state.auth.user)

  const article = articles.find((a) => a.id === Number(id))
  const handleScrollToTop = () => {
    window.scrollTo(0, 0)
  }
  if (!article) {
    return (
      <div className="container mt-5 text-center page-content-custom">
        <h2>Articolo non trovato</h2>
        <button className="btn btn-primary my-3" onClick={() => navigate('/')}>
          Torna alla Home
        </button>
      </div>
    )
  }

  return (
    <div className="container my-2" style={{ maxWidth: '800px' }}>
      {/* Intestazione Articolo */}
      <div className="mb-4">
        <button className="btn btn-primary my-3" onClick={() => navigate('/')}>
          Torna alla Home
        </button>
        <h1 className="fw-bold display-5 mt-1 mb-3">{article.title}</h1>
        <div className="d-flex align-items-center text-muted">
          <i className="bi bi-calendar3 me-2"></i>
          <span className="me-4">{article.date}</span>
          <i className="bi bi-person-circle me-2"></i>
          <span>{article.author}</span>
        </div>
      </div>

      {/* Immagine Copertina (Placeholder) */}
      <div
        className="bg-light rounded-4 mb-5 d-flex align-items-center justify-content-center text-muted"
        style={{ height: '300px', width: '100%', overflow: 'hidden' }}
      >
        <img
          src={`/${article.image}.jpg`}
          alt={article.title}
          style={{
            width: '100%',
            height: '100%',
            objectFit: 'cover',
          }}
        />
      </div>

      <div className="article-content fs-5 lh-lg mb-5">
        {article.content.split('\n\n').map((paragraph, index) => (
          <p key={index} className="mb-2 fs-6">
            {paragraph}
          </p>
        ))}
      </div>

      <hr className="my-5" />

      {/*  BOX PROMOZIONALE  */}
      {!user ? (
        // VISIBILE SOLO AI NON LOGGATI
        <div className="card border-0 shadow-lg rounded-4 overflow-hidden bg-primary text-white mb-4">
          <div className="card-body p-5 text-center">
            <h3 className="fw-bold mb-3">Ti è piaciuto questo articolo?</h3>
            <p className="lead mb-4">
              Questa è solo la punta dell'iceberg. <br />
              Accedi a <strong>GearForFit</strong> per ottenere piani di
              allenamento personalizzati, diete su misura e molto altro.
            </p>
            <div className="d-flex flex-column flex-sm-row justify-content-center gap-2 gap-md-3">
              <Link
                to="/register"
                className="btn btn-light btn-lg"
                onClick={handleScrollToTop}
              >
                Registrati
              </Link>
              <Link
                to="/login"
                className="btn btn-outline-light btn-lg"
                onClick={handleScrollToTop}
              >
                Accedi
              </Link>
            </div>
          </div>
        </div>
      ) : (
        // VISIBILE SOLO AI LOGGATI
        <div className="card border-0 shadow-sm rounded-4 bg-light mb-4">
          <div className="card-body p-4 text-center">
            <h4 className="fw-bold">Pronto per allenarti?</h4>
            <p className="text-muted mb-3">
              Metti in pratica i consigli. Vai alla sezione schede o controlla
              la tua dieta.
            </p>
            <div className="d-flex justify-content-center gap-3">
              <Link
                to="/schede"
                className="btn btn-primary"
                onClick={handleScrollToTop}
              >
                Vai alle Schede
              </Link>
              <Link
                to="/diete"
                className="btn btn-outline-primary"
                onClick={handleScrollToTop}
              >
                Vai alle Diete
              </Link>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
