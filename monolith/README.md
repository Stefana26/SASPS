# Hotel Booking - Arhitectură Monolitică

## Prezentare Generală

Arhitectura monolitică implementează toată funcționalitatea (utilizatori, camere, rezervări, plăți) într-o singură aplicație care rulează ca un singur proces și partajează o singură bază de date.

## Stack Tehnologic

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - Acces la baza de date
- **Spring Security** - Autentificare & Autorizare
- **PostgreSQL** - Bază de date (via Docker)
- **Maven** - Tool de build
- **Swagger/OpenAPI** - Documentație API

## Cerințe Preliminare

- **Java 17+** 
- **Maven 3.8+**
- **Docker** & **Docker Compose**

## Setup

### 1. Start DB

```bash
docker-compose up -d
```

Aceasta va porni PostgreSQL pe `localhost:5432` cu:
- Database: `hotel_booking_db`
- Username: `postgres`
- Password: `postgres`

### 2. Rulează Aplicația

#### Opțiunea A: Folosind Maven
```bash
mvn spring-boot:run
```

#### Opțiunea B: Din IDE
- Deschide proiectul în IDE-ul
- Rulează clasa main `HotelBookingApplication.java`

#### Opțiunea C: Folosind JAR-ul (după build)
```bash
mvn clean package -DskipTests
java -jar target/hotel-booking-monolith-1.0.0.jar
```

### 3. Accesează Aplicația

- **URL API**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Oprire

```bash
# Oprește baza de date
docker-compose down

# Oprește baza de date și șterge datele
docker-compose down -v
```

## Structura Proiectului

```
monolith/
├── src/
│   ├── main/
│   │   ├── java/com/sasps/hotelbooking/
│   │   │   ├── config/          # Clase de configurare
│   │   │   ├── controller/      # Controllere REST
│   │   │   ├── service/         # Logică de business
│   │   │   ├── repository/      # Layer de acces la date
│   │   │   ├── model/           # Entități JPA
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Excepții custom
│   │   │   └── HotelBookingApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── data.sql
│   └── test/
│       └── java/com/sasps/hotelbooking/
└── pom.xml
```

## Endpoint-uri API

### Hoteluri
- `GET /api/hotels` - Obține toate hotelurile
- `GET /api/hotels/{id}` - Obține hotel după ID
- `POST /api/hotels` - Creează hotel nou
- `PUT /api/hotels/{id}` - Actualizează hotel
- `DELETE /api/hotels/{id}` - Șterge hotel (cascade delete camere)
- `GET /api/hotels/city/{city}` - Hoteluri după oraș
- `POST /api/hotels/search` - Caută hoteluri

### Camere
- `GET /api/rooms/hotel/{hotelId}` - Toate camerele unui hotel
- `GET /api/rooms/hotel/{hotelId}/number/{roomNumber}` - Cameră specifică
- `POST /api/rooms` - Creează cameră nouă (necesită hotelId)
- `PUT /api/rooms/{id}` - Actualizează cameră
- `DELETE /api/rooms/{id}` - Șterge cameră
- `GET /api/rooms/type/{roomType}` - Camere după tip
- `POST /api/rooms/search` - Caută camere disponibile
- `PATCH /api/rooms/{id}/status` - Actualizează status cameră

### Rezervări
- `GET /api/bookings` - Obține toate rezervările
- `GET /api/bookings/{id}` - Obține rezervare după ID
- `POST /api/bookings` - Creează rezervare nouă
- `PUT /api/bookings/{id}` - Actualizează rezervare
- `POST /api/bookings/{id}/confirm` - Confirmă rezervare
- `POST /api/bookings/{id}/cancel` - Anulează rezervare
- `POST /api/bookings/{id}/check-in` - Check-in
- `POST /api/bookings/{id}/check-out` - Check-out

Pentru documentație completă API, vezi [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) sau vizitează Swagger UI la:
**http://localhost:8080/swagger-ui.html**
