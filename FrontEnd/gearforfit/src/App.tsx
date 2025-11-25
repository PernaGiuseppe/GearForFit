// src/App.tsx

import { Routes, Route } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { RootState } from './app/store'
import ProtectedRoute from './components/ProtectedRoute'
import LoginPage from './components/Login/LoginPage'
import Diete from './components/Diete/Diete'
import Schede from './components/Schede/Schede'
import Chat from './components/Home/Chat'
import AdminDashboard from './components/Home/AdminDashboard'
import Home from './components/Home/Home'
import RegisterPage from './components/Login/RegisterPage'
import ArticoloDettaglio from './components/Home/ArticoloDettaglio'
import SchedaDettaglio from './components/Schede/SchedaDettaglio'
import DietaDettaglio from './components/Diete/DietaDettaglio'
import ErrorPage from './components/Home/ErrorPage'
import Navbar from './components/Home/Navbar'
import Footer from './components/Home/Footer'
import ProfiloUtente from './components/Login/ProfiloUtente'
// NUOVO IMPORT
import GestioneUtenti from './components/Admin/GestioneUtenti'

export default function App() {
  const user = useSelector((s: RootState) => s.auth.user)

  return (
    <>
      <Navbar />
      <div className="container mt-4">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/articoli/:id" element={<ArticoloDettaglio />} />

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
            path="/profilo"
            element={
              <ProtectedRoute>
                <ProfiloUtente />
              </ProtectedRoute>
            }
          />
          <Route
            path="/chat"
            element={
              <ProtectedRoute>
                <Chat />
              </ProtectedRoute>
            }
          />

          {/* Route protette SOLO per ADMIN */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute adminOnly>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/utenti"
            element={
              <ProtectedRoute adminOnly>
                <GestioneUtenti />
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
