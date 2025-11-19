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

export default function App() {
  const user = useSelector((s: RootState) => s.auth.user)

  return (
    <>
      <Navbar />
      <div className="container mt-4">
        <Routes>
          {/* NoAuth Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/articolo/:id" element={<ArticoloDettaglio />} />
          <Route
            path="/admin"
            element={
              <ProtectedRoute requireAdmin={true}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          {/* Free/Silver Routes */}
          <Route
            path="/diete"
            element={
              <ProtectedRoute>
                <Diete />
              </ProtectedRoute>
            }
          />
          {/* Free/Silver Routes */}
          <Route
            path="/dieta/:id"
            element={
              <ProtectedRoute>
                <DietaDettaglio />
              </ProtectedRoute>
            }
          />
          {/* Silver/Gold Routes */}
          <Route
            path="/schede"
            element={
              <ProtectedRoute>
                <Schede />
              </ProtectedRoute>
            }
          />
          {/* Silver/Gold Routes */}
          <Route
            path="/scheda/:id"
            element={
              <ProtectedRoute>
                <SchedaDettaglio />
              </ProtectedRoute>
            }
          />

          <Route path="*" element={<ErrorPage />} />
        </Routes>
        {/* Premium e Admin chat */}
        {user &&
          (user.tipoUtente === 'ADMIN' || user.tipoPiano === 'PREMIUM') && (
            <Chat />
          )}
      </div>
      <Footer />
    </>
  )
}
