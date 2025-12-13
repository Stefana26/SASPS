# Auth Service

JWT-based authentication microservice for the SASPS Hotel Booking System.

## Features

- **User Registration (Signup)**: Create new user accounts with email validation
- **User Authentication (Login)**: Authenticate users and issue JWT tokens
- **Token Validation**: Validate JWT tokens for other microservices
- **Token Refresh**: Refresh expired access tokens using refresh tokens
- **Role-Based Access**: Support for USER and ADMIN roles
- **Password Encryption**: Secure password storage using BCrypt
- **Health Monitoring**: Actuator endpoints with Prometheus metrics

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT
- **PostgreSQL** for user data storage
- **Flyway** for database migrations
- **Docker** for containerization
- **Prometheus** for metrics collection
- **OpenAPI/Swagger** for API documentation

## API Endpoints

### Authentication Endpoints

#### 1. Register New User
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "john.doe",
  "email": "john.doe@example.com",
  "password": "secure123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "USER"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john.doe",
  "email": "john.doe@example.com",
  "role": "USER"
}
```

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "secure123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john.doe",
  "email": "john.doe@example.com",
  "role": "USER"
}
```

#### 3. Validate Token
```http
POST /api/auth/validate
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "valid": true,
  "username": "john.doe",
  "message": "Token is valid"
}
```

#### 4. Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john.doe",
  "email": "john.doe@example.com",
  "role": "USER"
}
```

#### 5. Health Check
```http
GET /api/auth/health
```

**Response:**
```json
{
  "message": "Auth service is running"
}
```

## Configuration

### Environment Variables

The service can be configured using the following properties in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://db-cluster:5432/hotel_booking_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT Configuration
jwt.secret=YourSuperSecretJWTKeyThatIsAtLeast256BitsLongForHS256Algorithm
jwt.expiration=86400000        # 24 hours in milliseconds
jwt.refresh-expiration=604800000  # 7 days in milliseconds
```

### Default Users

The service comes with two pre-configured users:

1. **Admin User**
   - Username: `admin`
   - Password: `admin123`
   - Email: `admin@sasps.com`
   - Role: `ADMIN`

2. **Test User**
   - Username: `testuser`
   - Password: `user123`
   - Email: `test@sasps.com`
   - Role: `USER`

## Building and Running

### Using Docker Compose

```bash
# Build and start all services
cd microservices
docker-compose up -d auth-service

# View logs
docker-compose logs -f auth-service

# Stop the service
docker-compose down
```

### Local Development

```bash
# Build the project
mvn clean package

# Run the service
java -jar target/auth-service-1.0.0.jar
```

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## Monitoring

### Actuator Endpoints

- **Health Check**: `http://localhost:8080/actuator/health`
- **Metrics**: `http://localhost:8080/actuator/metrics`
- **Prometheus Metrics**: `http://localhost:8080/actuator/prometheus`

### API Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Security Considerations

1. **JWT Secret**: Change the `jwt.secret` in production to a secure, randomly generated key
2. **Password Policy**: Implement stronger password requirements in production
3. **HTTPS**: Always use HTTPS in production environments
4. **Rate Limiting**: Consider implementing rate limiting for authentication endpoints
5. **Token Rotation**: Implement token rotation strategies for enhanced security

## Integration with Other Services

Other microservices can validate tokens by calling the `/api/auth/validate` endpoint:

```java
// Example validation call from another service
RestTemplate restTemplate = new RestTemplate();
ValidateTokenRequest request = new ValidateTokenRequest(token);
ValidateTokenResponse response = restTemplate.postForObject(
    "http://auth-service:8080/api/auth/validate",
    request,
    ValidateTokenResponse.class
);

if (response.getValid()) {
    // Token is valid, proceed with request
    String username = response.getUsername();
}
```

## Port Configuration

- **Service Port**: 8080
- **Database Port**: 5432 (PostgreSQL)
- **Prometheus Port**: 9090
- **Grafana Port**: 3002

## Troubleshooting

### Database Connection Issues

If the service fails to connect to the database:

```bash
# Check if PostgreSQL is running
docker-compose ps db-cluster

# View database logs
docker-compose logs db-cluster
```

### Token Validation Failures

- Ensure the JWT secret is consistent across service restarts
- Check token expiration times
- Verify clock synchronization between services

## License

Part of the SASPS Hotel Booking System project.
