// src/app/store.ts
import { configureStore } from '@reduxjs/toolkit'
import authReducer from '../features/auth/authSlice'
import qeaReducer from '../features/chat/qeaSlice'

export const store = configureStore({
  reducer: {
    auth: authReducer,
    qea: qeaReducer,
  },
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
