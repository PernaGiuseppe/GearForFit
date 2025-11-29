// src/features/chat/qeaSlice.js (Aggiornato con fetch().then().catch())
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import { API_BASE_URL, getAuthHeader } from '../../utils/apiConfig'

// =================== THUNKS (AZIONI ASINCRONE) ===================

// GET /qea (Ottieni tutte le Q&A) - Usato per popolare la lista delle domande.
// AsyncThunk serve a gestire le azioni asincrone, in modo da rendere il codice più snello.
export const fetchAllQeA = createAsyncThunk(
  'qea/fetchAll',
  (_, { rejectWithValue }) => {
    return fetch(`${API_BASE_URL}/qea`, {
      headers: getAuthHeader(),
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((errorData) => {
            throw new Error(errorData.message || 'Accesso Q&A non autorizzato')
          })
        }
        return response.json()
      })
      .catch((error) => {
        return rejectWithValue(
          error.message || 'Errore di rete nel recupero delle Q&A'
        )
      })
  }
)

// GET /qea/{id}/risposta (Ottieni solo la risposta di una Q&A)
export const fetchRispostaById = createAsyncThunk(
  'qea/fetchRispostaById',
  (id, { rejectWithValue }) => {
    return fetch(`${API_BASE_URL}/qea/${id}/risposta`, {
      headers: getAuthHeader(),
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((errorData) => {
            throw new Error(errorData.message || 'Risposta Q&A non trovata')
          })
        }
        return response.json()
      })
      .catch((error) => {
        return rejectWithValue(
          error.message || 'Errore di rete nel recupero della risposta'
        )
      })
  }
)

// =================== SLICE E REDUCER ===================

const initialState = {
  listaQeA: [], // Array di tutte le Q&A caricate inizialmente
  rispostaCorrente: null, // Risposta del Q&A selezionato
  isLoading: false,
  error: null,
}

const qeaSlice = createSlice({
  name: 'qea',
  initialState,
  reducers: {
    clearRispostaCorrente(state) {
      state.rispostaCorrente = null
      state.error = null
    },
  },
  extraReducers: (builder) => {
    builder
      // FETCH ALL Q&A
      .addCase(fetchAllQeA.pending, (state) => {
        state.isLoading = true
        state.error = null
      })
      .addCase(fetchAllQeA.fulfilled, (state, action) => {
        state.isLoading = false
        state.listaQeA = action.payload
        state.error = null
      })
      .addCase(fetchAllQeA.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload
        state.listaQeA = []
        state.rispostaCorrente = null
      })
      // FETCH RISPOSTA BY ID
      .addCase(fetchRispostaById.pending, (state) => {
        state.isLoading = true
        state.rispostaCorrente = null
        state.error = null
      })
      .addCase(fetchRispostaById.fulfilled, (state, action) => {
        state.isLoading = false
        state.rispostaCorrente = action.payload
        state.error = null
      })
      .addCase(fetchRispostaById.rejected, (state, action) => {
        state.isLoading = false
        state.error = action.payload
        state.rispostaCorrente = null
      })
  },
})

export const { clearRispostaCorrente } = qeaSlice.actions
export default qeaSlice.reducer
