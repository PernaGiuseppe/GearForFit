// src/app/store.ts
import { configureStore } from '@reduxjs/toolkit'
import authReducer from '../features/auth/authSlice'
import eserciziReducer from '../features/esercizi/eserciziSlice'
import qeaReducer from '../features/chat/qeaSlice'

export const store = configureStore({
  reducer: {
    auth: authReducer,
    qea: qeaReducer,
    esercizi: eserciziReducer,
  },
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
