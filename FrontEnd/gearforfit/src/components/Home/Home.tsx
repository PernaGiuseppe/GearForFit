import { articles } from '../../data/articles'
import { carouselItems } from '../../data/carousel'
import { Link } from 'react-router-dom'

export default function Home() {
  return (
    <div>
      <div
        id="heroCarousel"
        className="carousel slide mb-4"
        data-bs-ride="carousel"
      >
        <div className="carousel-inner">
          {carouselItems.map((it, idx) => (
            <div
              className={`carousel-item ${idx === 0 ? 'active' : ''}`}
              key={it.id}
            >
              <div
                className="d-flex align-items-center justify-content-center"
                style={{ height: 240, background: '#e9ecef' }}
              >
                <div className="text-center">
                  <h3>{it.title}</h3>
                  <p>{it.caption}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
        <button
          className="carousel-control-prev"
          type="button"
          data-bs-target="#heroCarousel"
          data-bs-slide="prev"
        >
          <span className="carousel-control-prev-icon" />
        </button>
        <button
          className="carousel-control-next"
          type="button"
          data-bs-target="#heroCarousel"
          data-bs-slide="next"
        >
          <span className="carousel-control-next-icon" />
        </button>
      </div>

      <h4>Articoli Salute & Sport</h4>
      <div className="row">
        {articles.map((a) => (
          <div className="col-md-4 mb-3" key={a.id}>
            <div className="card h-100">
              <div className="card-body">
                <h5 className="card-title">{a.title}</h5>
                <p className="card-text">{a.excerpt}</p>
                <Link
                  to={`/articoli/${a.id}`}
                  className="btn btn-sm btn-primary"
                >
                  Leggi
                </Link>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
