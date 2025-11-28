#                               "GearForFit"



GearForFit è una web application per appassionati di fitness dove a seconda del piano utente, si ha accesso a diversi contenuti, personalizzati e non.

Essendo stata strutturata in modo tale che possa essere aggiornata col tempo, si presta molto all'utilizzo per piccole e grandi aziende, avendo delle relazioni tra gli elementi che lo permettono.

Infatti tutti gli esercizi, sono aggiornabili nel tempo, avendo usato diverse classi es: attrezzi, gruppo muscolari, schema serie, giorno allenamento etc, per dare il modo di poter implementare col tempo, altri macchinari o esercizi a loro affiliati.



### **1 INFO APPLICAZIONE**



Profilo utente: A seconda del "TipoPiano" visualizza un diverso badge e tra le funzionalità del componente ci sono: informazioni utente, cambio dati personali/password ed eliminazione profilo).

Diete: Tutti gli utenti registrati hanno accesso a te pagina, dove si possono visualizzare tutte le diete standard, e se si è piano SILVER si possono creare le proprie diete custom e visualizzarle di conseguenza, eliminarle (nel caso delle proprie diete) ed attivare una delle proprie diete. Per quanto riguarda le diete custom, l'utente usa una dieta standard di riferimento, e tramite il calcolo BMR (per età, altezza, peso, ed altri fattori sull'attività), gestisce la quantità di calorie, e le diminuisce o le aumenta a seconda del soggetto, ovviamente aumentando o diminuendo anche le quantità degli alimenti. Tutti gli alimenti vengono caricati tramite un cvs, che all'avvio dell'application popola il DB.

Schede: Tutti gli utenti da SILVER possono accedervi, l'utente può visualizzare tutte le schede standard, e a partire dal piano GOLD può creare le proprie schede custom, visualizzarle, eliminarle ed attivarle se necessario.

Chat QeA: È una funzionalità di chat dove l'utente può accedere a delle risposte automatiche, sui temi più inerenti con la palestra, accessibile solo ai PREMIUM.

Admin Login: L'admin ha tutte le funzionalità degli utenti, ma con la differenza di poter eliminare qualsiasi dieta o scheda, creare schede standard e custom per un utente specifico. Ha una sua rotta per la gestione degli utenti, dove può cambiare il "TipoPiano" all'utente, resettare la password, o eliminare l'account direttamente.



### **2 STACK**



Backend: Java con utilizzo di Spring Boot, Spring Security e Data JPA

Database: PostgreSQL

Frontend: React.js, Bootstrap, React Router, Redux toolkit

Autenticazione: JWT

Storage media: Cloudinary, data.ts (per homepage), locale

Environment: Uso di variabili d'ambiente su file .env



### **3 DOCUMENTAZIONE API BASE\_URL (http://localhost:3001)**



#### 3.1 Autenticazione



📤 POST /auth/register

Crea nuovo account.

json

{

  "email": "test@test.com",

  "password": "test123",

  "nome": "Test",

  "cognome": "Test"

}

📤 POST /auth/login

Restituisce token di accesso.

json

{

  "email":"test@test.com",

  "password": "test123"

}

#### 3.2 Utenti



📥 GET /utenti/me

Auth: bearer token

Restituisce il profilo dell'utente autenticato.

🔄 PUT /utenti/me

Auth: bearer token

Aggiorna il profilo dell'utente autenticato.

json

{

  "nome": "NuovoNome",

  "cognome": "NuovoCognome",

  "email": "nuovaemail@test.com"

}

🔄 PUT /utenti/me/password

Auth: bearer token

Cambia la password dell'utente autenticato.

json

{

  "passwordVecchia": "test123",

  "passwordNuova": "nuovapassword"

}

🗑️ DELETE /utenti/me

Auth: bearer token

Elimina l'account dell'utente autenticato.



#### 3.3 Diete



📥 GET /diete/standard

Auth: bearer token (tutti i piani)

Restituisce una lista di tutte le diete standard.

📥 GET /diete/standard/{id}

Auth: bearer token (tutti i piani)

Nella path si inserisce l'id della dieta.

Restituisce una singola dieta standard specifica per ID.

📤 POST /diete/standard/{dietaStandardId}/custom

Auth: bearer token (SILVER in su)

Si inserisce nella path la dieta standard di riferimento. Tramite il calcolo BMR, la dieta custom viene generata. is\_standard sarà false e is\_attiva sarà true.

json

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

📥 GET /diete/custom

Auth: bearer token (SILVER in su)

Restituisce le diete personalizzate dell'utente.

📥 GET /diete/custom/{id}

Auth: bearer token (SILVER in su)

Nella path si inserisce l'id della dieta.

Restituisce una dieta personalizzata specifica dell'utente.

🔄 PATCH /diete/custom/{id}/attiva

Auth: bearer token (SILVER in su)

Nella path si inserisce l'id della dieta.

json

{

  "attiva": true

}

Attiva o disattiva una dieta personalizzata.

🗑️ DELETE /diete/custom/{id}

Auth: bearer token (SILVER in su)

Nella path si inserisce l'id della dieta.

Elimina una dieta personalizzata.



#### 3.4 Schede



📥 GET /schede-allenamento/esercizi

Auth: bearer token (GOLD in su)

Restituisce tutti gli esercizi.

📥 GET /schede-allenamento/standard

Auth: bearer token (SILVER in su)

Restituisce tutte le schede di allenamento standard.

📥 GET /schede-allenamento/standard/obiettivo/{obiettivo}

Auth: bearer token (SILVER in su)

Nella path si inserisce l'obiettivo, che può essere: DEFINIZIONE, MASSA, MANTENIMENTO.

Restituisce le schede di allenamento standard per obiettivo.

📥 GET /schede-allenamento/

Auth: bearer token (tutti i piani)

Restituisce tutte le schede di allenamento (con filtri).

📥 GET /schede-allenamento/{schedaId}

Auth: bearer token (tutti i piani)

Restituisce una scheda di allenamento per ID.

📤 POST /schede-allenamento/me

Auth: bearer token (GOLD in su)

Crea una scheda di allenamento personalizzata. is\_standard sarà false e is\_attiva sarà true.

Body: (esempio per 2 giorni di allenamento)

json

{

  "nome": "Forza Base Piramidale",

  "descrizione": "Programma di mantenimento con approccio piramidale sui fondamentali, 2 giorni",

  "durataSettimane": 10,

  "obiettivo": "MANTENIMENTO",

  "giorni": \[

  {

  "giornoSettimana": "MARTEDI",

  "serie": \[

  { "esercizioId": 1, "numeroSerie": 4, "numeroRipetizioni": "6", "tempoRecuperoSecondi": 150 },

  { "esercizioId": 15, "numeroSerie": 4, "numeroRipetizioni": "8", "tempoRecuperoSecondi": 120 }

  ]

  },

  {

  "giornoSettimana": "VENERDI",

  "serie": \[

  { "esercizioId": 8, "numeroSerie": 4, "numeroRipetizioni": "5", "tempoRecuperoSecondi": 180 },

  { "esercizioId": 11, "numeroSerie": 4, "numeroRipetizioni": "8", "tempoRecuperoSecondi": 120 }

  ]

  }

  ]

}

📥 GET /schede-allenamento/me

Auth: bearer token (GOLD in su)

Restituisce tutte le schede di allenamento personalizzate dell'utente.

📥 GET /schede-allenamento/me/{schedaId}

Auth: bearer token (GOLD in su)

Restituisce una scheda di allenamento personalizzata specifica dell'utente.

📥 GET /schede-allenamento/me/obiettivo/{obiettivo}

Auth: bearer token (GOLD in su)

Nella path si inserisce l'obiettivo, che può essere: DEFINIZIONE, MASSA, MANTENIMENTO.

Restituisce le schede di allenamento personalizzate dell'utente per obiettivo.

🔄 PUT /schede-allenamento/me/schede/{id}/attiva

Auth: bearer token (GOLD in su)

Attiva una scheda di allenamento per l'utente.

🔄 PATCH /schede-allenamento/me/schede/{schedaId}/serie/{serieId}/peso

Auth: bearer token (GOLD in su)

Aggiorna il peso per una serie in una scheda di allenamento.

json

{

  "peso": "16/18/20"

}

🗑️ DELETE /schede-allenamento/me/{schedaId}

Auth: bearer token (GOLD in su)

Elimina una scheda di allenamento personalizzata.



#### 3.5 Chat QeA



📥 GET /qea

Auth: bearer token (PREMIUM)

Restituisce tutte le domande e risposte.

📥 GET /qea/{id}

Auth: bearer token (PREMIUM)

Restituisce una domanda e risposta per ID.

📥 GET /qea/{id}/domanda

Auth: bearer token (PREMIUM)

Restituisce solo la domanda di una Q\&A.

📥 GET /qea/{id}/risposta

Auth: bearer token (PREMIUM)

Restituisce solo la risposta di una Q\&A.



#### 3.6 Admin dashboard (admin controller)



📥 GET /admin/alimenti

Auth: bearer token (ADMIN)

Restituisce tutti gli alimenti.

📥 GET /admin/alimenti/{id}

Auth: bearer token (ADMIN)

Restituisce un alimento per ID.

🗑️ DELETE /admin/alimenti/{id}

Auth: bearer token (ADMIN)

Elimina un alimento.

📤 POST /admin/diete

Auth: bearer token (ADMIN)

Crea una dieta standard. is\_standard sarà true.

Body: (esempio per 1 giorno e 3 pasti)

json

{

  "nome": "Dieta delete normale dettaglio",

  "descrizione": "Template per dieta ricca di proteine",

  "durataSettimane": 12,

  "tipoDieta": "NORMOCALORICA",

  "pasti": \[

  {

  "nomePasto": "Colazione",

  "ordine": 1,

  "giornoSettimana": "LUNEDI",

  "alimenti": \[

  { "alimentoId": 1, "grammi": 100 },

  { "alimentoId": 2, "grammi": 50 }

  ]

  }

  ]

}

🔄 PUT /admin/diete/{id}

Auth: bearer token (ADMIN)

Modifica una dieta standard. Stesso body della POST.

📥 GET /admin/diete/standard

Auth: bearer token (ADMIN)

Restituisce tutte le diete standard.

📥 GET /admin/diete/custom

Auth: bearer token (ADMIN)

Restituisce tutte le diete personalizzate.

📥 GET /admin/diete/all

Auth: bearer token (ADMIN)

Restituisce tutte le diete (standard e custom).

📥 GET /admin/diete/{dietaId}

Auth: bearer token (ADMIN)

Restituisce una dieta per ID.

📥 GET /admin/diete/custom/utente/{utenteId}

Auth: bearer token (ADMIN)

Restituisce le diete personalizzate di un utente specifico.

🗑️ DELETE /admin/diete/{id}

Auth: bearer token (ADMIN)

Elimina una dieta.

📥 GET /admin/utenti

Auth: bearer token (ADMIN)

Restituisce tutti gli utenti.

📥 GET /admin/utenti/{id}

Auth: bearer token (ADMIN)

Restituisce un utente per ID.

🔄 PUT /admin/utenti/{id}/piano?nuovoPiano=FREE

Auth: bearer token (ADMIN)

Si inserisce nella path l'id utente e nel query param nuovoPiano il tipo di piano: FREE/SILVER/GOLD/PREMIUM.

Modifica il piano di un utente.

🔄 PUT /admin/utenti/reset-password

Auth: bearer token (ADMIN)

Resetta la password di un utente.

json

{

  "utenteId": 1,

  "nuovaPassword": "newpassword123"

}

🗑️ DELETE /admin/utenti/{id}

Auth: bearer token (ADMIN)

Elimina un utente.

📥 GET /admin/schede/all

Auth: bearer token (ADMIN)

Restituisce tutte le schede di allenamento di tutti gli utenti.

📥 GET /admin/schede/utente/{utenteId}

Auth: bearer token (ADMIN)

Restituisce le schede di allenamento di un utente specifico.

📥 GET /admin/schede/{schedaId}

Auth: bearer token (ADMIN)

Restituisce una scheda di allenamento per ID.

📤 POST /admin/schede/standard

Auth: bearer token (ADMIN)

Crea una scheda di allenamento standard. is\_standard sarà true. Stesso body delle schede custom per utente.

🔄 PUT /admin/schede/standard/{schedaId}

Auth: bearer token (ADMIN)

Aggiorna una scheda di allenamento standard. Stesso body della POST.

🗑️ DELETE /admin/schede/standard/{schedaId}

Auth: bearer token (ADMIN)

Elimina una scheda di allenamento standard.

🗑️ DELETE /admin/schede/standard/{schedaId}/esercizi/{esercizioId}

Auth: bearer token (ADMIN)

Rimuove un esercizio da una scheda standard.

📥 GET /admin/qea

Auth: bearer token (ADMIN)

Restituisce tutte le Q\&A.

📤 POST /admin/qea

Auth: bearer token (ADMIN)

Crea una nuova Q\&A.

json

{

  "domanda": "Domanda?",

  "risposta": "Risposta!"

}

📥 GET /admin/qea/{id}

Auth: bearer token (ADMIN)

Restituisce una Q\&A per ID.

🔄 PUT /admin/qea/{id}

Auth: bearer token (ADMIN)

Aggiorna una Q\&A. Stesso body della POST.

🗑️ DELETE /admin/qea/{id}

Auth: bearer token (ADMIN)

Elimina una Q\&A.

📥 GET /admin/esercizi/all

Auth: bearer token (ADMIN)

Restituisce tutti gli esercizi.

📤 POST /admin/esercizi

Auth: bearer token (ADMIN)

Crea un nuovo esercizio.

json

{

  "nome": "nome",

  "descrizione": "descrizione",

  "urlImmagine": "https://example.com/esercizio",

  "gruppoMuscolareId": 1,

  "attrezzoId": 1,

  "isComposto": false

}

🔄 PUT /admin/esercizi/{id}

Auth: bearer token (ADMIN)

Aggiorna un esercizio. Stesso body della POST.

🔄 PATCH /admin/esercizi/{idEsercizio}/image

Auth: bearer token (ADMIN)

Carica un'immagine per un esercizio. Nel campo file del form-data, inserire l'immagine.

📥 GET /admin/esercizi/{id}

Auth: bearer token (ADMIN)

Restituisce un esercizio per ID.

🗑️ DELETE /admin/esercizi/{id}

Auth: bearer token (ADMIN)

Elimina un esercizio.

📥 GET /admin/gruppi-muscolari

Auth: bearer token (ADMIN)

Restituisce tutti i gruppi muscolari.

📤 POST /admin/gruppi-muscolari

Auth: bearer token (ADMIN)

Crea un nuovo gruppo muscolare.

json

{

  "nome": "nome gruppo muscolare"

}

📥 GET /admin/attrezzi

Auth: bearer token (ADMIN)

Restituisce tutti gli attrezzi.

📤 POST /admin/attrezzi

Auth: bearer token (ADMIN)

Crea un nuovo attrezzo.

json

{

  "nome": "nome attrezzo"

}

