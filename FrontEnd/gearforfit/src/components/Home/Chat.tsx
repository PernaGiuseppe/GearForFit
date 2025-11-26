// src/components/Home/Chat.tsx
import { useState, useEffect } from 'react'
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
  // ID della domanda attualmente selezionata per mostrare la risposta
  const [selectedQeAId, setSelectedQeAId] = useState<number | null>(null)

  const dispatch = useDispatch<AppDispatch>()
  const { listaQeA, rispostaCorrente, isLoading, error } = useSelector(
    (s: RootState) => s.qea
  )
  const user = useSelector((s: RootState) => s.auth.user)

  // Carica la lista delle Q&A all'apertura del pannello se non è già stata caricata
  useEffect(() => {
    if (isMessagesOpen && listaQeA.length === 0 && !error) {
      dispatch(fetchAllQeA())
    }
  }, [isMessagesOpen, listaQeA.length, error, dispatch])

  const toggleMessages = () => {
    if (isMessagesOpen) {
      setIsClosing(true)
      setTimeout(() => {
        setIsMessagesOpen(false)
        setIsClosing(false)
        setSelectedQeAId(null)
        dispatch(clearRispostaCorrente()) // Rimuovi la risposta dallo store alla chiusura
      }, 300)
    } else {
      setIsMessagesOpen(true)
    }
  }

  const handleQuestionClick = (qeaId: number) => {
    // Se la domanda è già selezionata, deselezionala e resetta la risposta
    if (selectedQeAId === qeaId) {
      setSelectedQeAId(null)
      dispatch(clearRispostaCorrente())
    } else {
      // Altrimenti, seleziona la nuova domanda e fai la fetch della risposta
      setSelectedQeAId(qeaId)
      dispatch(fetchRispostaById(qeaId))

      // NOTA: Se volessi ricaricare TUTTE le domande come suggerito,
      // dovresti chiamare dispatch(fetchAllQeA()) qui. Ma è inefficiente
      // per una chat statica di Q&A e l'endpoint /risposta funziona in isolamento.
    }
  }

  return (
    <>
      {!isMessagesOpen && (
        <button
          className="messaggistica-button order-5 d-none d-xxl-block"
          onClick={toggleMessages}
        >
          Chat Q&A
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
              <span>Chat Q&A (Piano {user?.tipoPiano})</span>
            </div>
            <div className="messages-header-actions">
              <button
                onClick={toggleMessages}
                className="close-button"
                title="Chiudi"
              >
                &times;
              </button>
            </div>
          </div>
          <div className="messages-content p-3">
            <h5 className="mb-3">Domande Frequenti (Q&A)</h5>

            {isLoading && !rispostaCorrente && <p>Caricamento domande...</p>}
            {error && <div className="alert alert-danger">Errore: {error}</div>}

            {!isLoading && listaQeA.length === 0 && !error && (
              <p>Nessuna Q&A disponibile in questo momento.</p>
            )}

            <div className="list-group">
              {listaQeA.map((qea: QeAResponseDTO) => (
                <div
                  key={qea.id}
                  className={`list-group-item list-group-item-action ${
                    selectedQeAId === qea.id ? 'active' : ''
                  }`}
                  onClick={() => handleQuestionClick(qea.id)}
                  style={{ cursor: 'pointer' }}
                >
                  <strong>D:</strong> {qea.domanda}
                  {/* Area per mostrare la risposta */}
                  {selectedQeAId === qea.id && (
                    <div
                      className={`mt-2 p-2 border-top ${
                        selectedQeAId === qea.id ? 'active' : 'text-white'
                      }`}
                    >
                      {isLoading ? (
                        <small className="text-white">
                          Caricamento risposta...
                        </small>
                      ) : (
                        <>
                          {rispostaCorrente ? (
                            <>
                              <strong>R:</strong>{' '}
                              {(rispostaCorrente as QeARispostaDTO).risposta}
                            </>
                          ) : (
                            <small className="text-white">
                              Risposta non disponibile.
                            </small>
                          )}
                        </>
                      )}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </>
  )
}
