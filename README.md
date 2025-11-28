# &nbsp;                                    "GearForFit"

# 

# GearForFit è una web application per appassionati di fitness dove a seconda del piano utente, si ha accesso a diversi contenuti, personalizzati e non.

# Essendo stata strutturata in modo tale che possa essere aggiornata col tempo, si presta molto all'utilizzo per piccole e grandi aziende, avendo delle relazioni tra gli elementi che lo permettono.

# Infatti tutti gli esercizi, sono aggiornabili nel tempo, avendo usato diverse classi es: attrezzi, gruppo muscolari, schema serie, giorno allenamento etc, per dare il modo di poter implementare col tempo, altri macchinari o esercizi a loro affiliati.

# 

# 1 INFO APPLICAZIONE

# 

# -Profilo utente: A seconda del "TipoPiano" visualizza un diverso badge e tra le funzionalità del componente ci sono: informazioni utente, cambio dati personali/password ed eliminazione profilo).

# -Diete: Tutti gli utenti registrati hanno accesso a te pagina, dove si possono visualizzare tutte le diete standard, e se si è piano SILVER si possono creare le proprie diete custom e visualizzarle di conseguenza, eliminarle (nel caso delle proprie diete) ed attivare una delle proprie diete. Per quanto riguarda le diete custom, l'utente usa una dieta standard di riferimento, e tramite il calcolo BMR (per età, altezza, peso, ed altri fattori sull'attività), gestisce la quantità di calorie, e le diminuisce o le aumenta a seconda del soggetto, ovviamente aumentando o diminuendo anche le quantità degli alimenti. Tutti gli alimenti vengono caricati tramite un cvs, che all'avvio dell'application popola il DB.

# -Schede: Tutti gli utenti da SILVER possono accedervi, l'utente può visualizzare tutte le schede standard, e a partire dal piano GOLD può creare le proprie schede custom, visualizzarle, eliminarle ed attivarle se necessario.

# -Chat QeA: È una funzionalità di chat dove l'utente può accedere a delle risposte automatiche, sui temi più inerenti con la palestra, accessibile solo ai PREMIUM.

# -Admin Login: L'admin ha tutte le funzionalità degli utenti, ma con la differenza di poter eliminare qualsiasi dieta o scheda, creare schede standard e custom per un utente specifico. Ha una sua rotta per la gestione degli utenti, dove può cambiare il "TipoPiano" all'utente, resettare la password, o eliminare l'account direttamente.

# 

# 2 STACK

# 

# -Backend: Java con utilizzo di Spring Boot, Spring Security e Data JPA

# -Database: PostgreSQL

# -Frontend: React.js, Bootstrap, React Router, Redux toolkit

# -Autenticazione: JWT

# -Storage media: Cloudinary, data.ts (per homepage), locale

# -Environment: Uso di variabili d'ambiente su file .env

# 

# 3 DOCUMENTAZIONE API BASE\_URL (http://localhost:3001)

# 

# 3.1 Autenticazione

# 

# 📤 POST /auth/register

# Crea nuovo account.

# {"email": "test@test.com", "password": "test123", "nome": "Test", "cognome": "Test"}

# 

# 📤 POST /auth/login

# Restituisce token di accesso.

# {"email":"test@test.com","password": "test123"}

# 

# 3.2 Utenti

# 

# ⬇️ GET /utenti/me

# Auth: bearer token

# Restituisce il profilo dell'utente autenticato.

# 

# ⬆️ PUT /utenti/me

# Auth: bearer token

# Aggiorna il profilo dell'utente autenticato.

# 

# { "nome": "NuovoNome","cognome": "NuovoCognome", "email": "nuovaemail@test.com"}

# 

# ⬆️ PUT /utenti/me/password

# Auth: bearer token

# Cambia la password dell'utente autenticato.

# {"passwordVecchia": "test123","passwordNuova": "nuovapassword"}

# 

# 🗑️ DELETE /utenti/me

# Auth: bearer token

# Elimina l'account dell'utente autenticato.

# 

# 3.3 Diete

# 

# 📤 POST /diete/standard/{dietaStandardId}/custom

# Auth: bearer token (SILVER in su)

# Si inserisce nella path la dieta standard di riferimento. Tramite il calcolo BMR, la dieta custom viene generata. is\_standard sarà false e is\_attiva sarà true di defaut.

# 

# { "nome": "La mia dieta personalizzata",

# "descrizione": "Dieta per perdere peso",

# "peso": 75.0,

# "altezza": 170,

# "eta": 25,

# "sesso": "M",

# "livelloAttivita": "MODERATO",

# "tipoDieta": "IPOCALORICA" }

# Restituisce la dieta creata.

# 

# Poi le diverse 📥 GET a seconda dei piani, se FREE ha accesso solo alle diete standard, invece se SILVER (in su) anche alle diete custom, a lui appartenenti.

# Ovviamente anche la 🗑️ DELETE sulle proprie diete.

# 

# 🛠️ PATCH /diete/custom/{id}/attiva

# Auth: bearer token (SILVER in su)

# Nella path si inserisce l'id della dieta.

# { "attiva": true }

# Attiva o disattiva una dieta personalizzata.

# 

# 3.4 Schede

# 

# 📥 GET /schede-allenamento/esercizi

# Auth: bearer token (GOLD in su)

# Restituisce tutti gli esercizi, usato per la creazione delle diete custom.



# 📤 POST /schede-allenamento/me

# Auth: bearer token (GOLD in su)

# Crea una scheda di allenamento personalizzata. is\_standard sarà false e is\_attiva sarà true di defaut.

# Body: (esempio per 2 giorni di allenamento)

# {

# "nome": "Forza Base Piramidale",

# "descrizione": "Programma di mantenimento con approccio piramidale sui fondamentali, 2 giorni",

# "durataSettimane": 10,

# "obiettivo": "MANTENIMENTO",

# "giorni": \[

# {

# "giornoSettimana": "MARTEDI",

# "serie": \[

# { "esercizioId": 1, "numeroSerie": 4, "numeroRipetizioni": "6", "tempoRecuperoSecondi": 150 },

# { "esercizioId": 15, "numeroSerie": 4, "numeroRipetizioni": "8", "tempoRecuperoSecondi": 120 }

# ]

# },

# {

# "giornoSettimana": "VENERDI",

# "serie": \[

# { "esercizioId": 8, "numeroSerie": 4, "numeroRipetizioni": "5", "tempoRecuperoSecondi": 180 },

# { "esercizioId": 11, "numeroSerie": 4, "numeroRipetizioni": "8", "tempoRecuperoSecondi": 120 }]}]}

# 

# 🔄 PUT /schede-allenamento/me/schede/{id}/attiva

# Auth: bearer token (GOLD in su)

# Attiva una scheda di allenamento per l'utente.



# 🛠️ PATCH /schede-allenamento/me/schede/{schedaId}/serie/{serieId}/peso

# Auth: bearer token (GOLD in su)

# Aggiorna il peso per una serie in una scheda di allenamento.

# { "peso": "16/18/20" }

# 

# Le diverse 📥 GET a seconda del piano, se SILVER solo diete standard se GOLD/PREMIUM anche delle schede custom a lui appartenenti, con l’uso anche di filtri per obbiettivo (MASSA, DEFINIZIONE, MANTENIMENTO “enum”).

# E anche la 🗑️ DELETE, sempre per le schede in suo possesso.

# 

# 3.5 Chat QeA

# 

# 📥 GET /qea/{id} risposta o domanda

# Auth: bearer token (PREMIUM)

# Restituisce una singola domande o risposta.

# Utilizzabile solo da PREMIUM e ADMIN.

# Le fetch nel progetto sono due, prima la GET di ogni domanda, e al click di quella domanda, viene fatta la GET della risposta corrispondente.

# 

# 3.6 Admin 

# 

# -Alimenti: ha la possibilità di fare le GET di ogni alimento e di eliminarlo, però il DB viene popolato da un file csv, che può essere aggiornato nel tempo.

# 

# -Diete:

# 📤 POST /admin/diete

# Crea una dieta standard. is\_standard sarà true, is\_attiva sarà false.

# Body: (esempio per 1 giorno e 3 pasti)

# { "nome": "Dieta delete normale dettaglio",

# "descrizione": "Template per dieta ricca di proteine",

# "durataSettimane": 12,

# "tipoDieta": "NORMOCALORICA",

# "pasti": \[

# {

# "nomePasto": "Colazione",

# "ordine": 1,

# "giornoSettimana": "LUNEDI",

# "alimenti": \[

# { "alimentoId": 1, "grammi": 100 },

# { "alimentoId": 2, "grammi": 50 }]}]}

# 

# Ovviamente l’ADMIN ha il controllo completo sulle diete, può fare le 📥 GET di singole diete, custom o standard che siano, entrambe essendo nella stessa table e di fare la 🗑️ DELETE a suo piacimento.

# 

# -Dashboard Utenti:

# 

# 🔄 PUT /admin/utenti/{id}/piano?nuovoPiano=FREE

# Si inserisce nella path l'id utente e nel query param nuovoPiano il tipo di piano: FREE/SILVER/GOLD/PREMIUM.

# Modifica il piano di un utente, con upgrade o downgrade.

# 

# 🔄 PUT /admin/utenti/reset-password

# Resetta la password di un utente, come se fosse una password mandata per email.

# {  "utenteId": 1,"nuovaPassword": "newpassword123" }

# 

# Infine l’ADMIN può sia “bloccare” l’account degli utenti, disattivandoli (o attivandoli), e può anche fare la 🗑️  DELETE dell’utente.

# 

# Esercizi:

# 

# 📤 POST /admin/gruppi-muscolari

# Crea un nuovo gruppo muscolare.

# { "nome": "nome gruppo muscolare" }

# 

# 📤 POST /admin/attrezzi

# Crea un nuovo attrezzo.

# { "nome": "nome attrezzo" }

# 

# 📤 POST /admin/esercizi

# Crea un nuovo esercizio.

# {

# "nome": "nome",

# "descrizione": "descrizione",

# "urlImmagine": "https://example.com/esercizio",

# "gruppoMuscolareId": 1,

# "attrezzoId": 1,

# "isComposto": false

# }

# 

# 🔄 PUT /admin/esercizi/{id}

# Aggiorna un esercizio. Stesso body della POST.

# 

# 🛠️ PATCH /admin/esercizi/{idEsercizio}/image

# Carica un'immagine per un esercizio. Nel campo file del form-data, inserire l'immagine.

# 

# L’ADMIN ha accesso a tutti i 📥 GET dei gruppi muscolari, attrezzi ed esercizi, ed ovviamente anche alle 🗑️ DELETE di essi.

# 

# Schede palestra:

# 

# 📤 POST /admin/schede/standard

# Crea una scheda di allenamento standard. is\_standard sarà true. 

# Stesso body delle schede custom per utente.

# 

# 🔄 PUT /admin/schede/standard/{schedaId}

# Aggiorna una scheda di allenamento standard. 

# Stesso body della POST.

# 

# Può fare ovviamente le 📥 GET di qualsiasi scheda, standard o custom che sia, usando anche i filtri per obbiettivo, e può fare la 🗑️ DELETE di qualsiasi scheda a suo piacimento.

# 

# Q\&A:

# 

# 📤 POST /admin/qea

# Crea una nuova Q\&A.

# { "domanda": "Domanda?",

# "risposta": "Risposta!" }

# 

# 🔄 PUT /admin/qea/{id}

# Aggiorna una Q\&A. Stesso body della POST.

# 

# Come sopracitato, può fare le 📥 GET come il piano PREMIUM ed la 🗑️ DELETE delle Q\&A.

# 

# 

# 



