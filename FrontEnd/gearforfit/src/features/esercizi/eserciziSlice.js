import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

// =================== THUNKS (AZIONI ASINCRONE) ===================

// GET /esercizi (Ottieni tutti gli esercizi) - SOLO ADMIN in base al controller
export const fetchAllEsercizi = createAsyncThunk(
  'esercizi/fetchAll',
  async (_, { rejectWithValue }) => {
    try {
      const response = await fetch(`${API_BASE_URL}/esercizi`, {
        headers: getAuthHeader(),
      })
      if (!response.ok) {
        const errorData = await response.json()
        // Sarà un UnauthorizedException se l'utente non è ADMIN
        return rejectWithValue(
          errorData.message || 'Accesso Esercizi non autorizzato (solo Admin)'
        )
      }
      return await response.json()
    } catch (error) {
      return rejectWithValue('Errore di rete nel recupero degli esercizi')
    }
  }
)

// GET /esercizi/{id} (Ottieni esercizio per ID) - SOLO ADMIN in base al controller
export const fetchEsercizioById = createAsyncThunk(
  'esercizi/fetchById',
  async (id, { rejectWithValue }) => {
    try {
      const response = await fetch(`${API_BASE_URL}/esercizi/${id}`, {
        headers: getAuthHeader(),
      })
      if (!response.ok) {
        const errorData = await response.json()
        return rejectWithValue(errorData.message || 'Esercizio non trovato')
      }
      return await response.json()
    } catch (error) {
      return rejectWithValue("Errore di rete nel recupero dell'esercizio")
    }
  }
)

// =================== SLICE E REDUCER ===================

const initialState = {
  listaEsercizi: [],
  dettaglioEsercizio: null,
  isLoading: false,
  error: null,
}

const eserciziSlice = createSlice({
  name: 'esercizi',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      // FETCH ALL ESERCIZI
      .addCase(fetchAllEsercizi.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(fetchAllEsercizi.fulfilled, (state, action) => {
        state.isLoading = false
        state.listaEsercizi = action.payload
      })
      .addCase(fetchAllEsercizi.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload
        state.listaEsercizi = []
      })
      // FETCH BY ID
      .addCase(fetchEsercizioById.fulfilled, (state, action) => {
        state.dettaglioEsercizio = action.payload
      })
  },
})

export default eserciziSlice.reducer
