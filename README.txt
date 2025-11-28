# GearForFit

GearForFit è una web application per appassionati di fitness dove, a seconda del piano utente, si ha accesso a diversi contenuti, personalizzati e non.

Essendo stata strutturata in modo tale che possa essere aggiornata col tempo, si presta molto all'utilizzo per piccole e grandi aziende, avendo delle relazioni tra gli elementi che lo permettono.

Infatti tutti gli esercizi sono aggiornabili nel tempo, avendo usato diverse classi (es: attrezzi, gruppi muscolari, schema serie, giorno allenamento, etc.) per dare il modo di poter implementare, col tempo, altri macchinari o esercizi a loro affiliati.

## 1. INFO APPLICAZIONE

- **Profilo utente**: A seconda del "TipoPiano" visualizza un diverso badge. Tra le funzionalità del componente ci sono: informazioni utente, cambio dati personali/password ed eliminazione profilo.
- **Diete**: Tutti gli utenti registrati hanno accesso a questa pagina, dove si possono visualizzare tutte le diete standard. Se si è piano **SILVER** si possono creare le proprie diete custom, visualizzarle, eliminarle ed attivarle. Per quanto riguarda le diete custom, l'utente usa una dieta standard di riferimento e, tramite il calcolo BMR (per età, altezza, peso ed altri fattori sull'attività), gestisce la quantità di calorie diminuendole o aumentandole a seconda del soggetto (aggiornando di conseguenza le quantità degli alimenti). Tutti gli alimenti vengono caricati tramite un CSV che popola il DB all'avvio dell'application.
- **Schede**: Tutti gli utenti da **SILVER** possono accedervi. L'utente può visualizzare tutte le schede standard e, a partire dal piano **GOLD**, può creare le proprie schede custom, visualizzarle, eliminarle ed attivarle se necessario.
- **Chat Q&A**: È una funzionalità di chat dove l'utente può accedere a delle risposte automatiche sui temi più inerenti alla palestra. Accessibile solo ai **PREMIUM**.
- **Admin Login**: L'admin ha tutte le funzionalità degli utenti, ma con la differenza di poter eliminare qualsiasi dieta o scheda e creare schede standard e custom per un utente specifico. Ha una rotta dedicata per la gestione degli utenti, dove può cambiare il "TipoPiano", resettare la password o eliminare l'account direttamente.

## 2. STACK

- **Backend**: Java con utilizzo di Spring Boot, Spring Security e Data JPA
- **Database**: PostgreSQL
- **Frontend**: React.js, Bootstrap, React Router, Redux toolkit
- **Autenticazione**: JWT
- **Storage media**: Cloudinary, data.ts (per homepage), locale
- **Environment**: Uso di variabili d'ambiente su file .env

## 3. DOCUMENTAZIONE API

**BASE_URL**: `http://localhost:3001`

### 3.1 Autenticazione

#### 📤 POST /auth/register
Crea nuovo account.
