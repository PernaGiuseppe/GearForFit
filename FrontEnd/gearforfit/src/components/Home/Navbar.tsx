import { Link, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { RootState } from '../../app/store'
import { clearUser } from '../../features/auth/authSlice'

export default function Navbar() {
  const user = useSelector((s: RootState) => s.auth.user)
  const dispatch = useDispatch()
  const navigate = useNavigate()

  const handleLogout = () => {
    dispatch(clearUser())
    navigate('/')
  }

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light">
      <div className="container">
        <Link className="navbar-brand" to="/">
          <img
            src="/logo_rotondo.png"
            width={40}
            alt="GearForFit Logo"
            className="mx-2"
          />
          GearForFit
        </Link>
        <button
          className="navbar-toggler"
          data-bs-toggle="collapse"
          data-bs-target="#nav"
        >
          <span className="navbar-toggler-icon" />
        </button>
        <div className="collapse navbar-collapse" id="nav">
          <ul className="navbar-nav me-auto">
            <li className="nav-item">
              <Link className="nav-link" to="/">
                Home
              </Link>
            </li>

            {user && (
              <>
                {/* Diete: tutti gli utenti loggati hanno accesso alle diete (diversi livelli di contenuto gestiti nelle pagine) */}
                <li className="nav-item">
                  <Link className="nav-link" to="/diete">
                    Diete
                  </Link>
                </li>

                {/* Schede: visibilità dipende dal piano */}
                {user.tipoPiano === 'PREMIUM' ||
                user.tipoPiano === 'GOLD' ||
                user.tipoPiano === 'SILVER' ? (
                  <li className="nav-item">
                    <Link className="nav-link" to="/schede">
                      Schede
                    </Link>
                  </li>
                ) : null}
                {/* 
                Chat solo per PREMIUM
                {user.tipoPiano === 'PREMIUM' ? (
                  <li className="nav-item">
                    <Link className="nav-link" to="/chat">
                      Chat
                    </Link>
                  </li>
                ) : null} */}
              </>
            )}
          </ul>

          <ul className="navbar-nav ms-auto">
            {/* Admin dashboard link */}
            {user?.tipoUtente === 'ADMIN' && (
              <li className="nav-item">
                <Link className="nav-link" to="/admin">
                  Dashboard
                </Link>
              </li>
            )}
            {!user ? (
              <li className="nav-item">
                <Link className="nav-link" to="/login">
                  Login / Register
                </Link>
              </li>
            ) : (
              <>
                <li className="nav-item nav-link">
                  Ciao, {user.nome ?? user.email}
                </li>
                <li className="nav-item">
                  <button
                    className="btn btn-outline-secondary btn-sm"
                    onClick={handleLogout}
                  >
                    Logout
                  </button>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  )
}
