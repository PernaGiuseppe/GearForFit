import { useState, useEffect, useRef } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { AppDispatch, RootState } from '../../app/store'
import {
  fetchAllQeA,
  fetchRispostaById,
  clearRispostaCorrente,
} from '../../features/chat/qeaSlice'
import '../../css/chat.css'

type QeAResponseDTO = {
  id: number
  domanda: string
  risposta: string
}

type QeARispostaDTO = {
  id: number
  risposta: string
}

export default function Chat() {
  const [isMessagesOpen, setIsMessagesOpen] = useState(false)
  const [isClosing, setIsClosing] = useState(false)
  const [selectedQeAId, setSelectedQeAId] = useState<number | null>(null)
  const dropdownRef = useRef<HTMLDivElement>(null)
  const buttonRef = useRef<HTMLButtonElement>(null)

  const dispatch = useDispatch<AppDispatch>()
  const { listaQeA, rispostaCorrente, isLoading, error } = useSelector(
    (s: RootState) => s.qea
  )
  const user = useSelector((s: RootState) => s.auth.user)

  useEffect(() => {
    if (isMessagesOpen && listaQeA.length === 0 && !error) {
      dispatch(fetchAllQeA())
    }
  }, [isMessagesOpen, listaQeA.length, error, dispatch])

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        isMessagesOpen &&
        dropdownRef.current &&
        buttonRef.current &&
        !dropdownRef.current.contains(event.target as Node) &&
        !buttonRef.current.contains(event.target as Node)
      ) {
        toggleMessages()
      }
    }

    if (isMessagesOpen) {
      document.addEventListener('mousedown', handleClickOutside)
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside)
    }
  }, [isMessagesOpen])

  const toggleMessages = () => {
    if (isMessagesOpen) {
      setIsClosing(true)
      setTimeout(() => {
        setIsMessagesOpen(false)
        setIsClosing(false)
        setSelectedQeAId(null)
        dispatch(clearRispostaCorrente())
      }, 300)
    } else {
      setIsMessagesOpen(true)
    }
  }

  const handleQuestionClick = (qeaId: number) => {
    if (selectedQeAId === qeaId) {
      setSelectedQeAId(null)
      dispatch(clearRispostaCorrente())
    } else {
      setSelectedQeAId(qeaId)
      dispatch(fetchRispostaById(qeaId))
    }
  }

  return (
    <>
      {!isMessagesOpen && (
        <button
          ref={buttonRef}
          onClick={toggleMessages}
          className="messaggistica-button"
        >
          Chat Q&A
        </button>
      )}

      {isMessagesOpen && (
        <div
          ref={dropdownRef}
          className={`messages-dropdown ${isClosing ? 'closing' : ''}`}
        >
          <div className="messages-header">
            <span>Domande Frequenti (Q&A)</span>
            <button onClick={toggleMessages} className="close-button">
              ×
            </button>
          </div>

          <div className="messages-content">
            {isLoading && !rispostaCorrente && <p>Caricamento domande...</p>}
            {error && <p>Errore: {error}</p>}
            {!isLoading && listaQeA.length === 0 && !error && (
              <p>Nessuna Q&A disponibile in questo momento.</p>
            )}

            {listaQeA.map((qea: QeAResponseDTO) => (
              <div
                key={qea.id}
                onClick={() => handleQuestionClick(qea.id)}
                className="question-item"
              >
                <div className="question-text">
                  <strong>D:</strong> {qea.domanda}
                </div>
                {selectedQeAId === qea.id && (
                  <div className="answer-container">
                    {isLoading ? (
                      <p>Caricamento risposta...</p>
                    ) : (
                      <>
                        {rispostaCorrente ? (
                          <div className="answer-text">
                            <strong>R:</strong>{' '}
                            {(rispostaCorrente as QeARispostaDTO).risposta}
                          </div>
                        ) : (
                          <p>Risposta non disponibile.</p>
                        )}
                      </>
                    )}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </>
  )
}
