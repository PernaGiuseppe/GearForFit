import { articles } from '../../data/articles'
import { Link } from 'react-router-dom'

export default function Home() {
  const handleScrollToTop = () => {
    window.scrollTo(0, 0)
  }
  return (
    <div className="mb-5 page-content">
      {/* ... TITOLO SEZIONE ... */}
      <div className="row align-items-center mb-4">
        <div className="col">
          <h3 className="fw-bold border-bottom pb-2">Articoli dal Blog</h3>
        </div>
      </div>

      {/* GRIGLIA CARDS */}
      <div className="row g-4">
        {articles.map((a) => (
          <div className="col-12 col-sm-6 col-md-4 col-lg-3" key={a.id}>
            <div className="card h-100 shadow-sm hover-card rounded-3">
              <div
                style={{
                  height: '120px',
                  backgroundColor: '#e9ecef',
                  width: '100%',
                  borderTopLeftRadius: 'var(--bs-border-radius-lg)',
                  borderTopRightRadius: 'var(--bs-border-radius-lg)',
                }}
                className="d-flex align-items-center justify-content-center text-muted"
              >
                <img
                  src={`/${a.image}.jpg`}
                  alt={a.title}
                  style={{
                    width: '100%',
                    height: '100%',
                    objectFit: 'cover',
                    borderTopLeftRadius: 'var(--bs-border-radius-lg)',
                    borderTopRightRadius: 'var(--bs-border-radius-lg)',
                  }}
                />
              </div>

              <div className="card-body d-flex flex-column">
                <h5 className="card-title fw-bold">{a.title}</h5>
                <p className="card-text text-muted small flex-grow-1">
                  {a.excerpt}
                </p>
                <Link
                  to={`/articoli/${a.id}`}
                  className="btn btn-outline-primary w-100 mt-3 fw-semibold"
                  onClick={handleScrollToTop}
                >
                  Leggi articolo <i className="bi bi-arrow-right-short"></i>
                </Link>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
