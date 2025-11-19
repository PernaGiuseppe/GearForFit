import { useNavigate } from 'react-router-dom'

export default function ErrorPage() {
  const navigate = useNavigate()

  return (
    <div
      className="d-flex align-items-center justify-content-center"
      style={{ height: '70vh', background: '#f8f9fa' }}
    >
      <div className="text-center">
        <h1 className="display-5">Ops — qualcosa è andato storto</h1>
        <p className="lead">
          La pagina che stai cercando non è disponibile in questo momento.
        </p>
        <div>
          <button className="btn btn-primary" onClick={() => navigate('/')}>
            Torna alla homepage
          </button>
        </div>
      </div>
    </div>
  )
}
