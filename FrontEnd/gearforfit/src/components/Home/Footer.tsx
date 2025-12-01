import { Container, Row, Col } from 'react-bootstrap'
import { Link } from 'react-router-dom'

export default function Footer() {
  return (
    <Container
      fluid
      className="colore-navfoot text-light mt-2 border-top border-secondary"
    >
      <Row className="px-md-3 px-3 pt-4">
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
