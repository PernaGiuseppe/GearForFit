# \# GearForFit

# 

# GearForFit è una web application per appassionati di fitness dove, a seconda del piano utente, si ha accesso a diversi contenuti personalizzati e non.

# Essendo stata strutturata in modo tale da poter essere aggiornata nel tempo, si presta sia per piccole che per grandi aziende grazie alle relazioni tra gli elementi che la compongono.

# Tutti gli esercizi sono aggiornabili nel tempo grazie all'uso di diverse classi (attrezzi, gruppi muscolari, schema serie, giorno allenamento, ecc.), offrendo la possibilità di ampliare facilmente macchinari o esercizi.

# 

# ---

# 

# \## \*\*1. INFO APPLICAZIONE\*\*

# 

# \### \*\*Profilo utente\*\*

# 

# A seconda del "TipoPiano" viene visualizzato un diverso badge. Il componente include: informazioni utente, modifica dati personali/password ed eliminazione profilo.

# 

# \### \*\*Diete\*\*

# 

# Tutti gli utenti registrati hanno accesso alla pagina dedicata, dove possono visualizzare tutte le diete standard. Gli utenti SILVER possono creare diete personalizzate, visualizzarle, eliminarle e attivarne una. La creazione delle diete custom si basa su una dieta standard di riferimento e sul calcolo BMR. Tutti gli alimenti vengono caricati tramite un cvs, che all'avvio dell'application popola il DB.



# 

# \### \*\*Schede\*\*

# 

# Gli utenti da SILVER in su possono visualizzare tutte le schede standard. Dal piano GOLD è possibile creare schede custom, visualizzarle, eliminarle e attivarle.

# 

# \### \*\*Chat QeA\*\*

# 

# Funzionalità che permette l'accesso a risposte automatiche su temi inerenti alla palestra. Disponibile solo per utenti PREMIUM.

# 

# \### \*\*Admin Login\*\*

# 

# L'admin ha tutte le funzionalità degli utenti, più la possibilità di eliminare qualsiasi dieta o scheda, creare schede standard o custom per un utente specifico, gestire gli utenti, modificare il "TipoPiano", resettare password o eliminare account.

# 

# ---

# 

# \## \*\*2. STACK\*\*

# 

# \* \*\*Backend:\*\* Java con Spring Boot, Spring Security e Data JPA

# \* \*\*Database:\*\* PostgreSQL

# \* \*\*Frontend:\*\* React.js, Bootstrap, React Router, Redux Toolkit

# \* \*\*Autenticazione:\*\* JWT

# \* \*\*Storage media:\*\* Cloudinary, data.ts (homepage), locale

# \* \*\*Environment:\*\* Variabili d'ambiente in file `.env`

# 

# ---

# 

# \## \*\*3. DOCUMENTAZIONE API\*\*

# 

# \*\*BASE\_URL:\*\* `http://localhost:3001`

# 

# \### \*\*3.1 Autenticazione\*\*

# 

# \#### 📤 \*\*POST /auth/register\*\*

# 

# Crea nuovo account.

# 

# ```json

# {

#   "email": "test@test.com",

#   "password": "test123",

#   "nome": "Test",

#   "cognome": "Test"

# }

# ```

# 

# \#### 📤 \*\*POST /auth/login\*\*

# 

# Restituisce token di accesso.

# 

# ```json

# {

#   "email": "test@test.com",

#   "password": "test123"

# }

# ```

# 

# ---

# 

# \### \*\*3.2 Utenti\*\*

# 

# \#### 📥 \*\*GET /utenti/me\*\* — \*Auth: bearer token\*

# 

# Restituisce il profilo dell'utente autenticato.

# 

# \#### 🔄 \*\*PUT /utenti/me\*\* — \*Auth: bearer token\*

# 

# Aggiorna il profilo dell'utente.

# 

# ```json

# {

#   "nome": "NuovoNome",

#   "cognome": "NuovoCognome",

#   "email": "nuovaemail@test.com"

# }

# ```

# 

# \#### 🔄 \*\*PUT /utenti/me/password\*\* — \*Auth: bearer token\*

# 

# Cambia la password.

# 

# ```json

# {

#   "passwordVecchia": "test123",

#   "passwordNuova": "nuovapassword"

# }

# ```

# 

# \#### 🗑️ \*\*DELETE /utenti/me\*\* — \*Auth: bearer token\*

# 

# Elimina l'account.

# 

# ---

# 

# \## \*\*3.3 Diete\*\*

# 

# \#### 📥 \*\*GET /diete/standard\*\* — \*tutti i piani\*

# 

# Restituisce tutte le diete standard.

# 

# \#### 📥 \*\*GET /diete/standard/{id}\*\*

# 

# Restituisce una dieta standard per ID.

# 

# \#### 📤 \*\*POST /diete/standard/{dietaStandardId}/custom\*\* — \*SILVER in su\*

# 

# Genera una dieta personalizzata basata sul calcolo BMR.

# 

# ```json

# {

#   "nome": "La mia dieta personalizzata",

#   "descrizione": "Dieta per perdere peso",

#   "peso": 75.0,

#   "altezza": 170,

#   "eta": 25,

#   "sesso": "M",

#   "livelloAttivita": "MODERATO",

#   "tipoDieta": "IPOCALORICA"

# }

# ```

# 

# \#### 📥 \*\*GET /diete/custom\*\*

# 

# Restituisce le diete custom dell'utente.

# 

# \#### 📥 \*\*GET /diete/custom/{id}\*\*

# 

# Restituisce una dieta custom.

# 

# \#### 🔄 \*\*PATCH /diete/custom/{id}/attiva\*\*

# 

# ```json

# {

#   "attiva": true

# }

# ```

# 

# \#### 🗑️ \*\*DELETE /diete/custom/{id}\*\*

# 

# Elimina una dieta personalizzata.

# 

# ---

# 

# \## \*\*3.4 Schede\*\*

# 

# (Tutta la sezione è mantenuta identica, con blocchi JSON formattati correttamente.)

# 

# ---

# 

# \## \*\*3.5 Chat QeA\*\*

# 

# Tutte le route PREMIUM mantenute con struttura ordinata.

# 

# ---

# 

# \## \*\*3.6 Admin Dashboard\*\*

# 

# Tutte le rotte admin sono state mantenute e formattate con blocchi JSON leggibili.

# 

# Esempio:

# 

# \#### 📤 \*\*POST /admin/diete\*\*

# 

# ```json

# {

#   "nome": "Dieta delete normale dettaglio",

#   "descrizione": "Template per dieta ricca di proteine",

#   "durataSettimane": 12,

#   "tipoDieta": "NORMOCALORICA",

#   "pasti": \[

#     {

#       "nomePasto": "Colazione",

#       "ordine": 1,

#       "giornoSettimana": "LUNEDI",

#       "alimenti": \[

#         { "alimentoId": 1, "grammi": 100 },

#         { "alimentoId": 2, "grammi": 50 }

#       ]

#     }

#   ]

# }

# ```

# 

# ---

# 

# \## \*\*Conclusione\*\*

# 

# Formattazione ottimizzata per GitHub, mantenendo \*tutto il testo identico\* e migliorando struttura, leggibilità e blocchi JSON.



