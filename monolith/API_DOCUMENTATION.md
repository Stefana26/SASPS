# Documentatie API Hotel Booking

## URL de Baza
```
http://localhost:8080/api
```
Viziteaza `http://localhost:8080/swagger-ui.html` pentru documentatie API interactiva cu Swagger UI.

---

## API Gestionare Hoteluri

### 1. Obtine Toate Hotelurile
**Endpoint:** `GET /api/hotels`

**Descriere:** Returneaza lista tuturor hotelurilor.

**Parametri Query:**
- `activeOnly` (Boolean, optional): Doar hoteluri active (default: true)

**Raspuns:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Grand Hotel Bucharest",
    "description": "Luxury hotel in the heart of the capital",
    "address": "123 Victory Street",
    "city": "Bucharest",
    "country": "Romania",
    "postalCode": "010101",
    "phoneNumber": "+40211234567",
    "email": "contact@grandhotel.ro",
    "website": "www.grandhotel.ro",
    "starRating": 5,
    "amenities": "WiFi, Pool, SPA, Restaurant, Parking",
    "imageUrl": null,
    "active": true,
    "totalRooms": 50,
    "availableRooms": 35,
    "createdAt": "2024-11-09T10:00:00",
    "updatedAt": "2024-11-09T10:00:00"
  }
]
```

### 2. Obtine Hotel dupa ID
**Endpoint:** `GET /api/hotels/{id}`

**Parametri Path:**
- `id` (Long): ID-ul hotelului

**Raspuns:** `200 OK`

### 3. Creaza Hotel
**Endpoint:** `POST /api/hotels`

**Body Request:**
```json
{
  "name": "Grand Hotel Bucharest",
  "description": "Luxury hotel in the heart of the capital",
  "address": "123 Victory Street",
  "city": "Bucharest",
  "country": "Romania",
  "postalCode": "010101",
  "phoneNumber": "+40211234567",
  "email": "contact@grandhotel.ro",
  "website": "www.grandhotel.ro",
  "starRating": 5,
  "amenities": "WiFi, Pool, SPA, Restaurant, Parking",
  "imageUrl": "https://example.com/hotel.jpg",
  "active": true
}
```

**Reguli de Validare:**
- `name`: Obligatoriu, maxim 200 caractere
- `address`: Obligatoriu, maxim 200 caractere
- `city`: Obligatoriu, maxim 100 caractere
- `country`: Obligatoriu, maxim 100 caractere
- `phoneNumber`: Optional, format valid (10-20 cifre)
- `email`: Optional, format email valid
- `starRating`: Optional, intre 1 si 5
- Alte campuri: Optionale

**Raspuns:** `201 Created`

### 4. Actualizeaza Hotel
**Endpoint:** `PUT /api/hotels/{id}`

**Parametri Path:**
- `id` (Long): ID-ul hotelului

**Body Request:** La fel ca la Creare Hotel (toate campurile optionale)

**Raspuns:** `200 OK`

### 5. Sterge Hotel
**Endpoint:** `DELETE /api/hotels/{id}`

**Parametri Path:**
- `id` (Long): ID-ul hotelului

**Raspuns:** `204 No Content`

**Note:** Camerele hotelului sunt sterse automat (cascade delete)

### 6. Obtine Hoteluri dupa Oras
**Endpoint:** `GET /api/hotels/city/{city}`

**Parametri Path:**
- `city` (String): Numele orasului

**Raspuns:** `200 OK` - Returneaza lista hotelurilor din acel oras

### 7. Cauta Hoteluri
**Endpoint:** `POST /api/hotels/search`

**Body Request:**
```json
{
  "searchTerm": "Grand",
  "city": "Bucharest",
  "country": "Romania",
  "minStarRating": 4,
  "onlyWithAvailableRooms": true
}
```

**Reguli de Validare:**
- `minStarRating`: Optional, intre 1 si 5
- Toate campurile sunt optionale

**Raspuns:** `200 OK` - Returneaza lista hotelurilor care corespund criteriilor

---

## API Gestionare Camere

### 1. Obtine Camerele unui Hotel
**Endpoint:** `GET /api/rooms/hotel/{hotelId}`

**Parametri Path:**
- `hotelId` (Long): ID-ul hotelului

**Raspuns:** `200 OK` - Returneaza toate camerele hotelului specificat

### 2. Obtine Camera dupa Hotel si Numar
**Endpoint:** `GET /api/rooms/hotel/{hotelId}/number/{roomNumber}`

**Parametri Path:**
- `hotelId` (Long): ID-ul hotelului
- `roomNumber` (String): Numarul camerei

**Raspuns:** `200 OK` - Returneaza camera specificata

### 3. Creaza Camera
**Endpoint:** `POST /api/rooms`

**Body Request:**
```json
{
  "hotelId": 1,
  "roomNumber": "101",
  "roomType": "SINGLE",
  "pricePerNight": 200.00,
  "maxOccupancy": 1,
  "description": "Comfortable single room",
  "facilities": "WiFi, TV, AC",
  "floorNumber": 1,
  "imageUrl": "https://example.com/room101.jpg",
  "status": "AVAILABLE"
}
```

**Reguli de Validare:**
- `hotelId`: Obligatoriu
- `roomNumber`: Obligatoriu, maxim 10 caractere, unic per hotel
- `roomType`: Obligatoriu, unul din [SINGLE, DOUBLE, TWIN, SUITE, DELUXE, PRESIDENTIAL]
- `pricePerNight`: Obligatoriu, trebuie sa fie > 0
- `maxOccupancy`: Obligatoriu, intre 1 si 10
- `description`: Optional, maxim 1000 caractere
- `facilities`: Optional, maxim 500 caractere
- `floorNumber`: Optional, intre -5 si 100
- `status`: Optional, implicit AVAILABLE

**Raspuns:** `201 Created`

### 4. Actualizeaza Camera
**Endpoint:** `PUT /api/rooms/{id}`

**Parametri Path:**
- `id` (Long): ID-ul camerei

**Body Request:** La fel ca la Creare Camera (toate campurile optionale)

**Raspuns:** `200 OK`

### 5. Sterge Camera
**Endpoint:** `DELETE /api/rooms/{id}`

**Parametri Path:**
- `id` (Long): ID-ul camerei

**Raspuns:** `204 No Content`

### 6. Obtine Camere dupa Tip
**Endpoint:** `GET /api/rooms/type/{roomType}`

**Parametri Path:**
- `roomType`: Unul din [SINGLE, DOUBLE, TWIN, SUITE, DELUXE, PRESIDENTIAL]

**Raspuns:** `200 OK`

### 7. Cauta Camere Disponibile
**Endpoint:** `POST /api/rooms/search`

**Body Request:**
```json
{
  "hotelId": 1,
  "roomType": "DOUBLE",
  "minOccupancy": 2,
  "minPrice": 100.00,
  "maxPrice": 300.00,
  "checkInDate": "2024-12-01",
  "checkOutDate": "2024-12-05"
}
```

**Reguli de Validare:**
- `checkInDate`: Obligatoriu, trebuie sa fie azi sau in viitor
- `checkOutDate`: Obligatoriu, trebuie sa fie in viitor
- `hotelId`: Optional, pentru a filtra dupa un anumit hotel
- Alte campuri: Optionale

**Raspuns:** `200 OK` - Returneaza lista camerelor disponibile care corespund criteriilor

### 8. Actualizeaza Status Camera
**Endpoint:** `PATCH /api/rooms/{id}/status`

**Parametri Path:**
- `id` (Long): ID-ul camerei

**Parametri Query:**
- `status`: Noul status al camerei

**Raspuns:** `200 OK`

---

## API Gestionare Rezervari

### 1. Obtine Toate Rezervarile
**Endpoint:** `GET /api/bookings`

**Raspuns:** `200 OK`
```json
[
  {
    "id": 1,
    "userId": 1,
    "userFullName": "John Doe",
    "roomId": 1,
    "roomNumber": "101",
    "checkInDate": "2024-12-01",
    "checkOutDate": "2024-12-05",
    "numberOfGuests": 2,
    "totalPrice": 400.00,
    "status": "CONFIRMED",
    "specialRequests": "Late check-in",
    "confirmationNumber": "BK-A1B2C3D4",
    "paymentStatus": "PAID",
    "paymentMethod": "CREDIT_CARD",
    "paidAmount": 400.00,
    "numberOfNights": 4,
    "createdAt": "2024-11-09T10:00:00",
    "updatedAt": "2024-11-09T10:00:00"
  }
]
```

### 2. Obtine Rezervare dupa ID
**Endpoint:** `GET /api/bookings/{id}`

**Parametri Path:**
- `id` (Long): ID-ul rezervarii

**Raspuns:** `200 OK`

### 3. Obtine Rezervare dupa Numar de Confirmare
**Endpoint:** `GET /api/bookings/confirmation/{confirmationNumber}`

**Parametri Path:**
- `confirmationNumber` (String): Numar de confirmare rezervare

**Raspuns:** `200 OK`

### 4. Obtine Rezervarile Utilizatorului
**Endpoint:** `GET /api/bookings/user/{userId}`

**Parametri Path:**
- `userId` (Long): ID-ul utilizatorului

**Raspuns:** `200 OK` - Returneaza toate rezervarile utilizatorului

### 5. Obtine Rezervarile Active ale Utilizatorului
**Endpoint:** `GET /api/bookings/user/{userId}/active`

**Parametri Path:**
- `userId` (Long): ID-ul utilizatorului

**Raspuns:** `200 OK` - Returneaza doar rezervarile active (PENDING, CONFIRMED, CHECKED_IN)

### 6. Obtine Rezervarile unei Camere
**Endpoint:** `GET /api/bookings/room/{roomId}`

**Parametri Path:**
- `roomId` (Long): ID-ul camerei

**Raspuns:** `200 OK` - Returneaza toate rezervarile camerei

### 7. Creaza Rezervare
**Endpoint:** `POST /api/bookings`

**Body Request:**
```json
{
  "userId": 1,
  "roomId": 1,
  "checkInDate": "2024-12-01",
  "checkOutDate": "2024-12-05",
  "numberOfGuests": 2,
  "specialRequests": "Late check-in after 10 PM",
  "paymentMethod": "CREDIT_CARD"
}
```

**Reguli de Validare:**
- `userId`: Obligatoriu
- `roomId`: Obligatoriu
- `checkInDate`: Obligatoriu, trebuie sa fie azi sau in viitor
- `checkOutDate`: Obligatoriu, trebuie sa fie dupa check-in
- `numberOfGuests`: Obligatoriu, intre 1 si 10
- `specialRequests`: Optional, maxim 1000 caractere
- `paymentMethod`: Optional, maxim 50 caractere

**Reguli Business:**
- Camera trebuie sa fie disponibila
- Camera trebuie sa fie disponibila pentru datele selectate
- Numarul de oaspeti nu poate depasi capacitatea maxima a camerei
- Durata rezervarii nu poate depasi 30 de nopti
- Pretul total este calculat automat

**Raspuns:** `201 Created`

### 8. Actualizeaza Rezervare
**Endpoint:** `PUT /api/bookings/{id}`

**Parametri Path:**
- `id` (Long): ID-ul rezervarii

**Body Request:** (toate campurile optionale)
```json
{
  "checkInDate": "2024-12-02",
  "checkOutDate": "2024-12-06",
  "numberOfGuests": 3,
  "specialRequests": "Updated request",
  "status": "CONFIRMED",
  "paymentStatus": "PAID",
  "paymentMethod": "CREDIT_CARD",
  "paidAmount": 500.00
}
```

**Reguli Business:**
- Nu se pot actualiza rezervari anulate sau finalizate
- Daca se schimba datele, camera trebuie sa fie disponibila pentru noile date
- Pretul total este recalculat daca se schimba datele

**Raspuns:** `200 OK`

### 9. Anuleaza Rezervare
**Endpoint:** `POST /api/bookings/{id}/cancel`

**Parametri Path:**
- `id` (Long): ID-ul rezervarii

**Body Request:**
```json
{
  "cancellationReason": "Change of plans"
}
```

**Reguli de Validare:**
- `cancellationReason`: Obligatoriu, maxim 500 caractere

**Reguli Business:**
- Se pot anula doar rezervari cu status PENDING sau CONFIRMED

**Raspuns:** `200 OK`

### 10. Confirma Rezervare
**Endpoint:** `POST /api/bookings/{id}/confirm`

**Parametri Path:**
- `id` (Long): ID-ul rezervarii

**Body Request:**
```json
{
  "paymentAmount": 400.00,
  "paymentMethod": "CREDIT_CARD"
}
```

**Reguli de Validare:**
- `paymentAmount`: Obligatoriu, trebuie sa fie > 0
- `paymentMethod`: Obligatoriu

**Reguli Business:**
- Se pot confirma doar rezervari cu status PENDING

**Raspuns:** `200 OK`

### 11. Check-in Rezervare
**Endpoint:** `POST /api/bookings/{id}/check-in`

**Parametri Path:**
- `id` (Long): ID-ul rezervarii

**Reguli Business:**
- Se poate face check-in doar pentru rezervari CONFIRMED
- Nu se poate face check-in inainte de data de check-in
- Statusul camerei este actualizat la OCCUPIED

**Raspuns:** `200 OK`

### 12. Check-out Rezervare
**Endpoint:** `POST /api/bookings/{id}/check-out`

**Parametri Path:**
- `id` (Long): ID-ul rezervarii

**Reguli Business:**
- Se poate face check-out doar pentru rezervari CHECKED_IN
- Statusul camerei este actualizat la AVAILABLE

**Raspuns:** `200 OK`

### 13. Sterge Rezervare
**Endpoint:** `DELETE /api/bookings/{id}`

**Parametri Path:**
- `id` (Long): ID-ul rezervarii

**Reguli Business:**
- Nu se pot sterge rezervari active (trebuie anulate mai intai)

**Raspuns:** `204 No Content`

---

## Enumerari

### RoomType (Tip Camera)
- `SINGLE`: Camera single
- `DOUBLE`: Camera dubla
- `TWIN`: Camera twin (doua paturi separate)
- `SUITE`: Suite
- `DELUXE`: Camera deluxe
- `PRESIDENTIAL`: Suite prezidential

### RoomStatus (Status Camera)
- `AVAILABLE`: Camera este disponibila pentru rezervare
- `OCCUPIED`: Camera este ocupata in prezent
- `MAINTENANCE`: Camera este in mentenanta
- `RESERVED`: Camera este rezervata
- `OUT_OF_SERVICE`: Camera este scoasa din serviciu

### BookingStatus (Status Rezervare)
- `PENDING`: Rezervarea este in asteptare
- `CONFIRMED`: Rezervarea este confirmata
- `CHECKED_IN`: Oaspetele a facut check-in
- `CHECKED_OUT`: Oaspetele a facut check-out
- `CANCELLED`: Rezervarea a fost anulata
- `NO_SHOW`: Oaspetele nu s-a prezentat

### PaymentStatus (Status Plata)
- `PENDING`: Plata este in asteptare
- `PAID`: Plata finalizata
- `PARTIALLY_PAID`: Plata partiala primita
- `REFUNDED`: Plata rambursata
- `FAILED`: Plata a esuat
