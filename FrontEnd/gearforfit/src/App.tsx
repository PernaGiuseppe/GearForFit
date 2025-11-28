import { Routes, Route } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from './app/store'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './components/Login/LoginPage'
import Diete from './components/Diete/Diete'
import Schede from './components/Schede/Schede'
import Chat from './components/Home/Chat'
import Home from './components/Home/Home'
import RegisterPage from './components/Login/RegisterPage'
import SchedaDettaglio from './components/Schede/SchedaDettaglio'
import DietaDettaglio from './components/Diete/DietaDettaglio'
import ErrorPage from './components/Home/ErrorPage'
import Navbar from './components/Home/Navbar'
import Footer from './components/Home/Footer'
import ProfiloUtente from './components/Login/ProfiloUtente'
import SchedaCustom from './components/Schede/SchedaCustom'
import DietaCustom from './components/Diete/DietaCustom'
import GestioneUtenti from './components/Admin/GestioneUtenti'
import { canUserAccessChat } from './features/auth/authSlice'
import SchedaCustomAdmin from './components/Admin/SchedaCustomAdmin'
import SchedaStandardAdmin from './components/Admin/SchedaStandardAdmin'
import ArticoliDettaglio from './components/Home/ArticoliDettaglio'
import './App.css'

export default function App() {
  const user = useSelector((s: RootState) => s.auth.user)
  const canAccessChat = canUserAccessChat(user)

  return (
    <>
      <Navbar />
      <div className="container mt-4">
        {canAccessChat && (
          <div>
            <Chat />
          </div>
        )}
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/articoli/:id" element={<ArticoliDettaglio />} />

          {/* Route protette per tutti gli utenti loggati */}
          <Route
            path="/diete"
            element={
              <ProtectedRoute>
                <Diete />
              </ProtectedRoute>
            }
          />
          <Route
            path="/diete/dettaglio/:id"
            element={
              <ProtectedRoute>
                <DietaDettaglio />
              </ProtectedRoute>
            }
          />
          <Route
            path="/diete/crea-custom"
            element={
              <ProtectedRoute>
                <DietaCustom />
              </ProtectedRoute>
            }
          />
          <Route
            path="/schede"
            element={
              <ProtectedRoute>
                <Schede />
              </ProtectedRoute>
            }
          />
          <Route
            path="/schede/:id"
            element={
              <ProtectedRoute>
                <SchedaDettaglio />
              </ProtectedRoute>
            }
          />
          <Route
            path="/schede/crea-custom"
            element={
              <ProtectedRoute>
                <SchedaCustom />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profilo"
            element={
              <ProtectedRoute>
                <ProfiloUtente />
              </ProtectedRoute>
            }
          />
          {/* Route solo per ADMIN gestione utenti */}
          <Route
            path="/utenti"
            element={
              <ProtectedRoute requireAdmin>
                <GestioneUtenti />
              </ProtectedRoute>
            }
          />
          <Route
            path="/schede/standard-admin"
            element={
              <ProtectedRoute requireAdmin>
                <SchedaStandardAdmin />
              </ProtectedRoute>
            }
          />
          <Route
            path="/schede/custom-admin"
            element={
              <ProtectedRoute requireAdmin>
                <SchedaCustomAdmin />
              </ProtectedRoute>
            }
          />

          {/* Error 404 */}
          <Route path="*" element={<ErrorPage />} />
        </Routes>
      </div>
      <Footer />
    </>
  )
}
