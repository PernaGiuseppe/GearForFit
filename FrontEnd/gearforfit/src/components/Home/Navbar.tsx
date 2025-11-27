import { Link, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { RootState } from '../../app/store'
import { logout } from '../../features/auth/authSlice'
import { Navbar, Nav, Container } from 'react-bootstrap'
import { useState } from 'react' // Aggiungi questo import

import '../../css/navbar.css'

export default function Navbar() {
  const user = useSelector((s: RootState) => s.auth.user)
  const dispatch = useDispatch()
  const navigate = useNavigate()

  const [expanded, setExpanded] = useState(false)

  const handleLogout = () => {
    dispatch(logout())
    navigate('/')
  }

  // Funzione per ottenere la classe CSS del badge in base al piano/ruolo
  const getBadgeClass = () => {
    if (user?.tipoUtente === 'ADMIN') return 'badge-admin'
    switch (user?.tipoPiano) {
      case 'FREE':
        return 'badge-free'
      case 'SILVER':
        return 'badge-silver'
      case 'GOLD':
        return 'badge-gold'
      case 'PREMIUM':
        return 'badge-premium'
      default:
        return ''
    }
  }

  // Funzione per ottenere il testo del badge
  const getBadgeText = () => {
    if (user?.tipoUtente === 'ADMIN') return 'ADMIN'
    return user?.tipoPiano || ''
  }

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
      <div className="container-fluid">
        {/* Brand con Badge */}
        <div className="brand-container">
          <Link className="navbar-brand ms-2" to="/">
            GearForFit
          </Link>
          {user && (
            <span className={`plan-badge ${getBadgeClass()}`}>
              {getBadgeText()}
            </span>
          )}
        </div>

        <button
          className="navbar-toggler"
          type="button"
          onClick={() => setExpanded(!expanded)}
          aria-controls="navbarNav"
          aria-expanded={expanded}
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div
          className={`collapse navbar-collapse ${expanded ? 'show' : ''}`}
          id="navbarNav"
        >
          <ul
            className={`navbar-nav me-auto mb-2 mb-lg-0 ${
              user ? 'nav-with-badge' : ''
            }`}
          >
            {/* Home Link */}
            <li className="nav-item ms-3 ms-lg-0 mt-2 mt-lg-0">
              <Link
                className="nav-link"
                to="/"
                onClick={() => setExpanded(false)}
              >
                Home
              </Link>
            </li>

            {user && (
              <>
                {/* Link Utenti - SOLO PER ADMIN */}
                {user.tipoUtente === 'ADMIN' && (
                  <li className="nav-item ms-3 ms-lg-0">
                    <Link
                      className="nav-link"
                      to="/utenti"
                      onClick={() => setExpanded(false)}
                    >
                      Utenti
                    </Link>
                  </li>
                )}

                {/* Link a Diete */}
                {(user.tipoUtente === 'ADMIN' ||
                  user.tipoPiano === 'PREMIUM' ||
                  user.tipoPiano === 'GOLD' ||
                  user.tipoPiano === 'SILVER' ||
                  user.tipoPiano === 'FREE') && (
                  <li className="nav-item ms-3 ms-lg-0">
                    <Link
                      className="nav-link"
                      to="/diete"
                      onClick={() => setExpanded(false)}
                    >
                      Diete
                    </Link>
                  </li>
                )}

                {/* Link a Schede */}
                {(user.tipoUtente === 'ADMIN' ||
                  user.tipoPiano === 'PREMIUM' ||
                  user.tipoPiano === 'GOLD' ||
                  user.tipoPiano === 'SILVER') && (
                  <li className="nav-item ms-3 ms-lg-0">
                    <Link
                      className="nav-link"
                      to="/schede"
                      onClick={() => setExpanded(false)}
                    >
                      Schede
                    </Link>
                  </li>
                )}
              </>
            )}
          </ul>

          <ul className="navbar-nav ms-auto me-1">
            {/* Link al Profilo - SOLO PER UTENTI NON ADMIN */}
            {user && user.tipoUtente !== 'ADMIN' && (
              <li className="nav-item me-2 ms-3 ms-lg-0">
                <Link
                  className="nav-link"
                  to="/profilo"
                  onClick={() => setExpanded(false)}
                >
                  Profilo
                </Link>
              </li>
            )}

            {/* Login/Logout Buttons */}
            {!user ? (
              <>
                <li className="nav-item ms-3 ms-lg-0 my-2 my-lg-0">
                  <Link
                    className="btn btn-primary me-2"
                    to="/login"
                    onClick={() => setExpanded(false)}
                  >
                    Accedi
                  </Link>
                </li>
                <li className="nav-item mx-2 ms-3 ms-lg-0 my-2 my-lg-0">
                  <Link
                    className="btn btn-outline-light"
                    to="/register"
                    onClick={() => setExpanded(false)}
                  >
                    Registrati
                  </Link>
                </li>
              </>
            ) : (
              <li className="nav-item me-2 ms-3 ms-lg-0 my-3 my-lg-0">
                <button
                  className="btn btn-outline-light"
                  onClick={() => {
                    handleLogout()
                    setExpanded(false)
                  }}
                >
                  Logout
                </button>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  )
}
