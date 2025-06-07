# 📘 BookInLine Backend Documentation

## Overview

BookInLine is a Spring Boot-based backend application designed as an analogue of Booking.com. The system supports user registration, property management, booking operations, and reviews. It includes role-based access control, performance optimizations, and extensive testing coverage.

## 🚀 Tech Stack

1. **Framework**: Spring Boot
2. **Database**: PostgreSQL
3. **Caching**: Redis
4. **Security**: JWT-based authentication
5. **Migration Tool**: Flyway
6. **Documentation**: Swagger (OpenAPI)
7. **Monitoring**: Spring Actuator + Prometheus
8. **Containerization**: Docker
9. **Testing**: JUnit/Mockito (unit & integration tests)

## 🔐 Roles & Permissions 

| Role  |                       Description                               |
|-------|-----------------------------------------------------------------|
| Guest |Registered users who can book properties and leave reviews       |
| Host  |Registered users who can manage their own properties and bookings|
| Admin |Has full control over users, properties, bookings, and reviews   |

## 🧩 Core entities

### User

1. **Fields**: id, email, password, fullName, phoneNumber, status (ENUM based), statusDescription, role (and relation fields)
2. **Feautures**:
      - Register as HOST or GUEST
      - Public: View property by ID, list all properties (with filtering & sorting)
      - Host: Create, update, delete own properties
      - Admin: View any entity, disable property booking, manage user statuses, delete reviews, booking cancelling. (Can not do GUEST or HOST actions (create bookings/properties etc.), only admin actions)

### Property

1. **Fields**: id, title, description, city, propertyType (ENUM based), floorArea, bedrooms, address, pricePerNight, maxGuests, available, averageRating (and relation fields)
2. **Features**:
      - Public: View property by ID, list all properties (with filtering & sorting)
      - Host: Create, update, delete own properties
      - Admin: Close property (booking disabled), view any property

### Booking

1. **Fields**: id, checkInDate, checkOutDate (and relation fields)
2. **Features**:
      - Guest: Create booking, cancel booking, view own bookings
      - Host: View bookings for own properties, confirm bookings
      - Admin: View & cancel any booking
      - Public: View unavailable dates for a property

### Review

1. **Fields**: id, rating, comment, createdAt (and relation fields)
2. **Features**:
      - Guest: Leave a review (only after checked_out and if not reviewed yet), update/delete own review
      - All: View reviews by user or property
      - Admin: Delete any review

### Image

1. **Fields**: id, imageUrl (and relation fields)
2. Attached to Property

## ⚙️ API overview 

### 🔑 Authentication & Authorization

1. JWT-based
2. Role-based endpoint restrictions
3. Rate limiting per IP/user to prevent abuse

### 🧪 Testing

1. **Unit tests**: For most services and controllers
2. **Integration tests**: Written for all sensitive methods. Separated via Maven profile integration.

### 🧾 Validation & Error Handling

1. DTO-based data transfer
2. Request validation with custom annotations
3. Centralized exception handling with custom exceptions

### 📊 Monitoring & Observability

1. Spring Actuator endpoints enabled
2. Integrated with Prometheus for metrics scraping

### 🐳 Deployment

1. Fully dockerized for development
2. Dev environment includes: <br /> 
      · PostgreSQL <br />
      · Redis <br />
      · Backend app with exposed Swagger UI <br />

### 📚 Swagger API Docs

Swagger UI is enabled and available at: <br /> 
`http://localhost:8080/swagger-ui/index.html` <br />
It provides complete documentation for all endpoints, including security schemes, schemas, and example payloads.

### ✅ Future Improvements / TODOs

1. React front-end for more enjoyable testing/viewing of the project.
2. Host analytics dashboard.
3. Notifications (email/SMS).
4. Email verification on registration.

## ▶️ How to Run the Project

## 🛠 Manual Setup (Local Development)

**1. Start PostgreSQL**
Make sure you have PostgreSQL running locally. Create a database (e.g., bookinline) and configure your credentials in application-dev.properties:
```
spring.datasource.url=jdbc:postgresql://localhost:5432/bookinline
spring.datasource.username=your_username
spring.datasource.password=your_password
```
**2. Start Redis**
Run Redis locally (default port 6379). You can install it via:
```
# On macOS (Homebrew)
brew install redis
brew services start redis

# On Linux
sudo apt install redis-server
sudo service redis-server start
```
Configure Redis in application.properties if necessary:
```
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
```
**3. Run the App**
Use Maven to build and run the application:
```
mvn clean install
mvn spring-boot:run
```
**Swagger UI** will be available at:
`http://localhost:8080/swagger-ui/index.html`

## 🐳 Docker-based Setup

The project includes a Docker configuration for local development. <br />
**1. Build the Docker Image** <br />

`docker-compose build` <br />

**2. Run with Docker Compose** <br /> 

The docker-compose.yml file contains services for the app, PostgreSQL, and Redis: <br />

`docker-compose -d up` <br />

This will:
 - Start the backend service
 - Launch Redis and PostgreSQL containers
 - Set up environment automatically using pre-configured values

✅ No need to modify application.properties for Docker — configs are injected via environment variables or mounted files.
