import { useSelector } from 'react-redux'
import { RootState } from '../app/store'
import { Navigate } from 'react-router-dom'

type Props = {
  children: JSX.Element
  requireAdmin?: boolean
  requirePiani?: Array<'PREMIUM' | 'GOLD' | 'SILVER' | 'FREE'>
}

export default function ProtectedRoute({
  children,
  requireAdmin,
  requirePiani,
}: Props) {
  const user = useSelector((s: RootState) => s.auth.user)

  if (!user) return <Navigate to="/login" replace />

  if (requireAdmin && user.tipoUtente !== 'ADMIN')
    return <Navigate to="/" replace />

  if (requirePiani && !requirePiani.includes(user.tipoPiano))
    return <Navigate to="/" replace />

  return children
}
