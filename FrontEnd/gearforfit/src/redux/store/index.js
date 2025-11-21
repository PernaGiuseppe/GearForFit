// src/app/store/index.js (o store/store.js)
import { configureStore } from '@reduxjs/toolkit'
import authReducer from '../features/auth/authSlice'
import utenteReducer from '../features/utente/utenteSlice'
import dietaReducer from '../features/dieta/dietaSlice'
import schedaReducer from '../features/scheda/schedaSlice'

export const store = configureStore({
  reducer: {
    auth: authReducer,
    utente: utenteReducer,
    dieta: dietaReducer,
    scheda: schedaReducer,
    // Aggiungi qui altri reducers
  },
})

// Esporta i tipi per l'uso in TypeScript (se necessario)
// export type RootState = ReturnType<typeof store.getState>;
// export type AppDispatch = typeof store.dispatch;
