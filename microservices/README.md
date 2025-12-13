# Hotel Booking System - Microservices Architecture

A microservices-based hotel booking system built with Spring Boot and Docker. The system provides a complete hotel reservation platform with separate services for user management, room management, bookings, and payments.

## Table of Contents
- [Architecture Overview](#architecture-overview)
- [Services](#services)
- [Technology Stack](#technology-stack)
- [Quick Start](#quick-start)
- [Testing the System](#testing-the-system)
- [Project Structure](#project-structure)
- [Key Design Decisions](#key-design-decisions)

## Architecture Overview

The system consists of 4 independent microservices communicating with a shared database cluster:

```
┌──────────────────────┐
│      Frontend        │
│                      │
└──────┬───────┬───────┘
       │       │
       │       │ REST API
       │       │
   ┌───▼───┐  │
   │ User  │  │
   │Service│  │
   │ 8081  │  │
   └───┬───┘  │
       │      │
       │   ┌──▼──────┐
       │   │ Booking │
       │   │ Service │
       │   │  8083   │
       │   └──┬───┬──┘
       │      │   │
       │      │   │ REST API
       │      │   │
       │  ┌───▼──┐│  ┌────▼────┐
       │  │ Room ││  │ Payment │
       │  │Service││  │ Service │
       │  │ 8082 ││  │  8084   │
       │  └───┬──┘│  └────┬────┘
       │      │   │       │
       └──────┴───┴───────┴──────┐
                                  │
                    ┌─────────────▼────────────┐
                    │   Database Cluster       │
                    │   (PostgreSQL)           │
                    │   hotel_booking_db       │
                    │   Port 5432              │
                    └──────────────────────────┘
```

## Communication Flow

### Frontend → Services (Direct REST API)
- **Frontend** → **User Service** (Port 8081)
  - User registration & authentication
  - JWT token generation & validation
  - User management

- **Frontend** → **Booking Service** (Port 8083)
  - Create bookings
  - View bookings
  - Cancel bookings
  - Check-in/Check-out

### Service → Service (Feign Client REST API)
- **Booking Service** → **Room Service** (Port 8082)
  - Check room availability
  - Get room details
  - Update room status
  - URL: `http://room-service:8082`

- **Booking Service** → **Payment Service** (Port 8084)
  - Process payments
  - Get payment status
  - URL: `http://payment-service:8084`

### Services → Database Cluster
All services connect to the **shared PostgreSQL database cluster**:
- **User Service** → `users` table
- **Room Service** → `hotels`, `rooms` tables
- **Booking Service** → `bookings` table
- **Payment Service** → `payments` table

## Services

### 1. User Service (Port 8081)
**Responsibility**: User authentication and management (placeholder)

**Notes**: This repository includes a placeholder `user-service` with a minimal Spring Boot skeleton and Flyway migrations that create a `users` table and insert test users. The service currently only exposes `/actuator/health` and has no authentication or user APIs implemented yet.

**Key Endpoints**:
- `/actuator/health` - Health endpoint (available)

**Database Tables**: `users` (created by Flyway migrations in `user-service/src/main/resources/db/migration`)

**Swagger UI**: When the service is implemented, Swagger will be available at http://localhost:8081/swagger-ui.html

### 2. Room Service (Port 8082)
**Responsibility**: Hotel and room management

**Key Endpoints**:
- `GET /api/hotels` - List all hotels
- `POST /api/hotels` - Create hotel
- `GET /api/hotels/{id}` - Get hotel details
- `GET /api/rooms/hotel/{hotelId}` - Get rooms for hotel
- `GET /api/rooms/hotel/{hotelId}/available` - Get available rooms
- `POST /api/rooms` - Create room
- `PATCH /api/rooms/{id}/status` - Update room status

**Database Tables**: `hotels`, `rooms`

**Swagger UI**: http://localhost:8082/swagger-ui.html

### 3. Booking Service (Port 8083)
**Responsibility**: Booking management and orchestration

**Key Endpoints**:
- `POST /api/bookings` - Create booking
- `GET /api/bookings/{id}` - Get booking details
- `GET /api/bookings/user/{userId}` - Get user bookings
- `POST /api/bookings/{id}/check-in` - Check-in
- `POST /api/bookings/{id}/check-out` - Check-out
- `POST /api/bookings/{id}/cancel` - Cancel booking

**Database Tables**: `bookings`

**Inter-service Communication**:
- Feign Client to Room Service (room availability)
- Feign Client to Payment Service (payment processing)

**Swagger UI**: http://localhost:8083/swagger-ui.html

### 4. Payment Service (Port 8084)
**Responsibility**: Payment processing

**Key Endpoints**:
- `POST /api/payments` - Process payment
- `GET /api/payments/{id}` - Get payment details
- `GET /api/payments/booking/{bookingId}` - Get payment by booking
- `POST /api/payments/{id}/refund` - Refund payment

**Database Tables**: `payments`

**Swagger UI**: http://localhost:8084/swagger-ui.html

## Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud OpenFeign** (Inter-service Communication)
- **Spring Data JPA** (Database Access)
- **PostgreSQL 15** (Shared Database Cluster)
- **Flyway** (Database migrations)
- **Lombok** (Reduce boilerplate code)
- **SpringDoc OpenAPI** (API documentation / Swagger)

### DevOps
- **Docker & Docker Compose** (Containerization)
- **Spring Boot Actuator** (Health checks)

## Quick Start

### 1. Build and Run All Services

```bash
cd microservices

# Start all services with Docker Compose
docker compose up --build
```

This will:
- Build all Docker images
- Start the Database Cluster (PostgreSQL)
- Start all 4 microservices (User, Room, Booking, Payment)

### 2. Verify Services are Running

Wait for all services to start. Check:

- **User Service**: http://localhost:8081/actuator/health
- **Room Service**: http://localhost:8082/actuator/health
- **Booking Service**: http://localhost:8083/actuator/health
- **Payment Service**: http://localhost:8084/actuator/health

### Monitoring

- **Prometheus (metrics)**: http://localhost:9090
- **Grafana (dashboard)**: http://localhost:3002

The Grafana dashboard for microservices is provisioned under `microservices/grafana/dashboards/hotel-booking-microservices.json`. Use the `Application` variable in Grafana to select the service (booking-service, room-service, payment-service).

To enable metrics scraping, ensure each service exposes the `/actuator/prometheus` endpoint; docker-compose sets `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus` for each service in the `microservices/docker-compose.yml`.

### Exporters (Host Metrics)

- This microservices stack includes `node-exporter` for host-level metrics. Node Exporter provides CPU, memory, disk, and network metrics from the host, available in Prometheus at `http://localhost:9100/metrics` and in Grafana as Node metrics.
- In Grafana, the microservices dashboard exposes a new section "Host Metrics" with panels for Node CPU/Memory; the `Application` variable can be used to select services and the `node-exporter` option to inspect node metrics.

## Database Schema

All services share a single PostgreSQL database: `hotel_booking_db`

### Tables:
- **users** - User accounts (User Service)
- **hotels** - Hotel information (Room Service)
- **rooms** - Room details (Room Service)
- **bookings** - Reservation records (Booking Service)
- **payments** - Payment transactions (Payment Service)

## Testing the System

### Test Workflow

#### 1. Get Available Hotels

```bash
curl -X GET http://localhost:8082/api/hotels/active
```

**Response**:
```json
[
  {
    "id": 1,
    "name": "Grand Hotel Bucharest",
    "city": "Bucharest",
    "starRating": 5
  },
  {
    "id": 2,
    "name": "Palace Hotel Cluj",
    "city": "Cluj-Napoca",
    "starRating": 4
  }
]
```

#### 2. Get Available Rooms for Hotel

```bash
curl -X GET http://localhost:8082/api/rooms/hotel/1/available
```

#### 3. Create a Booking

```bash
curl -X POST http://localhost:8083/api/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "roomId": 1,
    "checkInDate": "2025-12-20",
    "checkOutDate": "2025-12-23",
    "numberOfGuests": 1,
    "paymentMethod": "CREDIT_CARD",
    "specialRequests": "Late check-in please"
  }'
```

**What happens internally**:
1. Booking Service validates dates
2. Calls Room Service to get room details (price, availability)
3. Checks room availability in database
4. Calculates total price (3 nights × room price)
5. Creates booking with confirmation number
6. Updates room status to RESERVED via Room Service
7. Processes payment via Payment Service
8. Returns booking confirmation

**Response**:
```json
{
  "id": 3,
  "userId": 1,
  "roomId": 1,
  "checkInDate": "2025-12-20",
  "checkOutDate": "2025-12-23",
  "totalPrice": 450.00,
  "status": "CONFIRMED",
  "confirmationNumber": "BK-A3F2D8E1",
  "paymentStatus": "PAID"
}
```

#### 4. Get User's Bookings

```bash
curl -X GET http://localhost:8083/api/bookings/user/1
```

#### 5. Check-in

```bash
curl -X POST http://localhost:8083/api/bookings/3/check-in
```

#### 6. Check-out

```bash
curl -X POST http://localhost:8083/api/bookings/3/check-out
```

#### 7. Cancel Booking

```bash
curl -X POST http://localhost:8083/api/bookings/3/cancel \
  -H "Content-Type: application/json" \
  -d '{
    "cancellationReason": "Change of plans"
  }'
```

## Docker Compose Services

```yaml
services:
  - db-cluster (PostgreSQL) - Port 5432
  - room-service - Port 8082
  - booking-service - Port 8083
  - payment-service - Port 8084
```

## Development

### Running Services Locally

1. **Start Database**:
```bash
docker compose up -d db-cluster
```

2. **Start Individual Services**:
```bash
cd room-service
mvn spring-boot:run
```

Repeat for other services.

### Building Services

```bash
cd <service-name>
mvn clean package
```

## Stopping the System

```bash
# Stop all containers
docker compose down
```

## Project Structure

```
microservices/
├── room-service/
│   ├── src/main/java/com/sasps/roomservice/
│   │   ├── RoomServiceApplication.java
│   │   ├── model/Hotel.java, Room.java
│   │   ├── repository/HotelRepository.java, RoomRepository.java
│   │   ├── service/HotelService.java, RoomService.java
│   │   ├── controller/HotelController.java, RoomController.java
│   │   ├── dto/HotelDto.java, RoomDto.java
│   │   └── exception/
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/V1__create_tables.sql
│   ├── Dockerfile
│   └── pom.xml
│
├── booking-service/
│   ├── src/main/java/com/sasps/bookingservice/
│   │   ├── BookingServiceApplication.java
│   │   ├── model/Booking.java
│   │   ├── repository/BookingRepository.java
│   │   ├── service/BookingService.java
│   │   ├── controller/BookingController.java
│   │   ├── client/RoomServiceClient.java, PaymentServiceClient.java
│   │   ├── dto/BookingDto.java, RoomDto.java, PaymentDto.java
│   │   └── exception/
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/V1__create_bookings_table.sql
│   ├── Dockerfile
│   └── pom.xml
│
├── payment-service/
│   ├── src/main/java/com/sasps/paymentservice/
│   │   ├── PaymentServiceApplication.java
│   │   ├── model/Payment.java
│   │   ├── repository/PaymentRepository.java
│   │   ├── service/PaymentService.java
│   │   ├── controller/PaymentController.java
│   │   ├── dto/PaymentDto.java
│   │   └── exception/
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── db/migration/V1__create_payments_table.sql
│   ├── Dockerfile
│   └── pom.xml
│
├── docker-compose.yml
└── README.md
```