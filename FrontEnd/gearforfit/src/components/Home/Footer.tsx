import { Container, Row, Col } from 'react-bootstrap'
import { Link } from 'react-router-dom'

export default function Footer() {
  const linkSections = [
    [
      { text: 'Informazioni', href: '/about' },
      { text: 'Informativa sulla community', href: '/community-guidelines' },
      { text: 'Privacy e condizioni', href: '/privacy' },
      { text: 'Sales e solutions', href: '/sales' },
      { text: 'Centro sicurezza', href: '/security' },
    ],
    [
      { text: 'Accessibilità', href: '/accessibility' },
      { text: 'Carriera', href: '/careers' },
      { text: 'Opzioni per gli annunci', href: '/ads' },
      { text: 'Mobile', href: '/mobile' },
    ],
    [
      { text: 'Talent Solutions', href: '/talent' },
      { text: 'Soluzioni di marketing', href: '/marketing' },
      { text: 'Pubblicità', href: '/advertising' },
      { text: 'Piccole Imprese', href: '/small-business' },
    ],
  ]

  return (
    <Container
      fluid
      className="colore-navfoot text-light mt-2 border-top border-secondary"
    >
      <Row className="px-md-3Z px-3 pt-5">
        {/* Colonna Logo */}
        <Col xs={12} md={3} className="mb-4 mb-md-0">
          <Link
            to="/"
            className="navbar-brand fs-3 text-light d-flex align-items-center"
          >
            <img
              src="/logo_rotondo.png"
              width={40}
              alt="GearForFit Logo"
              className="mx-2"
            />
            <span>GearForFit</span>
          </Link>
        </Col>

        {/* Colonne Link Dinamici */}
        {linkSections.map((section, sectionIndex) => (
          <Col xs={6} md={3} lg={2} key={sectionIndex}>
            <ul className="list-unstyled ms-3 ms-md-4 ms-lg-0">
              {section.map((link, linkIndex) => (
                <li key={linkIndex} className="mb-2">
                  <Link
                    to={link.href}
                    className="text-decoration-none text-light opacity-75"
                  >
                    {link.text}
                  </Link>
                </li>
              ))}
            </ul>
          </Col>
        ))}
      </Row>
      <Row className="border-top border-secondary mt-3">
        <Col className="text-center py-3">
          <p className="mb-0 small text-light opacity-75">
            GearForFit Corporation © {new Date().getFullYear()}
          </p>
        </Col>
      </Row>
    </Container>
  )
}
