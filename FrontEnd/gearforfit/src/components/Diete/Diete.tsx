import { useSelector } from 'react-redux'
import { RootState } from '../app/store'

export default function Diete() {
  const user = useSelector((s: RootState) => s.auth.user)

  return (
    <div>
      <h3>Diete</h3>
      {!user && <p>Devi essere loggato per vedere le diete.</p>}
      {user && (
        <>
          <p>Tipo piano: {user.tipoPiano}</p>
          <ul>
            <li>Diete standard: disponibile per tutti gli utenti loggati</li>
            <li>
              Diete personalizzate: disponibile per PREMIUM / GOLD / SILVER
            </li>
          </ul>
        </>
      )}
    </div>
  )
}
