// src/components/ProtectedRoute.tsx
import { useSelector } from 'react-redux'
import { RootState } from '../app/store' // Assicurati che l'import sia corretto
import { Navigate } from 'react-router-dom'
import { UserData } from '../features/auth/authSlice' // Importa il tipo UserData aggiornato

type Piano = UserData['tipoPiano']

type Props = {
  children: JSX.Element
  requireAdmin?: boolean // Richiede esplicitamente l'essere ADMIN
  requirePiani?: Piano[] // Richiede uno specifico piano o l'admin per accedere
}

export default function ProtectedRoute({
  children,
  requireAdmin,
  requirePiani,
}: Props) {
  const user = useSelector((s: RootState) => s.auth.user)

  // 1. Se non è loggato, reindirizza al login
  if (!user) return <Navigate to="/login" replace />

  // Se l'utente è ADMIN, bypassa qualsiasi restrizione sui piani, a meno che non sia richiesto solo l'admin.
  const isUserAdmin = user.tipoUtente === 'ADMIN'

  // 2. Controllo specifico per ADMIN
  if (requireAdmin && !isUserAdmin) {
    return <Navigate to="/" replace />
  }

  // 3. Controllo per Piani a Pagamento
  if (requirePiani) {
    // Se l'utente è admin, passa il controllo anche se non è specificato nell'array (l'admin ha accesso a tutto)
    if (isUserAdmin) {
      return children
    }

    // Se l'utente non ha uno dei piani richiesti, nega l'accesso
    if (!requirePiani.includes(user.tipoPiano)) {
      return <Navigate to="/" replace />
    }
  }

  // 4. Se tutti i controlli passano (o se non ci sono requisiti speciali), permette l'accesso
  return children
}
