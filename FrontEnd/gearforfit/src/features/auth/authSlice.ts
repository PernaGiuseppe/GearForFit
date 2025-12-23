// src/features/auth/authSlice.ts
import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

// =================== TIPI ===================
export type User = {
  id: number
  email: string
  nome: string
  cognome: string
  tipoUtente: 'ADMIN' | 'USER'
  tipoPiano: 'PREMIUM' | 'GOLD' | 'SILVER' | 'FREE'
  token: string
}
export type UserData = User

export type AuthState = {
  user: User | null
  isLoading: boolean
  error: string | null
}
// logica per chat ADMIN/PREMIUM access
export const canUserAccessChat = (user: User | null): boolean => {
  if (!user) return false
  // user.tipoUtente === 'ADMIN' ||
  return user.tipoPiano === 'PREMIUM'
}

// Funzione per caricare lo stato iniziale da localStorage
const loadUserFromStorage = (): User | null => {
  try {
    const serializedUser = localStorage.getItem('user')
    if (serializedUser === null) {
      return null
    }
    return JSON.parse(serializedUser) as User
  } catch (e) {
    console.error('Errore nel caricamento dello stato da localStorage', e)
    return null
  }
}

const initialState: AuthState = {
  user: loadUserFromStorage(), // Carica l'utente all'avvio
  isLoading: false, // Inizializza a false
  error: null,
}

const handleFetch = (
  url: string,
  options: RequestInit,
  rejectWithValue: (value: string) => any
) => {
  return fetch(url, options)
    .then((response) => {
      if (!response.ok) {
        return response.json().then((errorData) => {
          throw new Error(errorData.message || 'Errore nella richiesta')
        })
      }
      return response.json()
    })
    .then((data: UserData) => {
      // Salva solo il token qui se necessario per le chiamate successive immediate
      if (data.token) {
        localStorage.setItem('accessToken', data.token)
      }
      return data
    })
    .catch((error) => {
      return rejectWithValue(
        error.message || 'Errore di rete o server non disponibile'
      )
    })
}

// =================== 2. THUNKS (AZIONI ASINCRONE) ===================

// POST /auth/login
export const loginUser = createAsyncThunk<
  UserData,
  any,
  { rejectValue: string }
>('auth/login', (credentials, { rejectWithValue }) => {
  return handleFetch(
    `${API_BASE_URL}/auth/login`,
    {
      method: 'POST',
      headers: getAuthHeader(),
      body: JSON.stringify(credentials),
    },
    rejectWithValue
  )
})

// POST /auth/register
export const registerUser = createAsyncThunk<
  UserData,
  any,
  { rejectValue: string }
>('auth/register', (userData, { rejectWithValue }) => {
  return handleFetch(
    `${API_BASE_URL}/auth/register`,
    {
      method: 'POST',
      headers: getAuthHeader(),
      body: JSON.stringify(userData),
    },
    rejectWithValue
  )
})

// =================== 3. SLICE E REDUCER ===================
const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<User>) {
      state.user = action.payload
      localStorage.setItem('user', JSON.stringify(action.payload))
    },
    logout(state) {
      state.user = null
      state.error = null
      localStorage.removeItem('user')
      localStorage.removeItem('accessToken')
    },
  },
  extraReducers: (builder) => {
    builder
      // LOGIN
      .addCase(loginUser.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.isLoading = false
        state.user = action.payload
        state.error = null
        // FIX: Salva l'utente nel localStorage dopo il login riuscito
        localStorage.setItem('user', JSON.stringify(action.payload))
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
        state.user = null
      })
      // REGISTER
      .addCase(registerUser.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(registerUser.fulfilled, (state, action) => {
        state.isLoading = false
        state.error = null
      })
      .addCase(registerUser.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload as string
        state.user = null
      })
  },
})

export const { logout, setUser } = authSlice.actions
export default authSlice.reducer
