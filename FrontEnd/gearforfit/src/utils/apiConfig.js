// URL di base dell'API (Spring Boot)
export const API_BASE_URL = 'http://localhost:3001'

// Funzione per ottenere l'header di autorizzazione (usato in tutti i thunks protetti)
export const getAuthHeader = (contentType = 'application/json') => {
  const token = localStorage.getItem('accessToken')
  const headers = {
    Accept: 'application/json',
  }
  if (contentType) {
    headers['Content-Type'] = contentType
  }
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  return headers
}
