# GearForFit

GearForFit è una web application per appassionati di fitness dove a seconda del piano utente, si ha accesso a diversi contenuti, personalizzati e non.
Essendo stata strutturata in modo tale che possa essere aggiornata col tempo, si presta molto all'utilizzo per piccole e grandi aziende, avendo delle relazioni tra gli elementi che lo permettono.
Infatti tutti gli esercizi, sono aggiornabili nel tempo, avendo usato diverse classi es: attrezzi, gruppo muscolari, schema serie, giorno allenamento etc, per dare il modo di poter implementare col tempo, altri macchinari o esercizi a loro affiliati.

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

{"email": "test@test.com", "password": "test123", "nome": "Test", "cognome": "Test"}

#### 📤 POST /auth/login

Restituisce token di accesso.

{"email":"test@test.com","password": "test123"}

### 3.2 Utenti

#### ⬇️ GET /utenti/me

_Auth: bearer token_
Restituisce il profilo dell'utente autenticato.

#### ⬆️ PUT /utenti/me

_Auth: bearer token_
Aggiorna il profilo dell'utente autenticato.

{ "nome": "NuovoNome","cognome": "NuovoCognome", "email": "nuovaemail@test.com"}

#### ⬆️ PUT /utenti/me/password

_Auth: bearer token_
Cambia la password dell'utente autenticato.

{"passwordVecchia": "test123","passwordNuova": "nuovapassword"}

#### 🗑️ DELETE /utenti/me

_Auth: bearer token_
Elimina l'account dell'utente autenticato.

### 3.3 Diete

#### 📤 POST /diete/standard/{dietaStandardId}/custom

_Auth: bearer token (SILVER in su)_
Si inserisce nella path la dieta standard di riferimento. Tramite il calcolo BMR, la dieta custom viene generata. `is_standard` sarà false e `is_attiva` sarà true di default.

{
"nome": "La mia dieta personalizzata",
"descrizione": "Dieta per perdere peso",
"peso": 75.0,
"altezza": 170,
"eta": 25,
"sesso": "M",
"livelloAttivita": "MODERATO",
"tipoDieta": "IPOCALORICA"
}

Restituisce la dieta creata.

_Nota: Sono disponibili le diverse 📥 GET a seconda dei piani (FREE: solo standard, SILVER+: anche custom). È disponibile la 🗑️ DELETE sulle proprie diete._

#### 🛠️ PATCH /diete/custom/{id}/attiva

_Auth: bearer token (SILVER in su)_
Attiva o disattiva una dieta personalizzata (id nella path).

{ "attiva": true }

### 3.4 Schede

#### 📥 GET /schede-allenamento/esercizi

_Auth: bearer token (GOLD in su)_
Restituisce tutti gli esercizi, usato per la creazione delle schede custom.

#### 📤 POST /schede-allenamento/me

_Auth: bearer token (GOLD in su)_
Crea una scheda di allenamento personalizzata. `is_standard` sarà false e `is_attiva` sarà true di default.

{ "nome": "Forza Base Piramidale",
"descrizione": "Programma di mantenimento con approccio piramidale sui fondamentali, 2 giorni",
"durataSettimane": 10, "obiettivo": "MANTENIMENTO","giorni": [
{"giornoSettimana": "MARTEDI","serie": [
{ "esercizioId": 1, "numeroSerie": 4, "numeroRipetizioni": "6", "tempoRecuperoSecondi": 150 },
{ "esercizioId": 15, "numeroSerie": 4, "numeroRipetizioni": "8", "tempoRecuperoSecondi": 120 }]},
{ "giornoSettimana": "VENERDI", "serie":
{ "esercizioId": 8, "numeroSerie": 4, "numeroRipetizioni": "5", "tempoRecuperoSecondi": 180 },
{ "esercizioId": 11, "numeroSerie": 4, "numeroRipetizioni": "8", "tempoRecuperoSecondi": 120 }]}]}

#### 🔄 PUT /schede-allenamento/me/schede/{id}/attiva

_Auth: bearer token (GOLD in su)_
Attiva una scheda di allenamento per l'utente.

#### 🛠️ PATCH /schede-allenamento/me/schede/{schedaId}/serie/{serieId}/peso

_Auth: bearer token (GOLD in su)_
Aggiorna il peso per una serie in una scheda di allenamento.

{ "peso": "16/18/20" }

_Nota: Sono disponibili le diverse 📥 GET a seconda del piano (SILVER: solo standard, GOLD/PREMIUM: anche custom con filtri enum MASSA/DEFINIZIONE/MANTENIMENTO) e la 🗑️ DELETE per le schede in proprio possesso._

### 3.5 Chat Q&A

#### 📥 GET /qea/{id}

_Auth: bearer token (PREMIUM)_
Restituisce una singola domanda o risposta. Utilizzabile solo da PREMIUM e ADMIN.
Il flusso prevede una GET per la domanda e, al click, una GET per la risposta corrispondente.

### 3.6 Admin

#### Alimenti

L'admin ha la possibilità di fare le GET di ogni alimento e di eliminarlo. Il DB viene popolato da un file CSV aggiornabile nel tempo.

#### Diete

**📤 POST /admin/diete**
Crea una dieta standard. `is_standard` sarà true, `is_attiva` sarà false.

{ "nome": "Dieta delete normale dettaglio",
"descrizione": "Template per dieta ricca di proteine",
"durataSettimane": 12,
"tipoDieta": "NORMOCALORICA",
"pasti": [
{ "nomePasto": "Colazione", "ordine": 1, "giornoSettimana": "LUNEDI","alimenti": [
{ "alimentoId": 1, "grammi": 100 },
{ "alimentoId": 2, "grammi": 50 }]}]}

_L'ADMIN ha controllo completo (GET/DELETE) su tutte le diete._

#### Dashboard Utenti

**🔄 PUT /admin/utenti/{id}/piano?nuovoPiano=FREE**
Modifica il piano di un utente (upgrade/downgrade). Parametri: id utente nella path, nuovo piano (FREE/SILVER/GOLD/PREMIUM) nella query.

**🔄 PUT /admin/utenti/reset-password**
Resetta la password di un utente.

{ "utenteId": 1, "nuovaPassword": "newpassword123" }

_L'ADMIN può anche bloccare/attivare gli utenti e fare la DELETE._

#### Esercizi

**📤 POST /admin/gruppi-muscolari**

{ "nome": "nome gruppo muscolare" }

**📤 POST /admin/attrezzi**

{ "nome": "nome attrezzo" }

**📤 POST /admin/esercizi**

{ "nome": "nome",
"descrizione": "descrizione",
"urlImmagine": "https://example.com/esercizio",
"gruppoMuscolareId": 1,
"attrezzoId": 1,
"isComposto": false }

**🔄 PUT /admin/esercizi/{id}**
Aggiorna un esercizio (stesso body della POST).

**🛠️ PATCH /admin/esercizi/{idEsercizio}/image**
Carica un'immagine (form-data file).

_L'ADMIN ha accesso a GET/DELETE di gruppi muscolari, attrezzi ed esercizi._

#### Schede palestra

**📤 POST /admin/schede/standard**
Crea scheda standard (`is_standard` true). Stesso body delle custom.

**🔄 PUT /admin/schede/standard/{schedaId}**
Aggiorna scheda standard.

_L'ADMIN può fare GET/DELETE di qualsiasi scheda._

#### Q&A

**📤 POST /admin/qea**

{ "domanda": "Domanda?", "risposta": "Risposta!" }

**🔄 PUT /admin/qea/{id}**
Aggiorna Q&A.

{ "domanda": "Domanda aggiornata?", "risposta": "Risposta aggiornata!" }

_Come sopracitato, può fare le 📥 GET come il piano PREMIUM ed la 🗑️ DELETE delle Q&A._
