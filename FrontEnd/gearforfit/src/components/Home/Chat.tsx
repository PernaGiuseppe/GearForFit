import { useState } from 'react'
import '../../css/chat.css'

export default function Chat() {
  const [isMessagesOpen, setIsMessagesOpen] = useState(false)
  const [isClosing, setIsClosing] = useState(false)

  const toggleMessages = () => {
    if (isMessagesOpen) {
      setIsClosing(true)
      setTimeout(() => {
        setIsMessagesOpen(false)
        setIsClosing(false)
      }, 300)
    } else {
      setIsMessagesOpen(true)
    }
  }

  return (
    <>
      {!isMessagesOpen && (
        <button
          className="messaggistica-button order-5 d-none d-xxl-block"
          onClick={toggleMessages}
        >
          Messaggistica
        </button>
      )}
      {isMessagesOpen && (
        <div
          className={`messages-dropdown d-none d-xxl-block ${
            isClosing ? 'closing' : ''
          }`}
        >
          <div className="messages-header">
            <div className="messages-title">
              <span>Messaggistica</span>
            </div>
            <div className="messages-header-actions">
              <button className="icon-button" title="Impostazioni">
                &#9881;
              </button>
              <button
                onClick={toggleMessages}
                className="icon-button"
                title="Abbassa"
              >
                &#x25BC;
              </button>
              <button
                onClick={toggleMessages}
                className="close-button"
                title="Chiudi"
              >
                &times;
              </button>
            </div>
          </div>
          <div className="messages-search mb-4">
            <input type="text" placeholder="Cerca messaggi" />
            <button className="icon-button" title="Filtra">
              &#x1F50D;
            </button>
          </div>
          <div className="messages-content mt-5">
            <img
              src="https://static.licdn.com/aero-v1/sc/h/eeol4w9o9de2j4gq699mzx79d"
              alt="No messages illustration"
              className="messages-illustration"
            />
            <h4>Ancora nessun messaggio</h4>
            <p>
              Entra in contatto e dai il via a una conversazione per far
              decollare la tua carriera
            </p>
            <button className="btn-primary">Invia un messaggio</button>
          </div>
        </div>
      )}
    </>
  )
}
