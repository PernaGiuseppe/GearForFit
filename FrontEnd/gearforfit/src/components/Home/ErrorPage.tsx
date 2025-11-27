// src/components/Home/ErrorPage.tsx

import { Link } from 'react-router-dom'
import '../../css/errorpage.css'

export default function ErrorPage() {
  return (
    <div className="error-page page-content-custom-2">
      <div className="error-content">
        <div className="error-numbers">
          <span className="number-4 blue">4</span>
          <div className="ghost-container">
            <div className="ghost">
              <div className="ghost-body"></div>
              <div className="ghost-eyes">
                <div className="eye"></div>
                <div className="eye"></div>
              </div>
              <div className="ghost-tail">
                <span></span>
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
          <span className="number-4 green">4</span>
        </div>

        <h1 className="error-title">Ooops, looks like a ghost!</h1>
        <Link to="/" className="btn btn-primary mt-2">
          Torna alla homepage
        </Link>
      </div>
    </div>
  )
}
