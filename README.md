# **Sisteme cu microservicii vs. monolit** - Hotel Booking App

## Obiectivul și contextul studiului

În contextul dezvoltării moderne de aplicații software, alegerea arhitecturii potrivite reprezintă un factor esențial pentru eficiență, scalabilitate și mentenabilitate. Proiectul de față își propune să realizeze un **studiu comparativ între arhitectura monolitică și cea bazată pe microservicii**, având ca studiu de caz o **aplicație de rezervare camere de hotel**.
Scopul principal este de a analiza modul în care fiecare arhitectură influențează performanța, optimizarea resurselor și capacitatea de extindere a sistemului. Pentru partea practică, va fi dezvoltat un **sistem de rezervare camere de hotel**, implementat în ambele variante arhitecturale

## Descrierea proiectului

Proiectul constă în dezvoltarea unei **aplicații web pentru rezervarea camerelor de hotel**, implementată în două variante arhitecturale \- monolitică și bazată pe microservicii \- pentru a permite analiza comparativă a performanței.

Aplicația este destinată utilizatorilor finali și are ca scop **facilitarea procesului de căutare și rezervare a camerelor** într-un mod rapid și intuitiv.

#### **Funcționalități principale:**

* **Autentificare și înregistrare utilizatori** \- fiecare utilizator își poate crea un cont și se poate autentifica pentru a gestiona propriile rezervări.

* **Căutare camere disponibile** \- utilizatorul poate căuta camere pe baza criteriilor dorite (dată de sosire, dată de plecare, număr de persoane, preț).

* **Vizualizare detalii cameră** \- afișarea informațiilor esențiale despre camere (descriere, preț, facilități, imagini).

* **Rezervare cameră** \- selectarea unei camere și completarea datelor necesare pentru confirmarea rezervării.

* **Confirmarea rezervării** \- afișarea unui rezumat al rezervării, cu detalii despre cameră, perioadă și cost total.

* **Gestionarea rezervărilor proprii** \- utilizatorul poate vizualiza, modifica sau anula rezervările efectuate.

Aplicația va fi împărțită logic în următoarele module:

* **Modul Utilizatori** \- gestiunea conturilor, autentificare și sesiuni;

* **Modul Camere** \- administrarea datelor despre camere, tarife și facilități;

* **Modul Rezervări** \- gestionarea cererilor și confirmărilor de rezervare;

* **Interfața Web (Frontend)** \- componenta vizuală care interacționează cu utilizatorul final printr-un browser.

## Implementări de referință

Comparația se va axa pe două implementări principale și mai multe implementări secundare, după cum urmează:

**Implementarea Monolitică:** O singură aplicație (un singur proces, o singură unitate de deployment). Toate funcționalitățile sunt implementate ca module interconectate în cadrul aceleiași aplicații.

**Implementarea bazată pe microservicii:** Mai multe servicii independente, fiecare rulând în propriul proces și comunicând prin diverse mecanisme (de exemplu: API HTTP/REST, gRPC etc.).

**Variațiuni ale implementării bazate pe microservicii:** Pentru a extinde analiza, vom implementa diverse componente folosind **diferite limbaje de programare și tehologii** (de exemplu: Java cu Spring Boot vs. Python cu Django vs. Node.js cu Express). Această sub-analiză ne va permite să măsurăm impactul specific al unei tehnologii asupra performanței și resurselor **în cadrul aceluiași stil arhitectural**.

## Metrici de evaluare

Pentru a realiza o comparație obiectivă, bazată pe date, vom colecta și analiza două categorii principale de metrici:

#### **A. Metrici de performanță și metrici operaționale**

Aceste metrici ne vor arăta eficiența sistemelor în timpul execuției, sub diferite scenarii de încărcare.

* **Latență:** Timpul de răspuns (în ms) pentru operațiunile cheie ale API-ului. Printre altele, vom măsura media, mediana, p95, p99, pentru a înțelege experiența utilizatorului în condiții normale și de vârf.  
* **Throughput:** Numărul de request-uri pe secundă (RPS) pe care fiecare sistem le poate gestiona înainte ca performanța să se degradeze semnificativ.  
* **Utilizarea resurselor:**  
  * **CPU:** Procentul de utilizare a procesorului.  
  * **Memorie:** Memoria RAM utilizată și rata de alocare a memoriei.

#### **B. Metrici de dezvoltare și mentenanță**

Aceste metrici ne ajută să măsurăm complexitatea implementării și efortul de mentenanță.

* **Linii de cod:** O metrică de bază pentru a estima dimensiunea codebase-ului.  
* **Viteza ciclului de dezvoltare:**  
  * **Timp de build:** cât durează compilarea și împachetarea întregii aplicații.  
  * **Timp de execuție a testelor:** Durata rulării testelor (unitare, de integrare, end-to-end etc.).  
  * **Timp de deployment:** cât durează publicarea unei noi versiuni în mediul de producție.

## Limbaje de programare și tehnologii

În ceea ce privește alegerea limbajului de programare și a tehnologiilor:

* **Monolit:** Vom folosi un tech stack comun pentru astfel de aplicații: **Java cu Spring Boot**.  
* **Microservicii:** Vom explora mai multe căi. Vom implementa diverse componente folosind **diferite limbaje de programare și tehologii** (de exemplu: Java cu Spring Boot vs. Python cu Django vs. Node.js cu Express).  
* **Containerizare:** Infrastructura va fi gestionată folosind **Kubernetes** pentru a reflecta practicile comune din proiecte reale, de producție.

## Importanță

Proiectul va oferi o imagine completă asupra modului în care arhitectura software influențează performanța, stabilitatea și costurile unui sistem.  
Prin analiza comparativă dintre monolit și microservicii, se vor identifica **avantajele strategice ale fiecărei abordări**, precum și **cele mai bune practici** pentru proiectarea de aplicații scalabile și eficiente.  
Rezultatele vor reprezenta o bază solidă pentru decizii arhitecturale în proiectele viitoare și o contribuție la înțelegerea relației dintre design, performanță și optimizare software.

---

## Descriere generală
Aplicație pentru rezervarea camerelor de hotel, dezvoltată în două variante arhitecturale:  
- Monolitică  
- Microservicii  

Proiectul se desfășoară în cadrul metodologiei Agile/Scrum, având trei sprinturi lunare între 13 octombrie – 31 ianuarie.

---

## Organizare echipă
Project Manager - Oblesniuc Stefana
Team Lead - Dinu Ion-Irinel
Dev - Ciobanu Andrei
Dev - Nicanor Mihaila
Tester - Ceausene Patricia
Tester - Mirica Victor   



## Sprint 1 – Analiză și Design Arhitectural
Perioadă: 13 Octombrie – 10 Noiembrie  
Obiectiv: Definirea cerințelor, arhitecturii și organizarea echipei

### Activități principale
1. Organizare echipă  
2. Definire idee proiect  
3. Setup proiect:
   - Creare repository (GitHub / GitLab)
   - Adăugare contributori
4. Documentație cerințe funcționale și non-funcționale  
5. Studiu comparativ:
   - Arhitectură monolitică vs microservicii  
   - Avantaje și dezavantaje  
6. Creare diagrame UML:
   - Diagrame de cazuri de utilizare  
   - Diagrame de clase, secvență și componente  
7. Realizare Diagrama Gantt + WBS (Work Breakdown Structure)  
8. Milestone 1: Finalizarea documentației de analiză și design

---

## Sprint 2 – Dezvoltare MVP
Perioadă: 11 Noiembrie – 8 Decembrie  
Obiectiv: Implementarea unui MVP funcțional

### Dezvoltare arhitectură Monolitică
1. Setup și structurare proiect  
2. Modul autentificare (login/register – clienți și admin)  
3. CRUD pentru camere și rezervări  
4. Interfață web (frontend integrat)  
5. Testare și validare MVP  

### Dezvoltare arhitectură pe Microservicii
1. Setup arhitectură generală  
2. Auth-Service  
   - Înregistrare, autentificare, validare token  
3. Room-Service  
   - Gestionare camere (CRUD, disponibilitate)  
4. Reservation-Service  
   - Creare și gestionare rezervări  
5. API Gateway  
   - Rutare și securizare request-uri între servicii  
6. Frontend  
   - Aplicație web care consumă API-urile definite  
7. Testare și orchestrare  
   - Testare integrată și orchestrare (Docker / Compose)

Milestone 2: MVP funcțional (rezervare completă)

---

## Sprint 3 – Testare, Optimizare și Documentație Finală
Perioadă: 9 Decembrie – 31 Ianuarie  
Obiectiv: Finalizarea aplicației, testare și documentație

### Activități principale
1. Testare unitară și integrare  
2. Raport de performanță:
   - Comparativ monolit vs microservicii  
3. Optimizări UI/UX  
4. Documentație tehnică finală:
   - Manual utilizator  
   - Raport proiect  
5. Milestone 3: Versiune finală pregătită pentru prezentare

---

## Diagrame și documentație atașată
- Diagrama Gantt  
- WBS (Work Breakdown Structure)  
- UML (use case, class, component, sequence)  
- Raport comparativ arhitectural  

---
 
---

## Milestones rezumate

| Milestone | Perioadă | Obiectiv | Rezultat |
|------------|-----------|-----------|-----------|
| M1 | 13 oct – 10 nov | Analiză și design | Documentație completă și setup proiect |
| M2 | 11 nov – 8 dec | Dezvoltare MVP | MVP funcțional (rezervare completă) |
| M3 | 9 dec – 31 ian | Testare și documentație | Versiune finală + raport comparativ |

---

## Rezultat final
O aplicație complet funcțională de rezervare camere hotel, implementată și comparată în două arhitecturi:
- Monolitică  
- Bazată pe microservicii  


## Diagrama Gantt



