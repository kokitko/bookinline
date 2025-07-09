# 📘 BookInLine Backend Documentation

## Overview

BookInLine is a Spring Boot-based backend application inspired by Booking.com. The system supports user registration, property management, booking operations, and reviews with role-based access control, performance optimizations, and extensive testing coverage.

---

## 🚀 Tech Stack

1. **Framework**: Spring Boot
2. **Database**: PostgreSQL
3. **Caching**: Redis
4. **Security**: JWT-based authentication & CSRF protection
5. **Migration Tool**: Flyway
6. **Documentation**: Swagger (OpenAPI)
7. **Monitoring**: Spring Actuator + Prometheus
8. **Containerization**: Docker
9. **Testing**: JUnit/Mockito (unit & integration tests)

---

## 🔐 Roles & Permissions 

| Role  |                       Description                               |
|-------|-----------------------------------------------------------------|
| Guest |Registered users who can book properties and leave reviews       |
| Host  |Registered users who can manage their own properties and bookings|
| Admin |Has full control over users, properties, bookings, and reviews   |

---

## 🧩 Core entities

### User

1. **Fields**: id, email, password, fullName, phoneNumber, status (ENUM based), statusDescription, role (and relation fields)
2. **Features**:
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

---

## ⚙️ API overview 

### 🔑 Authentication & Authorization

1. JWT two tokens based
2. CSRF-protected for enhanced security
3. Role-based endpoint restrictions
4. Rate limiting per IP/user to prevent abuse

### 🧪 Testing

*~60-70% tests coverage*

1. **Unit tests**: For most services and controllers
2. **Integration tests**: Written for most of sensitive methods. Separated via Maven profile integration.

### 🧾 Validation & Error Handling

1. DTO-based data transfer
2. Request validation with custom annotations
3. Centralized exception handling with custom exceptions

### 📊 Monitoring & Observability

1. Spring Actuator endpoints enabled
2. Integrated with Prometheus for metrics scraping

---

## ▶️ How to Run the Project

### ⚙️ Environment Configuration

The project uses environment variables for configuration (for PostgreSQL, Redis, and Amazon S3).  
A `.env.example` file is provided in the project root — **copy it to `.env` and fill in the required values** before running the application.

- If you use Docker, environment variables are injected automatically using the `.env` file and Docker Compose.
- For manual (local) execution, you'll need to either:
    - create a `.env` file in the root folder and use an env plugin (e.g., `envfile` for IntelliJ, or `direnv`), or
    - export the variables in your shell session before running the app.

**Note:**  
- Use the `*_DEV` variables when running locally with Maven.  
- Use the `*_PROD` variables when running via Docker Compose.

---

### 🛠 Manual Setup (Local Development)

**1. Copy `.env.example` to `.env` and fill out your local (DEV) values.**

**2. Start PostgreSQL**  
Make sure you have PostgreSQL running locally. Create a database (e.g., bookinline) and set your credentials via environment variables.

**3. Start Redis**  
Run Redis locally (default port 6379).  
- macOS: `brew install redis && brew services start redis`
- Linux: `sudo apt install redis-server && sudo service redis-server start`

**4. Run the App**  
Use Maven to build and run the application:
```
mvn clean install
mvn spring-boot:run
```
**Swagger UI** will be available at:  
`http://localhost:8080/swagger-ui/index.html`

---

### 🐳 Docker-based Setup

**1. Copy `.env.example` to `.env` and fill out your production (PROD) values.**

**2. Build the Docker Image**  
```
docker-compose build
```

**3. Run with Docker Compose**  
```
docker-compose up -d
```
This will:
 - Start the backend service
 - Launch Redis and PostgreSQL containers
 - Configure all environment variables automatically (using `.env`)

✅ **No need to modify application.properties for Docker — configs are injected via environment variables or mounted files.**

---

### 📚 Swagger API Docs

Swagger UI is enabled and available at:  
`http://localhost:8080/swagger-ui/index.html`  
It provides complete documentation for all endpoints, including security schemes, schemas, and example payloads.

---

### ✅ Future Improvements / TODOs

1. Host analytics dashboard.
2. Notifications (email/SMS).
3. Email verification on registration.

---
